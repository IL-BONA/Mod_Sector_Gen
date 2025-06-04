package mod.planets;

import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;

public class MapGenerator {

    private Tiles tiles;

    public MapGenerator(Tiles tiles) {
        this.tiles = tiles;
    }

    public Tiles generate() {
        for (int s = 0; s < tiles.height * tiles.width; s++) {
            Tile.setFloor(tiles.geti(s), Blocks.stone, Blocks.air);
        }
        Block[] floors = { Blocks.sand, Blocks.stone, Blocks.water };
        int seed = 0;
        for (int s = 0; s < floors.length; s++) {
            boolean[] boolFloor = RandomGen.generate(tiles.width, tiles.height, 4, (floors[s] != Blocks.water || floors[s] != Blocks.deepwater || floors[s] != Blocks.sand) ? 5 : 2, seed += 10);
            for (int z = 0; z < boolFloor.length; z++) {
                if (!boolFloor[z])
                    Tile.setFloor(tiles.geti(z), floors[s], Blocks.air);
            }
        }

        // Overlay
        Block[] overlays = { Blocks.oreCoal, Blocks.oreCopper, Blocks.oreLead, Blocks.oreScrap, Blocks.oreThorium,
                Blocks.oreTitanium };
        for (int s = 0; s < overlays.length; s++) {
            boolean[] boolOverlay = RandomGen.generate(tiles.width, tiles.height, 4, 3, seed++);
            for (int z = 0; z < boolOverlay.length; z++) {
                if (!boolOverlay[z] && tiles.geti(z).floor() != Blocks.water && tiles.geti(z).floor() != Blocks.sand
                        && tiles.geti(z).floor() != Blocks.deepwater)
                    Tile.setOverlay(tiles.geti(z), overlays[s]);
            }
        }

        // Wall
        boolean[] boolWalls = RandomGen.generate(tiles.width, tiles.height, 4, 5, seed++);
        for (int z = 0; z < boolWalls.length; z++) {
            if (!boolWalls[z])
                if (tiles.geti(z).floor() != Blocks.water && tiles.geti(z).floor() != Blocks.deepwater)
                    tiles.geti(z).setBlock(tiles.geti(z).floor().wall);
                else
                    tiles.geti(z).setBlock(Blocks.stone.asFloor().wall);
        }

        return tiles;

    }

}