package br.com.bobwizley.rootboot.tools.spawners;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;

final class WorldScanner {
    private static final Pattern REGION_FILE = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.mca");
    private static final Path OVERWORLD_REGION_PATH = Path.of("dimensions/minecraft/overworld/region");
    private static final RegionStorageInfo REGION_INFO = new RegionStorageInfo(
            "spawner-scanner", Level.OVERWORLD, "chunk");

    ScanReport scan(Path world, String minecraftVersion) throws WorldScanException {
        Path normalizedWorld = validateWorld(world, minecraftVersion);
        Path regionDirectory = normalizedWorld.resolve(OVERWORLD_REGION_PATH);
        List<Spawner> spawners = new ArrayList<>();
        List<ScanError> errors = new ArrayList<>();
        int chunksScanned = 0;

        try (WorldLock ignored = acquireWorldLock(normalizedWorld.resolve("session.lock"));
                var paths = Files.list(regionDirectory)) {
            for (Path region : paths
                    .filter(path -> REGION_FILE.matcher(path.getFileName().toString()).matches())
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList()) {
                chunksScanned += scanRegion(region, spawners, errors);
            }
        } catch (IOException exception) {
            throw new WorldScanException("Could not list Overworld region files.", exception);
        }

        List<SpawnerGroup> groups = SpawnerGroupFinder.findGroups(spawners);
        return new ScanReport(
                errors.isEmpty(),
                minecraftVersion,
                chunksScanned,
                spawners.size(),
                groups.size(),
                List.copyOf(errors),
                groups);
    }

    private Path validateWorld(Path world, String minecraftVersion) throws WorldScanException {
        Path normalized = world.toAbsolutePath().normalize();
        if (!Files.isDirectory(normalized)) {
            throw new WorldScanException("World path is not a directory: " + normalized);
        }
        Path levelData = normalized.resolve("level.dat");
        Path regionDirectory = normalized.resolve(OVERWORLD_REGION_PATH);
        if (!Files.isRegularFile(levelData) || !Files.isDirectory(regionDirectory)) {
            throw new WorldScanException("Path is not a valid Overworld save: " + normalized);
        }
        try {
            CompoundTag root = NbtIo.readCompressed(levelData, NbtAccounter.defaultQuota());
            CompoundTag data = root.getCompound("Data")
                    .orElseThrow(() -> new WorldScanException("level.dat has no Data compound."));
            String saveVersion = data.getCompound("Version")
                    .flatMap(version -> version.getString("Name"))
                    .orElseThrow(() -> new WorldScanException("level.dat has no Minecraft version."));
            if (!minecraftVersion.equals(saveVersion)) {
                throw new WorldScanException(
                        "Incompatible world version: expected " + minecraftVersion + ", found " + saveVersion + ".");
            }
        } catch (IOException exception) {
            throw new WorldScanException("Could not read level.dat.", exception);
        }
        return normalized;
    }

    private WorldLock acquireWorldLock(Path lockPath) throws WorldScanException {
        if (!Files.exists(lockPath)) {
            return WorldLock.absent();
        }
        FileChannel channel = null;
        try {
            channel = FileChannel.open(lockPath, StandardOpenOption.WRITE);
            FileLock lock = channel.tryLock();
            if (lock == null) {
                channel.close();
                throw new WorldScanException("World is currently in use.");
            }
            return new WorldLock(channel, lock);
        } catch (OverlappingFileLockException exception) {
            closeAfterLockFailure(channel, exception);
            throw new WorldScanException("World is currently in use.", exception);
        } catch (IOException exception) {
            closeAfterLockFailure(channel, exception);
            throw new WorldScanException("Could not verify the world session lock.", exception);
        }
    }

    private void closeAfterLockFailure(FileChannel channel, Exception failure) {
        if (channel == null) {
            return;
        }
        try {
            channel.close();
        } catch (IOException closeFailure) {
            failure.addSuppressed(closeFailure);
        }
    }

    private int scanRegion(Path path, List<Spawner> spawners, List<ScanError> errors) {
        String fileName = path.getFileName().toString();
        String[] coordinates = fileName.substring(2, fileName.length() - 4).split("\\.");
        int regionX = Integer.parseInt(coordinates[0]);
        int regionZ = Integer.parseInt(coordinates[1]);
        int chunksScanned = 0;
        try {
            long size = Files.size(path);
            if (size == 0) {
                return 0;
            }
            if (size < 8192) {
                errors.add(ScanError.region(fileName, "Region file has an invalid Anvil header."));
                return 0;
            }
        } catch (IOException exception) {
            errors.add(ScanError.region(fileName, message(exception)));
            return 0;
        }
        try (RegionFile region = new RegionFile(REGION_INFO, path, path.getParent(), false)) {
            for (int localZ = 0; localZ < 32; localZ++) {
                for (int localX = 0; localX < 32; localX++) {
                    ChunkPos chunk = new ChunkPos(regionX * 32 + localX, regionZ * 32 + localZ);
                    if (!region.hasChunk(chunk)) {
                        continue;
                    }
                    chunksScanned++;
                    try (DataInputStream input = region.getChunkDataInputStream(chunk)) {
                        if (input == null) {
                            errors.add(ScanError.chunk(fileName, chunk.x(), chunk.z(), "Chunk data is missing."));
                            continue;
                        }
                        CompoundTag chunkData = NbtIo.read(input);
                        readSpawners(fileName, chunk, chunkData, spawners, errors);
                    } catch (Exception exception) {
                        errors.add(ScanError.chunk(fileName, chunk.x(), chunk.z(), message(exception)));
                    }
                }
            }
        } catch (Exception exception) {
            errors.add(ScanError.region(fileName, message(exception)));
        }
        return chunksScanned;
    }

    private void readSpawners(
            String region,
            ChunkPos chunk,
            CompoundTag chunkData,
            List<Spawner> spawners,
            List<ScanError> errors) {
        ListTag blockEntities = chunkData.getList("block_entities").orElseGet(ListTag::new);
        for (CompoundTag blockEntity : blockEntities.compoundStream().toList()) {
            if (!"minecraft:mob_spawner".equals(blockEntity.getStringOr("id", ""))) {
                continue;
            }
            if (blockEntity.getInt("x").isEmpty()
                    || blockEntity.getInt("y").isEmpty()
                    || blockEntity.getInt("z").isEmpty()) {
                errors.add(ScanError.chunk(
                        region, chunk.x(), chunk.z(), "Spawner position is missing or malformed."));
                continue;
            }
            BlockPosition position = new BlockPosition(
                    blockEntity.getInt("x").orElseThrow(),
                    blockEntity.getInt("y").orElseThrow(),
                    blockEntity.getInt("z").orElseThrow());
            MobTypes mobTypes = readMobTypes(blockEntity);
            spawners.add(new Spawner(position, mobTypes.values()));
            if (mobTypes.malformed()) {
                errors.add(ScanError.spawner(
                        region, chunk.x(), chunk.z(), position, "Spawner mob configuration is missing or malformed."));
            }
        }
    }

    private MobTypes readMobTypes(CompoundTag spawner) {
        TreeSet<String> values = new TreeSet<>();
        boolean malformed = false;

        if (spawner.contains("SpawnData")) {
            malformed |= !readSpawnData(spawner.get("SpawnData"), values);
        } else {
            malformed = true;
        }

        if (spawner.contains("SpawnPotentials")) {
            if (spawner.get("SpawnPotentials") instanceof ListTag potentials) {
                for (var potential : potentials) {
                    if (!(potential instanceof CompoundTag entry)
                            || !readSpawnData(entry.get("data"), values)) {
                        malformed = true;
                    }
                }
            } else {
                malformed = true;
            }
        }
        boolean unreadable = malformed || values.isEmpty();
        return new MobTypes(unreadable ? List.of() : List.copyOf(values), unreadable);
    }

    private boolean readSpawnData(Object value, TreeSet<String> mobTypes) {
        if (!(value instanceof CompoundTag spawnData)
                || !(spawnData.get("entity") instanceof CompoundTag entity)) {
            return false;
        }
        String id = entity.getStringOr("id", "");
        if (id.isBlank() || !id.contains(":")) {
            return false;
        }
        mobTypes.add(id);
        return true;
    }

    private static String message(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
    }

    private record MobTypes(List<String> values, boolean malformed) {
    }

    private record WorldLock(FileChannel channel, FileLock lock) implements AutoCloseable {
        private static WorldLock absent() {
            return new WorldLock(null, null);
        }

        @Override
        public void close() throws IOException {
            if (lock != null) {
                try {
                    lock.close();
                } finally {
                    channel.close();
                }
            }
        }
    }
}
