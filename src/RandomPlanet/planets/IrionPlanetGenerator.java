package RandomPlanet.planets;

import RandomPlanet.sectors.OreConfig;
import RandomPlanet.sectors.OreGenerator;
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

        // Generate ores with custom configuration for this planet
        generatePlanetOres();

        // Place core and surrounding area
        generateCoreArea();
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