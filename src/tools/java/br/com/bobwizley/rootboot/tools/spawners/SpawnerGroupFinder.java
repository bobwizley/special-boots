package br.com.bobwizley.rootboot.tools.spawners;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SpawnerGroupFinder {
    private static final int ACTIVATION_RANGE_SQUARED_DOUBLED = 32 * 32;

    private SpawnerGroupFinder() {
    }

    public static List<SpawnerGroup> findGroups(List<Spawner> input) {
        List<Spawner> spawners = input.stream()
                .sorted(Comparator.comparing(Spawner::position))
                .toList();
        Map<Cell, List<Spawner>> spatialIndex = index(spawners);
        Map<List<Spawner>, BestPoint> candidates = new HashMap<>();

        for (Spawner seed : spawners) {
            BlockPosition position = seed.position();
            for (int x = position.x() - 15; x <= position.x() + 16; x++) {
                for (int y = position.y() - 15; y <= position.y() + 16; y++) {
                    for (int z = position.z() - 15; z <= position.z() + 16; z++) {
                        BlockPosition point = new BlockPosition(x, y, z);
                        if (!activates(point, seed)) {
                            continue;
                        }
                        List<Spawner> active = activeSpawners(point, spatialIndex);
                        if (active.size() < 2) {
                            continue;
                        }
                        int maximumDistance = active.stream()
                                .mapToInt(spawner -> distanceSquaredDoubled(point, spawner))
                                .max()
                                .orElseThrow();
                        BestPoint best = new BestPoint(point, maximumDistance);
                        candidates.merge(active, best, BestPoint::better);
                    }
                }
            }
        }

        Set<List<Spawner>> maximal = new HashSet<>(candidates.keySet());
        maximal.removeIf(group -> candidates.keySet().stream()
                .anyMatch(other -> other.size() > group.size() && other.containsAll(group)));

        return maximal.stream()
                .map(group -> new SpawnerGroup(candidates.get(group).point(), group))
                .sorted(Comparator
                        .comparingInt((SpawnerGroup group) -> group.spawners().size()).reversed()
                        .thenComparing(SpawnerGroupFinder::compareSpawnerPositions))
                .toList();
    }

    private static Map<Cell, List<Spawner>> index(List<Spawner> spawners) {
        Map<Cell, List<Spawner>> index = new HashMap<>();
        for (Spawner spawner : spawners) {
            index.computeIfAbsent(Cell.of(spawner.position()), ignored -> new ArrayList<>()).add(spawner);
        }
        return index;
    }

    private static List<Spawner> activeSpawners(BlockPosition point, Map<Cell, List<Spawner>> index) {
        List<Spawner> active = new ArrayList<>();
        int minimumCellX = Math.floorDiv(point.x() - 16, 16);
        int maximumCellX = Math.floorDiv(point.x() + 15, 16);
        int minimumCellY = Math.floorDiv(point.y() - 16, 16);
        int maximumCellY = Math.floorDiv(point.y() + 15, 16);
        int minimumCellZ = Math.floorDiv(point.z() - 16, 16);
        int maximumCellZ = Math.floorDiv(point.z() + 15, 16);
        for (int x = minimumCellX; x <= maximumCellX; x++) {
            for (int y = minimumCellY; y <= maximumCellY; y++) {
                for (int z = minimumCellZ; z <= maximumCellZ; z++) {
                    for (Spawner spawner : index.getOrDefault(new Cell(x, y, z), List.of())) {
                        if (activates(point, spawner)) {
                            active.add(spawner);
                        }
                    }
                }
            }
        }
        active.sort(Comparator.comparing(Spawner::position));
        return List.copyOf(active);
    }

    private static boolean activates(BlockPosition point, Spawner spawner) {
        return distanceSquaredDoubled(point, spawner) < ACTIVATION_RANGE_SQUARED_DOUBLED;
    }

    private static int distanceSquaredDoubled(BlockPosition point, Spawner spawner) {
        BlockPosition position = spawner.position();
        int x = 2 * point.x() - (2 * position.x() + 1);
        int y = 2 * point.y() - (2 * position.y() + 1);
        int z = 2 * point.z() - (2 * position.z() + 1);
        return x * x + y * y + z * z;
    }

    private static int compareSpawnerPositions(SpawnerGroup left, SpawnerGroup right) {
        for (int index = 0; index < left.spawners().size(); index++) {
            int comparison = left.spawners().get(index).position()
                    .compareTo(right.spawners().get(index).position());
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

    private record Cell(int x, int y, int z) {
        private static Cell of(BlockPosition position) {
            return new Cell(
                    Math.floorDiv(position.x(), 16),
                    Math.floorDiv(position.y(), 16),
                    Math.floorDiv(position.z(), 16));
        }
    }

    private record BestPoint(BlockPosition point, int maximumDistance) {
        private BestPoint better(BestPoint other) {
            if (maximumDistance != other.maximumDistance) {
                return maximumDistance < other.maximumDistance ? this : other;
            }
            return point.compareTo(other.point) <= 0 ? this : other;
        }
    }
}
