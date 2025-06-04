package mod.planets;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.maps.generators.BaseGenerator;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;

public class IrionPlanetGenerator extends PlanetGenerator {

    public static boolean alt = false;

    BaseGenerator basegen = new BaseGenerator(); // Per generare basi
    float scl = 5f;
    float waterOffset = 0.12f;
    boolean genLakes = false;

    // Ore generation components
    private OreGenerator oreGenerator;
    private int oreSeed = 12345; // Base seed for ore generation

    ObjectMap<Block, Block> tars = ObjectMap.of(
            Blocks.sporeMoss, Blocks.shale,
            Blocks.moss, Blocks.shale);

    private final Block[] floor = { Blocks.sand, Blocks.water, Blocks.deepwater, Blocks.stone };
    private final Block[] overlay = { Blocks.oreCopper, Blocks.oreLead, Blocks.oreScrap, Blocks.oreCoal,
            Blocks.oreTitanium, Blocks.oreThorium,
            Blocks.oreBeryllium, Blocks.oreTungsten, Blocks.oreCrystalThorium };
    private final Block[] block = { Blocks.air };

    @Override
    // Il blocco determina il colore dell'atmosphera esterna
    // Quella che era diventata enorme
    public Color getColor(Vec3 position) {
        Block block = getBlock(position);
        if (block == Blocks.salt)
            return Blocks.sand.mapColor;
        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    // Funzione che genera i muri
    // Per ora non funziona
    public void genTile(Vec3 position, TileGen tile) {
        if (genWalls == null) {
            genWalls = RandomGen.generate(tiles.width, tiles.height, 0);
        }

        // tile.block = tiles.getn((int) position.x, (int) position.y).floor().wall;
        tile.block = Blocks.stoneWall;
        if (genWalls[(int) (position.x + position.y * width)]) {
            tile.block = Blocks.air;
        }
        /*
         * tile.floor = getBlock(position);
         * if (genWalls == null) {
         * genWalls = RandomGen.generate(tiles.width, tiles.height, 0);
         * }
         * tile.block = (genWalls[(int) (position.x + position.y * width)]) ?
         * tile.floor.asFloor().wall : Blocks.air;
         * 
         * if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) >
         * 0.31){
         * tile.block = Blocks.air;
         * }
         */
    }

    /*----------*/
    static boolean[] genWalls;

    Block[][] arr = {
            { Blocks.stone, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.darksand,
                    Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.darksand, Blocks.stone,
                    Blocks.stone },
            { Blocks.stone, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.sand, Blocks.sand,
                    Blocks.sand, Blocks.sand, Blocks.sand, Blocks.darksand, Blocks.stone, Blocks.stone,
                    Blocks.stone },
            { Blocks.stone, Blocks.darksand, Blocks.darksand, Blocks.sand, Blocks.salt, Blocks.sand, Blocks.sand,
                    Blocks.sand, Blocks.sand, Blocks.darksand, Blocks.stone, Blocks.stone, Blocks.stone },
            { Blocks.stone, Blocks.sand, Blocks.sand, Blocks.salt, Blocks.salt, Blocks.salt, Blocks.sand,
                    Blocks.stone, Blocks.stone, Blocks.stone, Blocks.snow, Blocks.iceSnow, Blocks.ice },
            { Blocks.stone, Blocks.stone, Blocks.sand, Blocks.sand, Blocks.salt, Blocks.sand, Blocks.sand,
                    Blocks.basalt, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice },
            { Blocks.stone, Blocks.stone, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.moss,
                    Blocks.iceSnow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.snow, Blocks.ice },
            { Blocks.stone, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.moss, Blocks.moss, Blocks.snow,
                    Blocks.basalt, Blocks.basalt, Blocks.basalt, Blocks.ice, Blocks.snow, Blocks.ice },
            { Blocks.stone, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.basalt,
                    Blocks.moss, Blocks.basalt, Blocks.hotrock, Blocks.basalt, Blocks.ice, Blocks.snow, Blocks.ice,
                    Blocks.ice },
            { Blocks.stone, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.moss, Blocks.sporeMoss,
                    Blocks.snow, Blocks.basalt, Blocks.basalt, Blocks.ice, Blocks.snow, Blocks.ice, Blocks.ice },
            { Blocks.stone, Blocks.darksand, Blocks.darksand, Blocks.sporeMoss, Blocks.ice, Blocks.ice,
                    Blocks.snow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice },
            { Blocks.stone, Blocks.darksand, Blocks.darksand, Blocks.sporeMoss, Blocks.sporeMoss,
                    Blocks.ice, Blocks.ice, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice },
            { Blocks.stone, Blocks.darksand, Blocks.darksand, Blocks.sporeMoss, Blocks.moss,
                    Blocks.sporeMoss, Blocks.iceSnow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice,
                    Blocks.ice },
            { Blocks.stone, Blocks.darksand, Blocks.snow, Blocks.ice, Blocks.iceSnow, Blocks.snow, Blocks.snow,
                    Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice }
    };

    // Da modificare ----------------------------------------------------------
    Block getBlock(Vec3 position) {
        float height = rawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(scl);
        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2f) / (rad));
        float tnoise = Simplex.noise3d(seed, 7, 0.56, 1f / 3f, position.x, position.y + 999f, position.z);
        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        float tar = Simplex.noise3d(seed, 4, 0.55f, 1f / 2f, position.x, position.y + 999f, position.z) * 0.3f
                + Tmp.v31.dst(0, 0, 1f) * 0.2f;

        Block res = arr[Mathf.clamp((int) (temp * arr.length), 0, arr[0].length - 1)][Mathf
                .clamp((int) (height * arr[0].length), 0, arr[0].length - 1)];
        if (tar > 0.5f) {
            return tars.get(res, res);
        } else {
            return res;
        }
    }
    /*----------*/

    @Override
    public void generateSector(Sector sector) {
        // Remove the offensive content and use a proper name
        sector.setName("Irion Sector " + sector.id);
    }

    @Override
    public float getHeight(Vec3 position) {
        float height = rawHeight(position);
        return Math.max(height, 6.5f);
    }

    float rawHeight(Vec3 position) {
        position = Tmp.v33.set(position).scl(scl);
        return (Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f / 3f, position.x, position.y, position.z), 2.3f)
                + waterOffset) / (1f + waterOffset);
    }

    @Override
    public void generate() { //DIOCANE
        // Initialize ore generator
        oreGenerator = new OreGenerator(tiles, seed + oreSeed);

        // Generate base terrain using the advanced map generator
        generateAdvancedTerrain();

        // Generate ores with custom configuration for this planet
        generatePlanetOres();

        // Place core and surrounding area
        generateCoreArea();
    }

    /**
     * Generate advanced terrain with multiple layers
     */
    private void generateAdvancedTerrain() {
        // Base stone layer
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile.setFloor(tiles.getn(x, y), Blocks.stone, Blocks.air);
            }
        }

        // Generate different floor types in layers
        generateFloorLayer(Blocks.sand, 4, 25, seed + 100);
        generateFloorLayer(Blocks.darksand, 3, 15, seed + 200);
        generateFloorLayer(Blocks.basalt, 3, 20, seed + 300);
        generateFloorLayer(Blocks.moss, 2, 10, seed + 400);
        generateFloorLayer(Blocks.sporeMoss, 2, 8, seed + 500);
        generateFloorLayer(Blocks.salt, 2, 12, seed + 600);
        generateFloorLayer(Blocks.snow, 3, 18, seed + 700);
        generateFloorLayer(Blocks.ice, 2, 15, seed + 800);

        // Generate water bodies
        //generateWaterBodies();

        // Generate walls (commented out your existing wall logic, but keeping it available)
        // generateWalls();
    }

    /**
     * Generate a specific floor type using noise and cellular automata
     */
    private void generateFloorLayer(Block floorType, int iterations, int percentCoverage, int layerSeed) {
        boolean[] floorMask = RandomGen.generate(tiles.width, tiles.height, iterations, percentCoverage, layerSeed);
        
        for (int i = 0; i < floorMask.length; i++) {
            if (floorMask[i]) {
                int x = i % tiles.width;
                int y = i / tiles.width;
                Tile tile = tiles.getn(x, y);
                
                // Only place floor if it's still the default stone floor
                if (tile.floor() == Blocks.stone) {
                    Tile.setFloor(tile, floorType, Blocks.air);
                }
            }
        }
    }

    /**
     * Generate water bodies and rivers
     */
    private void generateWaterBodies() {
        // Generate water patches with reduced frequency
        boolean[] waterMask = RandomGen.generate(tiles.width, tiles.height, 3, 8, seed + 1000); // Reduced from 15 to 8
        boolean[] deepWaterMask = RandomGen.generate(tiles.width, tiles.height, 2, 4, seed + 1100); // Reduced from 8 to 4
        
        for (int i = 0; i < waterMask.length; i++) {
            if (waterMask[i]) {
                int x = i % tiles.width;
                int y = i / tiles.width;
                Tile tile = tiles.getn(x, y);
                
                // Only place water if it's not too close to the center
                float distFromCenter = Mathf.sqrt((x - tiles.width/2) * (x - tiles.width/2) + 
                                                (y - tiles.height/2) * (y - tiles.height/2));
                if (distFromCenter < 20) continue; // Skip water placement near center
                
                Block waterType = deepWaterMask[i] ? Blocks.deepwater : Blocks.water;
                
                // Use appropriate water type based on surrounding floor
                if (tile.floor() == Blocks.darksand) {
                    waterType = deepWaterMask[i] ? Blocks.deepTaintedWater : Blocks.darksandTaintedWater;
                } else if (tile.floor() == Blocks.sand) {
                    waterType = deepWaterMask[i] ? Blocks.deepwater : Blocks.sandWater;
                }
                
                Tile.setFloor(tile, waterType, Blocks.air);
            }
        }
    }

    /**
     * Generate planet-specific ore configurations
     */
    private void generatePlanetOres() {
        // Custom ore configurations for Irion planet
        OreConfig[] irionOreConfigs = {
            // Common ores - moderate abundance
            new OreConfig(Blocks.oreCopper, 0.25f, 3, 8, 0.6f, 0.9f, 0.35f, 15, false,
                Blocks.stone, Blocks.sand, Blocks.basalt),
            
            new OreConfig(Blocks.oreCoal, 0.22f, 3, 7, 0.55f, 1.0f, 0.4f, 18, false,
                Blocks.stone, Blocks.sand, Blocks.darksand),
            
            new OreConfig(Blocks.oreLead, 0.20f, 2, 6, 0.5f, 1.2f, 0.45f, 20, true,
                Blocks.stone, Blocks.basalt, Blocks.darksand),
            
            // Uncommon ores - moderate abundance
            new OreConfig(Blocks.oreScrap, 0.15f, 2, 5, 0.4f, 1.4f, 0.5f, 25, true,
                Blocks.stone, Blocks.basalt, Blocks.sand),
            
            new OreConfig(Blocks.oreTitanium, 0.12f, 2, 4, 0.35f, 1.6f, 0.55f, 30, true,
                Blocks.stone, Blocks.basalt),
            
            // Rare ores - clustered generation
            new OreConfig(Blocks.oreThorium, 0.08f, 1, 3, 0.25f, 1.8f, 0.6f, 35, true,
                Blocks.stone, Blocks.basalt),
            
            new OreConfig(Blocks.oreBeryllium, 0.10f, 2, 4, 0.3f, 1.7f, 0.58f, 32, true,
                Blocks.stone, Blocks.basalt, Blocks.hotrock),
            
            // Very rare ores - highly clustered
            new OreConfig(Blocks.oreTungsten, 0.06f, 1, 3, 0.2f, 2.0f, 0.65f, 40, true,
                Blocks.stone, Blocks.basalt),
            
            new OreConfig(Blocks.oreCrystalThorium, 0.04f, 1, 2, 0.15f, 2.5f, 0.7f, 45, true,
                Blocks.stone)
        };

        // Generate ores with custom configuration
        oreGenerator.generateOres(seed + oreSeed, irionOreConfigs);
    }

    /**
     * Generate core area with surrounding safe zone
     */
    private void generateCoreArea() {
        int coreX = tiles.width / 2;
        int coreY = tiles.height / 2;
        
        // Create core zone floor
        surround(Blocks.coreNucleus, coreX, coreY, Blocks.coreZone);
        
        // Place the core
        tiles.getn(coreX, coreY).setBlock(Blocks.coreNucleus, Team.sharded);
        
        // Clear ores around core for better starting area
        clearOresAroundCore(coreX, coreY, 8);
    }

    /**
     * Clear ores in a radius around the core
     */
    private void clearOresAroundCore(int coreX, int coreY, int radius) {
        for (int y = coreY - radius; y <= coreY + radius; y++) {
            for (int x = coreX - radius; x <= coreX + radius; x++) {
                if (x >= 0 && x < tiles.width && y >= 0 && y < tiles.height) {
                    float distance = Mathf.sqrt((x - coreX) * (x - coreX) + (y - coreY) * (y - coreY));
                    if (distance <= radius) {
                        Tile tile = tiles.getn(x, y);
                        if (tile.overlay().itemDrop != null) { // Is an ore
                            tile.setOverlay(Blocks.air);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generate walls (your existing logic, modified)
     */
    private void generateWalls() {
        boolean[] wallMask = RandomGen.generate(tiles.width, tiles.height, 4, 15, seed + 2000);
        
        for (int i = 0; i < wallMask.length; i++) {
            if (wallMask[i]) {
                int x = i % tiles.width;
                int y = i / tiles.width;
                Tile tile = tiles.getn(x, y);
                
                // Don't place walls on water or in core area
                if (!tile.floor().isLiquid && 
                    Mathf.sqrt((x - tiles.width/2) * (x - tiles.width/2) + 
                               (y - tiles.height/2) * (y - tiles.height/2)) > 10) {
                    
                    Block wallType = tile.floor().wall;
                    if (wallType == null) wallType = Blocks.stoneWall;
                    
                    tile.setBlock(wallType);
                }
            }
        }
    }

    private void surround(Block source, int blockX, int blockY, Block floor) {
        int blockSize = source.size;
        if (blockSize % 2 != 0) {
            blockX -= blockSize / 2 + 1; // Adjust x to center the core zone
            blockY -= blockSize / 2 + 1; // Adjust y to center the core zone
        } else {
            // If condition is false, adjust the coordinates differently
            blockX = blockX - blockSize / 2;
            blockY = blockY - blockSize / 2;
        }

        for (int i = 0; i < blockSize + 2; i++) {
            for (int j = 0; j < blockSize + 2; j++) {
                Tile tile = tiles.getn(blockX + i, blockY + j);
                Tile.setFloor(tile, floor, Blocks.air);
            }
        }
    }

    /**
     * Utility method to regenerate only ores (useful for testing)
     */
    public void regenerateOres() {
        if (oreGenerator != null) {
            oreGenerator.clearOres();
            generatePlanetOres();
        }
    }

    /**
     * Set custom ore seed for variation
     */
    public void setOreSeed(int newOreSeed) {
        this.oreSeed = newOreSeed;
    }
}