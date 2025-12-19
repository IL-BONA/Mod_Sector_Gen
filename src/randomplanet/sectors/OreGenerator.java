package randomplanet.sectors;

import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;

public class OreGenerator {

    private final Rand rand;
    private final Tiles tiles;
    private final int width, height;
    private final OrePatchGenerator patchGenerator;

    // Default ore configurations
    private static final OreConfig[] DEFAULT_ORE_CONFIGS = {
            new OreConfig(Blocks.oreCopper, 0.20f, 3, 12, 0.7f, 0.8f, 0.3f, 15, true,
                    Blocks.stone, Blocks.sand, Blocks.basalt),
            new OreConfig(Blocks.oreLead, 0.10f, 2, 8, 0.6f, 1.2f, 0.4f, 20, true,
                    Blocks.stone, Blocks.basalt),
            new OreConfig(Blocks.oreCoal, 0.10f, 4, 15, 0.8f, 0.6f, 0.25f, 12, false,
                    Blocks.stone, Blocks.sand),
            new OreConfig(Blocks.oreScrap, 0.10f, 1, 6, 0.5f, 1.5f, 0.5f, 25, true,
                    Blocks.stone, Blocks.basalt, Blocks.sand),
            new OreConfig(Blocks.oreTitanium, 0.10f, 2, 7, 0.4f, 2.0f, 0.6f, 30, true,
                    Blocks.stone, Blocks.basalt),
            new OreConfig(Blocks.oreThorium, 0.10f, 1, 4, 0.3f, 2.5f, 0.7f, 40, true,
                    Blocks.stone),
            new OreConfig(Blocks.oreBeryllium, 0.10f, 2, 6, 0.4f, 1.8f, 0.65f, 35, true,
                    Blocks.stone, Blocks.basalt),
            new OreConfig(Blocks.oreTungsten, 0.10f, 1, 3, 0.25f, 3.0f, 0.75f, 50, true,
                    Blocks.stone),
            new OreConfig(Blocks.oreCrystalThorium, 0.10f, 1, 2, 0.2f, 4.0f, 0.8f, 60, true,
                    Blocks.stone)
    };

    public OreGenerator(Tiles tiles, int seed) {
        this.tiles = tiles;
        this.width = tiles.width;
        this.height = tiles.height;
        this.rand = new Rand(seed);
        this.patchGenerator = new OrePatchGenerator(tiles);
    }

    /**
     * Generate ores using default configuration
     */
    public void generateOres(int seed) {
        generateOres(seed, DEFAULT_ORE_CONFIGS);
    }

    /**
     * Generate ores with custom configuration
     */
    public void generateOres(int seed, OreConfig... oreConfigs) {
        this.rand.setSeed(seed);

        for (OreConfig config : oreConfigs) {
            if (config.clusterMode) {
                generateClusteredOre(config, seed);
            } else {
                generateScatteredOre(config, seed);
            }
        }
    }

    /**
     * Genera minerali utilizzando un approccio basato su cluster (per minerali
     * rari/preziosi)
     */
    private void generateClusteredOre(OreConfig config, int baseSeed) {
        // Create a sequence to hold cluster centers
        Seq<Vec2> clusterCenters = new Seq<>();

        // Calculate number of clusters based on spawn chance and min distance
        int numClusters = (int) (width * height * config.spawnChance / (config.minDistance * config.minDistance));

        // Generate cluster centers
        for (int i = 0; i < numClusters; i++) {
            // Generate random coordinates for cluster center
            float x = rand.random(width);
            float y = rand.random(height);

            // Check if valid location (distance from other clusters)
            boolean validLocation = true;
            for (Vec2 center : clusterCenters) {
                if (center.dst(x, y) < config.minDistance) {
                    validLocation = false;
                    break;
                }
            }

            // If valid, add to list
            if (validLocation) {
                clusterCenters.add(new Vec2(x, y));
            }
        }

        // Generate ore patches around centers
        for (Vec2 center : clusterCenters) {
            generateOreCluster(config, center);
        }
    }

    /**
     * Generate scattered ore (for common ores)
     */
    private void generateScatteredOre(OreConfig config, int baseSeed) {
        // OreSeed used for Simplex noise consistency
        int oreSeed = baseSeed + config.oreType.id * 1000;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles.getn(x, y);

                if (!isValidFloorForOre(tile, config))
                    continue;
                if (tile.overlay() != Blocks.air)
                    continue; // Don't override existing overlays

                // Use noise for natural distribution (Uses oreSeed for coordinate consistency)
                float noise = Simplex.noise2d(oreSeed, (double) (x * config.noiseScale),
                        (double) (y * config.noiseScale), 0.6, 0.0, 0.0);
                float distanceNoise = Simplex.noise2d(oreSeed + 1000, (double) (x * config.noiseScale * 0.5f),
                        (double) (y * config.noiseScale * 0.5f), 0.4, 0.0, 0.0);

                float combinedNoise = (noise + distanceNoise * 0.3f) * config.density;

                if (combinedNoise > config.noiseThreshold) {
                    // Use deterministic rand for placement decision
                    if (rand.random(1f) < config.spawnChance * 0.5f) {
                        generateSmallOrePatch(config, x, y);
                    }
                }
            }
        }
    }

    /**
     * Generate a cluster of ore patches around a center point
     */
    private void generateOreCluster(OreConfig config, Vec2 center) {
        // No local random, use class rand
        int numPatches = rand.random(2, 4); // 2-4 patches
        float clusterRadius = config.maxPatchSize * 2.5f;

        for (int i = 0; i < numPatches; i++) {
            float angle = rand.random(6.28f); // 2π
            float distance = rand.random(clusterRadius);

            int patchX = (int) (center.x + Mathf.cos(angle) * distance);
            int patchY = (int) (center.y + Mathf.sin(angle) * distance);

            if (patchX >= 0 && patchX < width && patchY >= 0 && patchY < height) {
                int patchSize = rand.random(config.minPatchSize, config.maxPatchSize);
                generateOrePatch(config, patchX, patchY, patchSize);
            }
        }
    }

    /**
     * Generate small ore patches for scattered generation
     */
    private void generateSmallOrePatch(OreConfig config, int centerX, int centerY) {
        // No local random, use class rand
        int patchSize = rand.random(config.minPatchSize, Math.min(config.maxPatchSize, 6));
        generateOrePatch(config, centerX, centerY, patchSize);
    }

    /**
     * Generate a single ore patch using various shapes
     */
    private void generateOrePatch(OreConfig config, int centerX, int centerY, int size) {
        // Choose patch shape
        PatchShape shape = PatchShape.values()[rand.random(PatchShape.values().length - 1)];

        switch (shape) {
            case CIRCULAR:
                patchGenerator.generateCircularPatch(config, centerX, centerY, size, rand);
                break;
            case OVAL:
                patchGenerator.generateOvalPatch(config, centerX, centerY, size, rand);
                break;
            case IRREGULAR:
                patchGenerator.generateIrregularPatch(config, centerX, centerY, size, rand);
                break;
            case LINEAR:
                patchGenerator.generateLinearPatch(config, centerX, centerY, size, rand);
                break;
        }
    }

    private boolean isValidFloorForOre(Tile tile, OreConfig config) {
        if (tile == null)
            return false;

        Block floor = tile.floor();

        // Don't place on water or deep water unless specifically allowed
        if (floor == Blocks.water || floor == Blocks.deepwater ||
                floor == Blocks.taintedWater || floor == Blocks.deepTaintedWater ||
                floor.hasLiquids) {
            return false;
        }

        // Check if floor is in allowed list
        for (Block allowedFloor : config.allowedFloors) {
            if (floor == allowedFloor) {
                return true;
            }
        }

        return false;
    }

    /**
     * Generate ores with fine-tuned parameters for specific control
     */
    public void generateCustomOres(int seed, Block oreType, float spawnRate,
            int minPatchSize, int maxPatchSize, float density,
            float noiseScale, Block... allowedFloors) {

        OreConfig customConfig = new OreConfig(oreType, spawnRate, minPatchSize, maxPatchSize,
                density, noiseScale, 0.4f, 20, true, allowedFloors);

        generateOres(seed, customConfig);
    }

    /**
     * Clear all existing ore overlays
     */
    public void clearOres() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles.getn(x, y);
                if (tile.overlay().itemDrop != null) { // Is an ore
                    tile.setOverlay(Blocks.air);
                }
            }
        }
    }

    private enum PatchShape {
        CIRCULAR, OVAL, IRREGULAR, LINEAR
    }
}
