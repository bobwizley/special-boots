package br.com.bobwizley.rootboot.tools.spawners;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class SpawnerGroupFinderTest {
    @Test
    void findsPairWithOptimalIntegerActivationPoint() {
        List<SpawnerGroup> groups = SpawnerGroupFinder.findGroups(List.of(
                spawner(0, 0, 0),
                spawner(20, 0, 0)));

        assertEquals(List.of(new SpawnerGroup(
                new BlockPosition(10, 0, 0),
                List.of(spawner(0, 0, 0), spawner(20, 0, 0)))), groups);
    }

    @Test
    void omitsSpawnersWithoutAnIntegerPointInsideEveryActivationRange() {
        assertEquals(List.of(), SpawnerGroupFinder.findGroups(List.of(
                spawner(0, 0, 0),
                spawner(32, 0, 0))));
    }

    @Test
    void doesNotInferACommonPointFromPairwiseProximity() {
        List<Spawner> spawners = List.of(
                spawner(0, 0, 0),
                spawner(30, 0, 0),
                spawner(15, 26, 0));

        List<SpawnerGroup> groups = SpawnerGroupFinder.findGroups(spawners);

        assertEquals(3, groups.size());
        assertEquals(List.of(2, 2, 2), groups.stream()
                .map(group -> group.spawners().size())
                .toList());
    }

    @Test
    void returnsDistinctOverlappingMaximalGroupsWithoutSubsets() {
        List<SpawnerGroup> groups = SpawnerGroupFinder.findGroups(List.of(
                spawner(0, 0, 0),
                spawner(10, 0, 0),
                spawner(20, 0, 0),
                spawner(30, 0, 0),
                spawner(40, 0, 0)));

        assertEquals(List.of(
                List.of(0, 10, 20, 30),
                List.of(10, 20, 30, 40)), groups.stream()
                .map(group -> group.spawners().stream()
                        .map(spawner -> spawner.position().x())
                        .toList())
                .toList());
    }

    @Test
    void breaksOptimalPointTiesByXThenYThenZ() {
        List<SpawnerGroup> groups = SpawnerGroupFinder.findGroups(List.of(
                spawner(0, 0, 0),
                spawner(0, 0, 1)));

        assertEquals(new BlockPosition(0, 0, 1), groups.getFirst().activationPoint());
    }

    private static Spawner spawner(int x, int y, int z) {
        return new Spawner(new BlockPosition(x, y, z), List.of("minecraft:pig"));
    }
}
