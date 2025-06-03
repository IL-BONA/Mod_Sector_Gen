package mod.planets;

import arc.graphics.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.ObjectMap;
import arc.util.*;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;

public class IrionPlanetGenerator extends PlanetGenerator {

    public static boolean alt = false;

    BaseGenerator basegen = new BaseGenerator(); // Per generare basi
    float scl = 5f;
    float waterOffset = 0.07f;
    boolean genLakes = false;

    MapGenerator mapGenerator;

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

//    @Override
//    // Funzione che genera i muri
//    // Per ora non funziona
//    public void genTile(Vec3 position, TileGen tile) {
//        if (genWalls == null) {
//            genWalls = RandomGen.generate(tiles.width, tiles.height, 0);
//        }
//
//        // tile.block = tiles.getn((int) position.x, (int) position.y).floor().wall;
//        tile.block = Blocks.stoneWall;
//        if (genWalls[(int) (position.x + position.y * width)]) {
//            tile.block = Blocks.air;
//        }
//        /*
//         * tile.floor = getBlock(position);
//         * if (genWalls == null) {
//         * genWalls = RandomGen.generate(tiles.width, tiles.height, 0);
//         * }
//         * tile.block = (genWalls[(int) (position.x + position.y * width)]) ?
//         * tile.floor.asFloor().wall : Blocks.air;
//         * 
//         * if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) >
//         * 0.31){
//         * tile.block = Blocks.air;
//         * }
//         */
//    }

    /*----------*/
    static boolean[] genWalls;

    Block[][] arr = {
            { Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.darksand,
                    Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.darksandTaintedWater, Blocks.stone,
                    Blocks.stone },
            { Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.sand, Blocks.sand,
                    Blocks.sand, Blocks.sand, Blocks.sand, Blocks.darksandTaintedWater, Blocks.stone, Blocks.stone,
                    Blocks.stone },
            { Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.sand, Blocks.salt, Blocks.sand, Blocks.sand,
                    Blocks.sand, Blocks.sand, Blocks.darksandTaintedWater, Blocks.stone, Blocks.stone, Blocks.stone },
            { Blocks.water, Blocks.sandWater, Blocks.sand, Blocks.salt, Blocks.salt, Blocks.salt, Blocks.sand,
                    Blocks.stone, Blocks.stone, Blocks.stone, Blocks.snow, Blocks.iceSnow, Blocks.ice },
            { Blocks.deepwater, Blocks.water, Blocks.sandWater, Blocks.sand, Blocks.salt, Blocks.sand, Blocks.sand,
                    Blocks.basalt, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice },
            { Blocks.deepwater, Blocks.water, Blocks.sandWater, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.moss,
                    Blocks.iceSnow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.snow, Blocks.ice },
            { Blocks.deepwater, Blocks.sandWater, Blocks.sand, Blocks.sand, Blocks.moss, Blocks.moss, Blocks.snow,
                    Blocks.basalt, Blocks.basalt, Blocks.basalt, Blocks.ice, Blocks.snow, Blocks.ice },
            { Blocks.deepTaintedWater, Blocks.darksandTaintedWater, Blocks.darksand, Blocks.darksand, Blocks.basalt,
                    Blocks.moss, Blocks.basalt, Blocks.hotrock, Blocks.basalt, Blocks.ice, Blocks.snow, Blocks.ice,
                    Blocks.ice },
            { Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.moss, Blocks.sporeMoss,
                    Blocks.snow, Blocks.basalt, Blocks.basalt, Blocks.ice, Blocks.snow, Blocks.ice, Blocks.ice },
            { Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.sporeMoss, Blocks.ice, Blocks.ice,
                    Blocks.snow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice },
            { Blocks.deepTaintedWater, Blocks.darksandTaintedWater, Blocks.darksand, Blocks.sporeMoss, Blocks.sporeMoss,
                    Blocks.ice, Blocks.ice, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice },
            { Blocks.taintedWater, Blocks.darksandTaintedWater, Blocks.darksand, Blocks.sporeMoss, Blocks.moss,
                    Blocks.sporeMoss, Blocks.iceSnow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice,
                    Blocks.ice },
            { Blocks.darksandWater, Blocks.darksand, Blocks.snow, Blocks.ice, Blocks.iceSnow, Blocks.snow, Blocks.snow,
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
        sector.setName("NIGGERS");
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
    public void generate() {

        /*
         * 1. Floor = Ex. sand, etc
         * 2. Overlay = ore
         * 3. Block = Ex. air, etc
         */

        // Genera la mappa con aree tematiche
        //generateAreeMap();
        MapGenerator mapGenerator = new MapGenerator(tiles);
        mapGenerator.generate();

        surround(Blocks.coreNucleus, tiles.width / 2, tiles.height / 2, Blocks.coreZone);

        // Place core in the middle of the map

        // Tile.setFloor(tiles.getn(tiles.width / 2-2, tiles.height / 2+2),
        // Blocks.coreZone,Blocks.air);
        tiles.getn(tiles.width / 2, tiles.height / 2).setBlock(Blocks.coreNucleus, Team.sharded);
    }

//    private void generateAreeMap() {
//
//        boolean[] walls = RandomGen.generate(tiles.width, tiles.height, 0);
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                // tiles.getn(x,y).setBlock(walls[x + y * width] ? Blocks.stone : Blocks.sand);
//                Tile.setFloor(tiles.getn(x, y), walls[x + y * width] ? Blocks.stone : Blocks.sand, Blocks.air);
//            }
//        }
//
//    }

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
                Tile.setFloor(tile, Blocks.coreZone, Blocks.air);
            }
        }

    }

    /*----------------------------------------------------------------*/

}
