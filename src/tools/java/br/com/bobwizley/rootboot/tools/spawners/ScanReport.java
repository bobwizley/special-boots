package br.com.bobwizley.rootboot.tools.spawners;

import java.util.List;

public record ScanReport(
        boolean complete,
        String minecraftVersion,
        int chunksScanned,
        int spawnersFound,
        int groupsFound,
        List<ScanError> errors,
        List<SpawnerGroup> groups) {
}
