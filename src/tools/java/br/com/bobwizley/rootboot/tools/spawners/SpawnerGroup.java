package br.com.bobwizley.rootboot.tools.spawners;

import java.util.List;

public record SpawnerGroup(BlockPosition activationPoint, List<Spawner> spawners) {
}
