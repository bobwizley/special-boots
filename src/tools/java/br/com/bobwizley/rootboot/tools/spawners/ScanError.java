package br.com.bobwizley.rootboot.tools.spawners;

public record ScanError(
        String scope,
        String region,
        ChunkPosition chunk,
        BlockPosition spawner,
        String message) {
    static ScanError region(String region, String message) {
        return new ScanError("region", region, null, null, message);
    }

    static ScanError chunk(String region, int x, int z, String message) {
        return new ScanError("chunk", region, new ChunkPosition(x, z), null, message);
    }

    static ScanError spawner(String region, int chunkX, int chunkZ, BlockPosition position, String message) {
        return new ScanError("spawner", region, new ChunkPosition(chunkX, chunkZ), position, message);
    }
}
