package mod.planets;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;

import java.util.Random;

public class OreGenerator {
    
    private final Random random;
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
        this.random = new Random(seed);
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
        this.random.setSeed(seed);
        
        for (OreConfig config : oreConfigs) {
            if (config.clusterMode) {
                generateClusteredOre(config, seed);
            } else {
                generateScatteredOre(config, seed);
            }
        }
    }
    
    /**
     * Genera minerali utilizzando un approccio basato su cluster (per minerali rari/preziosi)
     */
    private void generateClusteredOre(OreConfig config, int baseSeed) {
        // Calcola il seme per la generazione dei minerali
        int oreSeed = baseSeed + config.oreType.id * 1000;
        
        // Crea una sequenza per contenere i centri dei cluster
        Seq<Vec2> clusterCenters = new Seq<>();
        
        // Calcola il numero di cluster da generare basandosi sulla probabilità di spawn e sulla distanza minima tra i cluster
        int numClusters = (int)(width * height * config.spawnChance / (config.minDistance * config.minDistance));
        
        // Genera i centri dei cluster
        for (int i = 0; i < numClusters; i++) {
            // Genera coordinate casuali per il centro del cluster
            float x = random.nextFloat() * width;
            float y = random.nextFloat() * height;
            
            // Controlla se la posizione generata è a una distanza minima sufficiente dagli altri cluster
            boolean validLocation = true;
            for (Vec2 center : clusterCenters) {
                if (center.dst(x, y) < config.minDistance) {
                    validLocation = false;
                    break;
                }
            }
            
            // Se la posizione è valida, aggiunge il centro del cluster alla sequenza
            if (validLocation) {
                clusterCenters.add(new Vec2(x, y));
            }
        }
        
        // Genera patch di minerali attorno ai centri dei cluster
        for (Vec2 center : clusterCenters) {
            generateOreCluster(config, center, oreSeed);
        }
    }
    
    /**
     * Generate scattered ore (for common ores)
     */
    private void generateScatteredOre(OreConfig config, int baseSeed) {
        int oreSeed = baseSeed + config.oreType.id * 1000;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles.getn(x, y);
                
                if (!isValidFloorForOre(tile, config)) continue;
                if (tile.overlay() != Blocks.air) continue; // Don't override existing overlays
                
                // Use noise for natural distribution
                float noise = Simplex.noise2d(oreSeed, (double)(x * config.noiseScale), (double)(y * config.noiseScale), 0.6, 0.0, 0.0);
                float distanceNoise = Simplex.noise2d(oreSeed + 1000, (double)(x * config.noiseScale * 0.5f), (double)(y * config.noiseScale * 0.5f), 0.4, 0.0, 0.0);
                
                float combinedNoise = (noise + distanceNoise * 0.3f) * config.density;
                
                if (combinedNoise > config.noiseThreshold) {
                    if (random.nextFloat() < config.spawnChance * 0.5f) {
                        generateSmallOrePatch(config, x, y, oreSeed);
                    }
                }
            }
        }
    }
    
    /**
     * Generate a cluster of ore patches around a center point
     */
    private void generateOreCluster(OreConfig config, Vec2 center, int seed) {
        Random clusterRandom = new Random(seed + (int)(center.x * center.y));
        
        int numPatches = clusterRandom.nextInt(3) + 2; // 2-4 patches per cluster
        float clusterRadius = config.maxPatchSize * 2.5f;
        
        for (int i = 0; i < numPatches; i++) {
            float angle = clusterRandom.nextFloat() * 6.28f; // 2π
            float distance = clusterRandom.nextFloat() * clusterRadius;
            
            int patchX = (int)(center.x + Mathf.cos(angle) * distance);
            int patchY = (int)(center.y + Mathf.sin(angle) * distance);
            
            if (patchX >= 0 && patchX < width && patchY >= 0 && patchY < height) {
                int patchSize = clusterRandom.nextInt(config.maxPatchSize - config.minPatchSize + 1) + config.minPatchSize;
                generateOrePatch(config, patchX, patchY, patchSize, seed + i);
            }
        }
    }
    
    /**
     * Generate small ore patches for scattered generation
     */
    private void generateSmallOrePatch(OreConfig config, int centerX, int centerY, int seed) {
        Random patchRandom = new Random(seed + centerX * 1000 + centerY);
        int patchSize = patchRandom.nextInt(Math.min(config.maxPatchSize, 6) - config.minPatchSize + 1) + config.minPatchSize;
        generateOrePatch(config, centerX, centerY, patchSize, seed);
    }
    
    /**
     * Generate a single ore patch using various shapes
     */
    private void generateOrePatch(OreConfig config, int centerX, int centerY, int size, int seed) {
        Random patchRandom = new Random(seed + centerX * 10000 + centerY * 100);
        
        // Choose patch shape
        PatchShape shape = PatchShape.values()[patchRandom.nextInt(PatchShape.values().length)];
        
        switch (shape) {
            case CIRCULAR:
                patchGenerator.generateCircularPatch(config, centerX, centerY, size, patchRandom);
                break;
            case OVAL:
                patchGenerator.generateOvalPatch(config, centerX, centerY, size, patchRandom);
                break;
            case IRREGULAR:
                patchGenerator.generateIrregularPatch(config, centerX, centerY, size, patchRandom);
                break;
            case LINEAR:
                patchGenerator.generateLinearPatch(config, centerX, centerY, size, patchRandom);
                break;
        }
    }
    
    private void placeOre(OreConfig config, int x, int y) {
        Tile tile = tiles.getn(x, y);
        
        if (isValidFloorForOre(tile, config) && tile.overlay() == Blocks.air) {
            tile.setOverlay(config.oreType);
        }
    }
    
    private boolean isValidFloorForOre(Tile tile, OreConfig config) {
        if (tile == null) return false;
        
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