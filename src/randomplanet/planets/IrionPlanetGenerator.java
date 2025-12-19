package randomplanet.planets;

import randomplanet.sectors.OreConfig;
import randomplanet.sectors.OreGenerator;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;

public class IrionPlanetGenerator extends PlanetGenerator {

    @Override
    public float getHeight(Vec3 position) {

        return 0;
    }

    @Override
    public Color getColor(Vec3 position) {
        // Return the color for this point on the planet surface
        // Simple example: purple-ish color matching your theme
        return Color.valueOf("7d4dff");
    }

    // Called ONCE per sector at game start
    @Override
    public void generateSector(Sector sector) {
        // Decide if this sector has an enemy base
        sector.generateEnemyBase = rand.chance(0.3);
    }

    OreConfig oreConfig = new OreConfig(
            Blocks.oreCopper,
            0.5f,
            8,
            16,
            0.8f,
            0.01f,
            0.5f,
            16,
            true,
            Blocks.stone, Blocks.sand);

    // Called FIRST TIME you land on this sector
    @Override
    public void generate() {
        /*
         * Has access to the following:
         *
         * this.tiles // The tile grid
         * this.sector // Current sector being generated
         * this.rand // Seeded random (deterministic)
         * this.width // tiles.width
         * this.height // tiles.height
         */

        OreGenerator Generator = new OreGenerator(this.tiles, this.sector.id);
        Generator.generateOres(this.sector.id, oreConfig);

        // Generate core area
        generateCoreArea();
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

}
