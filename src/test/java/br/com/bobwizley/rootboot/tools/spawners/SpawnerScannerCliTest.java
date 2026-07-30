package br.com.bobwizley.rootboot.tools.spawners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SpawnerScannerCliTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void rejectsAnInvalidPathWithoutWritingJson() {
        Invocation invocation = invoke(temporaryDirectory.resolve("missing").toString());

        assertEquals(1, invocation.exitCode());
        assertEquals("", invocation.stdout());
        assertTrue(invocation.stderr().contains("not a directory"));
    }

    @Test
    void reportsACompleteWorldWithoutGroupsAsCompactJson() throws Exception {
        Path world = createWorld(currentVersion());

        Invocation invocation = invoke(world.toString());

        assertEquals(0, invocation.exitCode());
        assertFalse(invocation.stdout().contains("\n "));
        assertEquals("", invocation.stderr());
        JsonObject report = JsonParser.parseString(invocation.stdout()).getAsJsonObject();
        assertTrue(report.get("complete").getAsBoolean());
        assertEquals(currentVersion(), report.get("minecraft_version").getAsString());
        assertEquals(0, report.get("chunks_scanned").getAsInt());
        assertEquals(0, report.get("spawners_found").getAsInt());
        assertEquals(0, report.get("groups_found").getAsInt());
        assertEquals(0, report.getAsJsonArray("errors").size());
        assertEquals(0, report.getAsJsonArray("groups").size());
    }

    @Test
    void rejectsAnOpenWorld() throws Exception {
        Path world = createWorld(currentVersion());
        Path lockPath = world.resolve("session.lock");
        Files.write(lockPath, new byte[] {0});

        try (FileChannel channel = FileChannel.open(lockPath, StandardOpenOption.WRITE);
                FileLock ignored = channel.lock()) {
            Invocation invocation = invoke(world.toString());

            assertEquals(1, invocation.exitCode());
            assertEquals("", invocation.stdout());
            assertTrue(invocation.stderr().contains("currently in use"));
        }
    }

    @Test
    void rejectsAWorldFromAnotherMinecraftVersion() throws Exception {
        Path world = createWorld("different-version");

        Invocation invocation = invoke(world.toString());

        assertEquals(1, invocation.exitCode());
        assertEquals("", invocation.stdout());
        assertTrue(invocation.stderr().contains("Incompatible world version"));
    }

    @Test
    void countsIsolatedClassicSpawnersAndIgnoresTrialSpawners() throws Exception {
        Path world = createWorld(currentVersion());
        CompoundTag trialSpawner = spawner(10, 64, 0, "minecraft:breeze", List.of());
        trialSpawner.putString("id", "minecraft:trial_spawner");
        writeChunk(world, List.of(
                spawner(0, 64, 0, "minecraft:zombie", List.of()),
                trialSpawner));

        Invocation invocation = invoke(world.toString());

        assertEquals(0, invocation.exitCode());
        JsonObject report = JsonParser.parseString(invocation.stdout()).getAsJsonObject();
        assertEquals(1, report.get("spawners_found").getAsInt());
        assertEquals(0, report.get("groups_found").getAsInt());
        assertEquals(0, report.getAsJsonArray("groups").size());
    }

    @Test
    void reportsUnreadableRegionsAsPartialWithoutDiscardingJson() throws Exception {
        Path world = createWorld(currentVersion());
        Files.write(
                world.resolve("dimensions/minecraft/overworld/region/r.0.0.mca"),
                new byte[] {1, 2, 3});

        Invocation invocation = invoke(world.toString());

        assertEquals(2, invocation.exitCode());
        assertTrue(invocation.stderr().contains("region"));
        JsonObject report = JsonParser.parseString(invocation.stdout()).getAsJsonObject();
        assertFalse(report.get("complete").getAsBoolean());
        assertEquals("region", report.getAsJsonArray("errors")
                .get(0).getAsJsonObject()
                .get("scope").getAsString());
    }

    @Test
    void ignoresEmptyRegionPlaceholders() throws Exception {
        Path world = createWorld(currentVersion());
        Files.createFile(world.resolve("dimensions/minecraft/overworld/region/r.0.0.mca"));

        Invocation invocation = invoke(world.toString());

        assertEquals(0, invocation.exitCode());
        assertTrue(JsonParser.parseString(invocation.stdout()).getAsJsonObject()
                .get("complete").getAsBoolean());
    }

    @Test
    void reportsUnreadableChunksAsPartial() throws Exception {
        Path world = createWorld(currentVersion());
        writeChunk(world, List.of(spawner(0, 64, 0, "minecraft:zombie", List.of())));
        Path region = world.resolve("dimensions/minecraft/overworld/region/r.0.0.mca");
        int location;
        try (DataInputStream input = new DataInputStream(Files.newInputStream(region))) {
            location = input.readInt();
        }
        int sectorOffset = location >>> 8;
        try (FileChannel channel = FileChannel.open(region, StandardOpenOption.WRITE)) {
            channel.position((long) sectorOffset * 4096 + Integer.BYTES);
            channel.write(ByteBuffer.wrap(new byte[] {99}));
        }

        Invocation invocation = invoke(world.toString());

        assertEquals(2, invocation.exitCode());
        JsonObject report = JsonParser.parseString(invocation.stdout()).getAsJsonObject();
        assertFalse(report.get("complete").getAsBoolean());
        assertEquals("chunk", report.getAsJsonArray("errors")
                .get(0).getAsJsonObject()
                .get("scope").getAsString());
    }

    @Test
    void scansSpawnerGroupsAndConsolidatesMobTypes() throws Exception {
        Path world = createWorld(currentVersion());
        writeChunk(world, List.of(
                spawner(0, 64, 0, "minecraft:zombie", List.of("minecraft:skeleton", "minecraft:zombie")),
                spawner(20, 64, 0, "minecraft:spider", List.of())));

        Invocation invocation = invoke(world.toString());

        assertEquals(0, invocation.exitCode());
        JsonObject report = JsonParser.parseString(invocation.stdout()).getAsJsonObject();
        assertEquals(1, report.get("chunks_scanned").getAsInt());
        assertEquals(2, report.get("spawners_found").getAsInt());
        assertEquals(1, report.get("groups_found").getAsInt());
        var group = report.getAsJsonArray("groups").get(0).getAsJsonObject();
        assertFalse(group.has("id"));
        assertFalse(group.has("size"));
        var firstSpawner = group.getAsJsonArray("spawners").get(0).getAsJsonObject();
        assertEquals(
                List.of("minecraft:skeleton", "minecraft:zombie"),
                firstSpawner.getAsJsonArray("mob_types").asList().stream()
                        .map(element -> element.getAsString())
                        .toList());
    }

    @Test
    void keepsMalformedSpawnersInGeometryAndReportsPartialResults() throws Exception {
        Path world = createWorld(currentVersion());
        CompoundTag malformed = spawner(0, 64, 0, "minecraft:zombie", List.of());
        malformed.remove("SpawnData");
        writeChunk(world, List.of(
                malformed,
                spawner(20, 64, 0, "minecraft:skeleton", List.of())));

        Invocation invocation = invoke(world.toString());

        assertEquals(2, invocation.exitCode());
        assertTrue(invocation.stderr().contains("Spawner mob configuration"));
        JsonObject report = JsonParser.parseString(invocation.stdout()).getAsJsonObject();
        assertFalse(report.get("complete").getAsBoolean());
        assertEquals(2, report.get("spawners_found").getAsInt());
        assertEquals(1, report.get("groups_found").getAsInt());
        assertEquals(1, report.getAsJsonArray("errors").size());
        assertEquals(0, report.getAsJsonArray("groups")
                .get(0).getAsJsonObject()
                .getAsJsonArray("spawners")
                .get(0).getAsJsonObject()
                .getAsJsonArray("mob_types").size());
    }

    @Test
    void doesNotPublishPartialMobTypesFromMalformedConfiguration() throws Exception {
        Path world = createWorld(currentVersion());
        CompoundTag partiallyMalformed = spawner(0, 64, 0, "minecraft:zombie", List.of());
        ListTag potentials = partiallyMalformed.getListOrEmpty("SpawnPotentials");
        potentials.add(new CompoundTag());
        writeChunk(world, List.of(
                partiallyMalformed,
                spawner(20, 64, 0, "minecraft:skeleton", List.of())));

        Invocation invocation = invoke(world.toString());

        assertEquals(2, invocation.exitCode());
        JsonObject firstSpawner = JsonParser.parseString(invocation.stdout()).getAsJsonObject()
                .getAsJsonArray("groups").get(0).getAsJsonObject()
                .getAsJsonArray("spawners").get(0).getAsJsonObject();
        assertEquals(0, firstSpawner.getAsJsonArray("mob_types").size());
    }

    @Test
    void excludesSpawnersWithoutReadableCoordinates() throws Exception {
        Path world = createWorld(currentVersion());
        CompoundTag missingPosition = spawner(0, 64, 0, "minecraft:zombie", List.of());
        missingPosition.remove("y");
        writeChunk(world, List.of(
                missingPosition,
                spawner(20, 64, 0, "minecraft:skeleton", List.of())));

        Invocation invocation = invoke(world.toString());

        assertEquals(2, invocation.exitCode());
        JsonObject report = JsonParser.parseString(invocation.stdout()).getAsJsonObject();
        assertEquals(1, report.get("spawners_found").getAsInt());
        assertEquals(0, report.get("groups_found").getAsInt());
    }

    private Path createWorld(String version) throws Exception {
        Path world = Files.createDirectory(temporaryDirectory.resolve("world-" + Math.abs(version.hashCode())));
        Files.createDirectories(world.resolve("dimensions/minecraft/overworld/region"));
        CompoundTag versionTag = new CompoundTag();
        versionTag.putString("Name", version);
        CompoundTag data = new CompoundTag();
        data.put("Version", versionTag);
        CompoundTag root = new CompoundTag();
        root.put("Data", data);
        NbtIo.writeCompressed(root, world.resolve("level.dat"));
        return world;
    }

    private void writeChunk(Path world, List<CompoundTag> spawners) throws Exception {
        Bootstrap.bootStrap();
        Path regionPath = world.resolve("dimensions/minecraft/overworld/region/r.0.0.mca");
        RegionStorageInfo info = new RegionStorageInfo("test", Level.OVERWORLD, "chunk");
        try (RegionFile region = new RegionFile(info, regionPath, regionPath.getParent(), false);
                DataOutputStream output = region.getChunkDataOutputStream(new ChunkPos(0, 0))) {
            ListTag blockEntities = new ListTag();
            spawners.forEach(blockEntities::add);
            CompoundTag chunk = new CompoundTag();
            chunk.put("block_entities", blockEntities);
            NbtIo.write(chunk, output);
        }
    }

    private CompoundTag spawner(int x, int y, int z, String currentType, List<String> potentialTypes) {
        CompoundTag spawner = new CompoundTag();
        spawner.putString("id", "minecraft:mob_spawner");
        spawner.putInt("x", x);
        spawner.putInt("y", y);
        spawner.putInt("z", z);
        spawner.put("SpawnData", spawnData(currentType));
        ListTag potentials = new ListTag();
        for (String type : potentialTypes) {
            CompoundTag potential = new CompoundTag();
            potential.put("data", spawnData(type));
            potential.putInt("weight", 1);
            potentials.add(potential);
        }
        spawner.put("SpawnPotentials", potentials);
        return spawner;
    }

    private CompoundTag spawnData(String type) {
        CompoundTag entity = new CompoundTag();
        entity.putString("id", type);
        CompoundTag spawnData = new CompoundTag();
        spawnData.put("entity", entity);
        return spawnData;
    }

    private Invocation invoke(String... arguments) {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        int exitCode = SpawnerScannerCli.run(
                arguments,
                new PrintStream(stdout, true, StandardCharsets.UTF_8),
                new PrintStream(stderr, true, StandardCharsets.UTF_8));
        return new Invocation(
                exitCode,
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8));
    }

    private String currentVersion() {
        SharedConstants.tryDetectVersion();
        return SharedConstants.getCurrentVersion().name();
    }

    private record Invocation(int exitCode, String stdout, String stderr) {
    }
}
