package br.com.bobwizley.rootboot.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class BetterTreesConfigurations {

    private static final List<Spec> SPECS = List.of(
            new Spec(
                    "acacia",
                    "acacia",
                    List.of(
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/radius", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "1"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:acacia_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "azalea_tree",
                    "azalea_tree",
                    List.of(
                            Patch.set("/config/below_trunk_provider/type", "\"minecraft:rule_based_state_provider\""),
                            Patch.remove("/config/below_trunk_provider/state"),
                            Patch.set("/config/below_trunk_provider/rules", "[{\"if_true\":{\"type\":\"minecraft:not\",\"predicate\":{\"type\":\"minecraft:matching_block_tag\",\"tag\":\"minecraft:cannot_replace_below_tree_trunk\"}},\"then\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:rooted_dirt\"}}}]"),
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:leave_vine\",\"probability\":0.25}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.remove("/config/foliage_placer/foliage_height"),
                            Patch.remove("/config/foliage_placer/leaf_placement_attempts"),
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/height", "2"),
                            Patch.set("/config/foliage_provider/entries", "[{\"weight\":5,\"data\":{\"Name\":\"minecraft:azalea_leaves\",\"Properties\":{\"persistent\":\"false\",\"distance\":\"7\"}}},{\"weight\":1,\"data\":{\"Name\":\"minecraft:flowering_azalea_leaves\",\"Properties\":{\"persistent\":\"false\",\"distance\":\"7\"}}}]"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "3"),
                            Patch.remove("/config/trunk_placer/bend_length"),
                            Patch.set("/config/trunk_placer/height_rand_a", "4"),
                            Patch.set("/config/trunk_placer/height_rand_b", "5"),
                            Patch.remove("/config/trunk_placer/min_height_for_leaves"),
                            Patch.set("/config/force_dirt", "true"))),
            new Spec(
                    "birch",
                    "birch",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "7"),
                            Patch.set("/config/trunk_placer/height_rand_b", "3"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:birch_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "birch_bees_0002",
                    "birch_bees_0002",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "7"),
                            Patch.set("/config/trunk_placer/height_rand_b", "3"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:birch_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "birch_bees_0002_leaf_litter",
                    "birch_bees_0002_leaf_litter",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:beehive\",\"probability\":0.002},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1}]},\"height\":2,\"radius\":4,\"tries\":96},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"4\"}},\"weight\":1}]},\"height\":2,\"radius\":2,\"tries\":150}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "7"),
                            Patch.set("/config/trunk_placer/height_rand_b", "3"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:birch_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "birch_bees_002",
                    "birch_bees_002",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "7"),
                            Patch.set("/config/trunk_placer/height_rand_b", "3"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:birch_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "birch_bees_005",
                    "birch_bees_005",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "7"),
                            Patch.set("/config/trunk_placer/height_rand_b", "3"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:birch_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "birch_leaf_litter",
                    "birch_leaf_litter",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1}]},\"height\":2,\"radius\":4,\"tries\":96},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"4\"}},\"weight\":1}]},\"height\":2,\"radius\":2,\"tries\":150}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "7"),
                            Patch.set("/config/trunk_placer/height_rand_b", "3"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:birch_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "cherry",
                    "cherry",
                    List.of(
                            Patch.set("/config/foliage_placer/corner_hole_chance", "1"),
                            Patch.set("/config/foliage_placer/hanging_leaves_chance", "0.2"),
                            Patch.set("/config/foliage_placer/hanging_leaves_extension_chance", "0.9"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.remove("/config/trunk_placer/branch_count"),
                            Patch.remove("/config/trunk_placer/branch_end_offset_from_top"),
                            Patch.remove("/config/trunk_placer/branch_horizontal_length"),
                            Patch.remove("/config/trunk_placer/branch_start_offset_from_top"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "cherry_bees_005",
                    "cherry_bees_005",
                    List.of(
                            Patch.set("/config/foliage_placer/corner_hole_chance", "1"),
                            Patch.set("/config/foliage_placer/hanging_leaves_chance", "0.2"),
                            Patch.set("/config/foliage_placer/hanging_leaves_extension_chance", "0.9"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.remove("/config/trunk_placer/branch_count"),
                            Patch.remove("/config/trunk_placer/branch_end_offset_from_top"),
                            Patch.remove("/config/trunk_placer/branch_horizontal_length"),
                            Patch.remove("/config/trunk_placer/branch_start_offset_from_top"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "dark_oak",
                    "dark_oak",
                    List.of(
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/radius", "1"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/type", "\"minecraft:two_layers_feature_size\""),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "20"),
                            Patch.set("/config/trunk_placer/height_rand_a", "5"),
                            Patch.set("/config/trunk_placer/height_rand_b", "6"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/root_placer", "{\"type\":\"minecraft:mangrove_root_placer\",\"root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:dark_oak_wood\"}},\"trunk_offset_y\":0,\"above_root_placement\":{\"above_root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:dark_oak_wood\"}},\"above_root_placement_chance\":0.5},\"mangrove_root_placement\":{\"max_root_width\":1,\"max_root_length\":20,\"random_skew_chance\":1,\"can_grow_through\":\"#minecraft:replaceable\",\"muddy_roots_in\":\"minecraft:mud\",\"muddy_roots_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:dark_oak_wood\"}}}}"))),
            new Spec(
                    "dark_oak_leaf_litter",
                    "dark_oak_leaf_litter",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1}]},\"height\":2,\"radius\":6,\"tries\":96},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"4\"}},\"weight\":1}]},\"height\":2,\"radius\":3,\"tries\":150},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"weight\":1,\"data\":{\"Name\":\"minecraft:brown_mushroom\"}},{\"weight\":1,\"data\":{\"Name\":\"minecraft:red_mushroom\"}}]},\"height\":2,\"radius\":5,\"tries\":32},{\"type\":\"minecraft:attached_to_logs\",\"probability\":0.3,\"block_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"weight\":1,\"data\":{\"Name\":\"minecraft:brown_mushroom\"}},{\"weight\":1,\"data\":{\"Name\":\"minecraft:red_mushroom\"}}]},\"directions\":[\"up\"]}]"),
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/radius", "1"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/type", "\"minecraft:two_layers_feature_size\""),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "20"),
                            Patch.set("/config/trunk_placer/height_rand_a", "5"),
                            Patch.set("/config/trunk_placer/height_rand_b", "6"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/root_placer", "{\"type\":\"minecraft:mangrove_root_placer\",\"root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:dark_oak_wood\"}},\"trunk_offset_y\":0,\"above_root_placement\":{\"above_root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:dark_oak_wood\"}},\"above_root_placement_chance\":0.5},\"mangrove_root_placement\":{\"max_root_width\":1,\"max_root_length\":20,\"random_skew_chance\":1,\"can_grow_through\":\"#minecraft:replaceable\",\"muddy_roots_in\":\"minecraft:mud\",\"muddy_roots_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:dark_oak_wood\"}}}}"))),
            new Spec(
                    "fancy_oak",
                    "fancy_oak",
                    List.of(
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.remove("/config/root_placer"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "fancy_oak_bees",
                    "fancy_oak_bees",
                    List.of(
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/base_height", "16"),
                            Patch.set("/config/trunk_placer/height_rand_a", "12"),
                            Patch.set("/config/trunk_placer/height_rand_b", "12"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/root_placer", "{\"type\":\"minecraft:mangrove_root_placer\",\"root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_wood\"}},\"trunk_offset_y\":0,\"above_root_placement\":{\"above_root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_wood\"}},\"above_root_placement_chance\":0.5},\"mangrove_root_placement\":{\"max_root_width\":1,\"max_root_length\":20,\"random_skew_chance\":1,\"can_grow_through\":\"#minecraft:replaceable\",\"muddy_roots_in\":\"minecraft:mud\",\"muddy_roots_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_wood\"}}}}"))),
            new Spec(
                    "fancy_oak_bees_0002",
                    "fancy_oak_bees",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:beehive\",\"probability\":0.002}]"),
                            Patch.remove("/config/root_placer"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "fancy_oak_bees_0002_leaf_litter",
                    "fancy_oak_bees_0002_leaf_litter",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:beehive\",\"probability\":0.002},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1}]},\"height\":2,\"radius\":4,\"tries\":96},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"4\"}},\"weight\":1}]},\"height\":2,\"radius\":2,\"tries\":150}]"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "fancy_oak_bees_002",
                    "fancy_oak_bees_002",
                    List.of(
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "fancy_oak_bees_005",
                    "fancy_oak_bees_005",
                    List.of(
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "fancy_oak_leaf_litter",
                    "fancy_oak_leaf_litter",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1}]},\"height\":2,\"radius\":4,\"tries\":96},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"4\"}},\"weight\":1}]},\"height\":2,\"radius\":2,\"tries\":150}]"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "jungle_bush",
                    "jungle_bush",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:trunk_vine\"},{\"type\":\"minecraft:leave_vine\",\"probability\":0.25}]"),
                            Patch.set("/config/foliage_placer/height", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:giant_trunk_placer\""),
                            Patch.set("/config/trunk_placer/height_rand_a", "3"),
                            Patch.set("/config/trunk_placer/height_rand_b", "5"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "jungle_tree",
                    "jungle_tree",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:cocoa\",\"probability\":0.2},{\"type\":\"minecraft:trunk_vine\"}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:acacia_foliage_placer\""),
                            Patch.remove("/config/foliage_placer/height"),
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/radius", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "15"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "1"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "jungle_tree_no_vine",
                    "jungle_tree_no_vine",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:acacia_foliage_placer\""),
                            Patch.remove("/config/foliage_placer/height"),
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/radius", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "15"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "1"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "mangrove",
                    "mangrove",
                    List.of(
                            Patch.set("/config/below_trunk_provider/rules", "[{\"if_true\":{\"type\":\"minecraft:not\",\"predicate\":{\"type\":\"minecraft:matching_block_tag\",\"tag\":\"minecraft:cannot_replace_below_tree_trunk\"}},\"then\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:mangrove_roots\"}}}]"),
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:leave_vine\",\"probability\":0.125},{\"type\":\"minecraft:attached_to_leaves\",\"probability\":0.14,\"exclusion_radius_xz\":1,\"exclusion_radius_y\":0,\"required_empty_blocks\":2,\"block_provider\":{\"type\":\"minecraft:randomized_int_state_provider\",\"property\":\"age\",\"values\":{\"type\":\"minecraft:uniform\",\"min_inclusive\":0,\"max_inclusive\":4},\"source\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:mangrove_propagule\",\"Properties\":{\"age\":\"0\",\"hanging\":\"true\",\"stage\":\"0\",\"waterlogged\":\"false\"}}}},\"directions\":[\"down\"]},{\"type\":\"minecraft:beehive\",\"probability\":0.01}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:acacia_foliage_placer\""),
                            Patch.remove("/config/foliage_placer/foliage_height"),
                            Patch.remove("/config/foliage_placer/leaf_placement_attempts"),
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/root_placer/mangrove_root_placement/max_root_width", "12"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "15"),
                            Patch.remove("/config/trunk_placer/can_grow_through"),
                            Patch.remove("/config/trunk_placer/extra_branch_length"),
                            Patch.remove("/config/trunk_placer/extra_branch_steps"),
                            Patch.set("/config/trunk_placer/height_rand_b", "1"),
                            Patch.remove("/config/trunk_placer/place_branch_per_log_probability"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "mega_jungle_tree",
                    "mega_jungle_tree",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:trunk_vine\"}]"),
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/radius", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "20"),
                            Patch.set("/config/trunk_placer/height_rand_a", "10"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/root_placer", "{\"type\":\"minecraft:mangrove_root_placer\",\"root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:jungle_wood\"}},\"trunk_offset_y\":0,\"above_root_placement\":{\"above_root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:jungle_wood\"}},\"above_root_placement_chance\":0.5},\"mangrove_root_placement\":{\"max_root_width\":1,\"max_root_length\":20,\"random_skew_chance\":1,\"can_grow_through\":\"#minecraft:replaceable\",\"muddy_roots_in\":\"minecraft:mud\",\"muddy_roots_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:jungle_wood\"}}}}"))),
            new Spec(
                    "mega_pine",
                    "mega_pine",
                    List.of(
                            Patch.set("/config/foliage_placer/crown_height/min_inclusive", "5"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/limit", "1"),
                            Patch.set("/config/trunk_placer/base_height", "26"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "mega_spruce",
                    "mega_spruce",
                    List.of(
                            Patch.set("/config/foliage_placer/crown_height", "24"),
                            Patch.set("/config/foliage_placer/offset", "2"),
                            Patch.set("/config/foliage_placer/radius", "1"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/limit", "1"),
                            Patch.set("/config/trunk_placer/base_height", "26"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "oak",
                    "oak",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "4"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "oak_bees_0002",
                    "oak_bees_002",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:beehive\",\"probability\":0.002}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "4"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "oak_bees_0002_leaf_litter",
                    "oak_bees_0002_leaf_litter",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:beehive\",\"probability\":0.002},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1}]},\"height\":2,\"radius\":4,\"tries\":96},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"4\"}},\"weight\":1}]},\"height\":2,\"radius\":2,\"tries\":150}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "4"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "oak_bees_002",
                    "oak_bees_002",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "4"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "oak_bees_005",
                    "oak_bees_005",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "4"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "oak_leaf_litter",
                    "oak_leaf_litter",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1}]},\"height\":2,\"radius\":4,\"tries\":96},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"4\"}},\"weight\":1}]},\"height\":2,\"radius\":2,\"tries\":150}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "4"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "10"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:oak_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "pale_oak",
                    "pale_oak",
                    List.of(
                            Patch.set("/config/below_trunk_provider/rules", "[{\"if_true\":{\"type\":\"minecraft:not\",\"predicate\":{\"type\":\"minecraft:matching_block_tag\",\"tag\":\"minecraft:cannot_replace_below_tree_trunk\"}},\"then\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}}}]"),
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:pale_moss\",\"ground_probability\":1,\"leaves_probability\":0.3,\"trunk_probability\":1},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1}]},\"height\":2,\"radius\":6,\"tries\":96},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"4\"}},\"weight\":1}]},\"height\":2,\"radius\":3,\"tries\":150}]"),
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/radius", "1"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/type", "\"minecraft:two_layers_feature_size\""),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "20"),
                            Patch.set("/config/trunk_placer/height_rand_a", "5"),
                            Patch.set("/config/trunk_placer/height_rand_b", "6"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/root_placer", "{\"type\":\"minecraft:mangrove_root_placer\",\"root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}},\"trunk_offset_y\":1,\"above_root_placement\":{\"above_root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}},\"above_root_placement_chance\":0.5},\"mangrove_root_placement\":{\"max_root_width\":1,\"max_root_length\":20,\"random_skew_chance\":0.5,\"can_grow_through\":\"#minecraft:replaceable\",\"muddy_roots_in\":\"minecraft:mud\",\"muddy_roots_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}}}}"))),
            new Spec(
                    "pale_oak_bonemeal",
                    "pale_oak_bonemeal",
                    List.of(
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/radius", "1"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/type", "\"minecraft:two_layers_feature_size\""),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "20"),
                            Patch.set("/config/trunk_placer/height_rand_a", "5"),
                            Patch.set("/config/trunk_placer/height_rand_b", "6"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/root_placer", "{\"type\":\"minecraft:mangrove_root_placer\",\"root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}},\"trunk_offset_y\":0,\"above_root_placement\":{\"above_root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}},\"above_root_placement_chance\":0.5},\"mangrove_root_placement\":{\"max_root_width\":1,\"max_root_length\":20,\"random_skew_chance\":1,\"can_grow_through\":\"#minecraft:replaceable\",\"muddy_roots_in\":\"minecraft:mud\",\"muddy_roots_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}}}}"))),
            new Spec(
                    "pale_oak_creaking",
                    "pale_oak_creaking",
                    List.of(
                            Patch.set("/config/below_trunk_provider/rules", "[{\"if_true\":{\"type\":\"minecraft:not\",\"predicate\":{\"type\":\"minecraft:matching_block_tag\",\"tag\":\"minecraft:cannot_replace_below_tree_trunk\"}},\"then\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}}}]"),
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:pale_moss\",\"ground_probability\":1,\"leaves_probability\":0.3,\"trunk_probability\":1},{\"type\":\"minecraft:creaking_heart\",\"probability\":1},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1}]},\"height\":2,\"radius\":6,\"tries\":96},{\"type\":\"minecraft:place_on_ground\",\"block_state_provider\":{\"type\":\"minecraft:weighted_state_provider\",\"entries\":[{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"1\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"2\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"3\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"north\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"east\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"south\",\"segment_amount\":\"4\"}},\"weight\":1},{\"data\":{\"Name\":\"minecraft:leaf_litter\",\"Properties\":{\"facing\":\"west\",\"segment_amount\":\"4\"}},\"weight\":1}]},\"height\":2,\"radius\":3,\"tries\":150}]"),
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/foliage_placer/radius", "1"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/type", "\"minecraft:two_layers_feature_size\""),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "20"),
                            Patch.set("/config/trunk_placer/height_rand_a", "5"),
                            Patch.set("/config/trunk_placer/height_rand_b", "6"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/root_placer", "{\"type\":\"minecraft:mangrove_root_placer\",\"root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}},\"trunk_offset_y\":1,\"above_root_placement\":{\"above_root_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}},\"above_root_placement_chance\":0},\"mangrove_root_placement\":{\"max_root_width\":1,\"max_root_length\":20,\"random_skew_chance\":0.5,\"can_grow_through\":\"#minecraft:replaceable\",\"muddy_roots_in\":\"minecraft:mud\",\"muddy_roots_provider\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:pale_oak_wood\"}}}}"))),
            new Spec(
                    "pine",
                    "pine",
                    List.of(
                            Patch.set("/config/foliage_placer/radius", "2"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/base_height", "15"),
                            Patch.set("/config/trunk_placer/height_rand_a", "8"),
                            Patch.set("/config/trunk_placer/height_rand_b", "1"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "spruce",
                    "spruce",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:mega_pine_foliage_placer\""),
                            Patch.set("/config/foliage_placer/offset", "2"),
                            Patch.set("/config/foliage_placer/radius", "1"),
                            Patch.remove("/config/foliage_placer/trunk_height"),
                            Patch.set("/config/foliage_placer/crown_height", "{\"type\":\"minecraft:uniform\",\"min_inclusive\":17,\"max_inclusive\":18}"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/trunk_placer/base_height", "15"),
                            Patch.set("/config/trunk_placer/height_rand_a", "5"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "super_birch_bees",
                    "super_birch_bees",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "13"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:birch_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "super_birch_bees_0002",
                    "super_birch_bees_0002",
                    List.of(
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "13"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:birch_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "super_birch_bees_05",
                    "super_birch_bees",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:beehive\",\"probability\":0.5}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "4"),
                            Patch.set("/config/foliage_placer/offset", "3"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:forking_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "13"),
                            Patch.set("/config/trunk_placer/height_rand_a", "1"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"),
                            Patch.set("/config/sapling_provider", "{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:birch_sapling\",\"Properties\":{\"stage\":\"0\"}}}"))),
            new Spec(
                    "swamp_oak",
                    "swamp_oak",
                    List.of(
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:trunk_vine\"}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:fancy_foliage_placer\""),
                            Patch.set("/config/foliage_placer/height", "2"),
                            Patch.set("/config/foliage_placer/offset", "2"),
                            Patch.remove("/config/foliage_provider/state/Properties/waterlogged"),
                            Patch.set("/config/ignore_vines", "true"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/height_rand_a", "10"),
                            Patch.set("/config/trunk_placer/height_rand_b", "10"),
                            Patch.set("/config/force_dirt", "false"))),
            new Spec(
                    "tall_mangrove",
                    "tall_mangrove",
                    List.of(
                            Patch.set("/config/below_trunk_provider/rules", "[{\"if_true\":{\"type\":\"minecraft:not\",\"predicate\":{\"type\":\"minecraft:matching_block_tag\",\"tag\":\"minecraft:cannot_replace_below_tree_trunk\"}},\"then\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:mangrove_roots\"}}}]"),
                            Patch.set("/config/decorators", "[{\"type\":\"minecraft:leave_vine\",\"probability\":0.125},{\"type\":\"minecraft:attached_to_leaves\",\"probability\":0.14,\"exclusion_radius_xz\":1,\"exclusion_radius_y\":0,\"required_empty_blocks\":2,\"block_provider\":{\"type\":\"minecraft:randomized_int_state_provider\",\"property\":\"age\",\"values\":{\"type\":\"minecraft:uniform\",\"min_inclusive\":0,\"max_inclusive\":4},\"source\":{\"type\":\"minecraft:simple_state_provider\",\"state\":{\"Name\":\"minecraft:mangrove_propagule\",\"Properties\":{\"age\":\"0\",\"hanging\":\"true\",\"stage\":\"0\",\"waterlogged\":\"false\"}}}},\"directions\":[\"down\"]},{\"type\":\"minecraft:beehive\",\"probability\":0.01}]"),
                            Patch.set("/config/foliage_placer/type", "\"minecraft:acacia_foliage_placer\""),
                            Patch.remove("/config/foliage_placer/foliage_height"),
                            Patch.remove("/config/foliage_placer/leaf_placement_attempts"),
                            Patch.set("/config/foliage_placer/offset", "1"),
                            Patch.set("/config/minimum_size/limit", "0"),
                            Patch.set("/config/minimum_size/upper_size", "0"),
                            Patch.set("/config/minimum_size/min_clipped_height", "4"),
                            Patch.set("/config/minimum_size/lower_size", "0"),
                            Patch.set("/config/root_placer/mangrove_root_placement/max_root_width", "12"),
                            Patch.set("/config/root_placer/trunk_offset_y/max_inclusive", "5"),
                            Patch.set("/config/root_placer/trunk_offset_y/min_inclusive", "1"),
                            Patch.set("/config/trunk_placer/type", "\"minecraft:fancy_trunk_placer\""),
                            Patch.set("/config/trunk_placer/base_height", "15"),
                            Patch.remove("/config/trunk_placer/can_grow_through"),
                            Patch.remove("/config/trunk_placer/extra_branch_length"),
                            Patch.remove("/config/trunk_placer/extra_branch_steps"),
                            Patch.set("/config/trunk_placer/height_rand_b", "1"),
                            Patch.remove("/config/trunk_placer/place_branch_per_log_probability"),
                            Patch.set("/config/force_dirt", "false"))));

    static final Set<String> IDS =
            SPECS.stream().map(Spec::id).collect(java.util.stream.Collectors.toUnmodifiableSet());

    private BetterTreesConfigurations() {
    }

    static Map<String, JsonObject> create() {
        Map<String, JsonObject> configurations = new LinkedHashMap<>();
        for (Spec spec : SPECS) {
            JsonObject json = readVanilla(spec.baseId());
            spec.patches().forEach(patch -> patch.apply(json));
            normalizeForMinecraft26_2(json);
            configurations.put(spec.id(), json);
        }
        return Map.copyOf(configurations);
    }

    private static void normalizeForMinecraft26_2(JsonElement element) {
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(BetterTreesConfigurations::normalizeForMinecraft26_2);
            return;
        }
        if (!element.isJsonObject()) {
            return;
        }
        JsonObject object = element.getAsJsonObject();
        JsonElement muddyRoots = object.get("muddy_roots_in");
        if (muddyRoots != null && !muddyRoots.isJsonArray()) {
            JsonArray values = new JsonArray();
            values.add(muddyRoots.deepCopy());
            object.add("muddy_roots_in", values);
        }
        object.entrySet().forEach(entry -> normalizeForMinecraft26_2(entry.getValue()));
    }

    private static JsonObject readVanilla(String id) {
        String resource = "data/minecraft/worldgen/configured_feature/" + id + ".json";
        try {
            List<URL> resources =
                    Collections.list(
                            BetterTreesConfigurations.class
                                    .getClassLoader()
                                    .getResources(resource));
            URL vanilla =
                    resources.stream()
                            .filter(
                                    url ->
                                            url.getProtocol().equals("jar")
                                                    && url.toString().contains("minecraft-common"))
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "missing vanilla configured feature " + id));
            try (InputStream input = vanilla.openStream()) {
                return JsonParser.parseString(
                                new String(input.readAllBytes(), StandardCharsets.UTF_8))
                        .getAsJsonObject();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("could not read vanilla configured feature " + id, exception);
        }
    }

    private record Spec(String id, String baseId, List<Patch> patches) {
    }

    private record Patch(String pointer, JsonElement value, boolean remove) {

        static Patch set(String pointer, String json) {
            return new Patch(pointer, JsonParser.parseString(json), false);
        }

        static Patch remove(String pointer) {
            return new Patch(pointer, null, true);
        }

        void apply(JsonObject root) {
            String[] parts = pointer.substring(1).split("/");
            JsonObject parent = root;
            for (int index = 0; index < parts.length - 1; index++) {
                parent = parent.getAsJsonObject(parts[index]);
            }
            String key = parts[parts.length - 1];
            if (remove) {
                parent.remove(key);
                return;
            }
            parent.add(key, value.deepCopy());
        }
    }
}
