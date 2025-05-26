package mod.planets;

import java.util.Random;

import arc.graphics.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;

public class IrionPlanetGenerator extends PlanetGenerator {

    private final Block[] floor = { Blocks.sand, Blocks.water, Blocks.deepwater, Blocks.stone };
    private final Block[] overlay = { Blocks.oreCopper, Blocks.oreLead, Blocks.oreScrap, Blocks.oreCoal,
            Blocks.oreTitanium, Blocks.oreThorium,
            Blocks.oreBeryllium, Blocks.oreTungsten, Blocks.oreCrystalThorium };
    private final Block[] block = { Blocks.air, Blocks.coreShard };

    @Override
    public float getHeight(Vec3 position) {
        float height = 1.10f;
        return Math.max(height, 11);
    }

    @Override
    public Color getColor(Vec3 position) {
        Block block = Blocks.sand;
        // replace salt with sand color
        if (block == Blocks.salt)
            return Blocks.sand.mapColor;
        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public void generateSector(Sector sector) {
        sector.setName("Non mi importa");
    }

    @Override
    public void generate() {

        /*
         * 1. Floor = Ex. sand, etc
         * 2. Overlay = ore
         * 3. Block = Ex. air, etc
         */

        // Create a random map
        maxRandomGen();

        // Place core in the middle of the map
        tiles.getn(tiles.width / 2, tiles.height / 2).setBlock(Blocks.coreShard, Team.sharded);
    }

    private void maxRandomGen() {
        // INITIALIZE OBJECT FOR GENERATE RANDOM NUMBERS
        Random random = new Random();

        int tlen = tiles.width * tiles.height;

        for (int i = 0; i < tlen; i++) {
            Tile myTile = tiles.geti(i);

            // setFloor ( tile, floor, overlay );
            // Randomly select a floor and overlay block
            Tile.setFloor(myTile, floor[random.nextInt(floor.length)],
                    overlay[random.nextInt(overlay.length)]);

            // setBlock ( block, team );
            // Randomly select a block
            myTile.setBlock(Blocks.air, Team.sharded);
        }
    }

}
