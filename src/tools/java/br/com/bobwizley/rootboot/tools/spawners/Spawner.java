package br.com.bobwizley.rootboot.tools.spawners;

import java.util.List;

public record Spawner(BlockPosition position, List<String> mobTypes) {
    public Spawner {
        mobTypes = mobTypes.stream().distinct().sorted().toList();
    }
}
