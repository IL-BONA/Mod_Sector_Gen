package mod.planets;

import java.util.Random;

import arc.graphics.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.ObjectMap;
import arc.util.*;
import arc.util.noise.Noise;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.graphics.g3d.PlanetGrid.Ptile;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;

public class IrionPlanetGenerator extends PlanetGenerator {

    public static boolean alt = false;

    BaseGenerator basegen = new BaseGenerator();
    float scl = 5f;
    float waterOffset = 0.07f;
    boolean genLakes = false;

    ObjectMap<Block, Block> tars = ObjectMap.of(
        Blocks.sporeMoss, Blocks.shale,
        Blocks.moss, Blocks.shale
    );




    private final Block[] floor = { Blocks.sand, Blocks.water, Blocks.deepwater, Blocks.stone };
    private final Block[] overlay = { Blocks.oreCopper, Blocks.oreLead, Blocks.oreScrap, Blocks.oreCoal,
            Blocks.oreTitanium, Blocks.oreThorium,
            Blocks.oreBeryllium, Blocks.oreTungsten, Blocks.oreCrystalThorium };
    private final Block[] block = { Blocks.air};

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
        sector.setName("NIGGERS");
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) > 0.31){
            tile.block = Blocks.air;
        }
    }

    Block getBlock(Vec3 position){
        float height = rawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(scl);
        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2f) / (rad));
        float tnoise = Simplex.noise3d(seed, 7, 0.56, 1f/3f, position.x, position.y + 999f, position.z);
        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        float tar = Simplex.noise3d(seed, 4, 0.55f, 1f/2f, position.x, position.y + 999f, position.z) * 0.3f + Tmp.v31.dst(0, 0, 1f) * 0.2f;

        Block res = block[Mathf.clamp((int)(temp * block.length), 0, block.length - 1)];
        if(tar > 0.5f){
            return tars.get(res, res);
        }else{
            return res;
        }
    }

    @Override
    public float getHeight(Vec3 position){
        float height = rawHeight(position);
        return Math.max(height, water);
    }

    float water = 2f / block.length;

    float rawHeight(Vec3 position){
        position = Tmp.v33.set(position).scl(scl);
        return (Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f/3f, position.x, position.y, position.z), 2.3f) + waterOffset) / (1f + waterOffset);
    }


    @Override
    public void generate() {

        /*
         * 1. Floor = Ex. sand, etc
         * 2. Overlay = ore
         * 3. Block = Ex. air, etc
         */

        // Create a random map
        //maxRandomGen();


        Tile testtile = tiles.getn(100, 100);
        Tile.setFloor(testtile, Blocks.coreZone, Blocks.air);
        testtile.setBlock(Blocks.cryofluidMixer, Team.sharded);
        
        Tile testtileC = tiles.getn(130, 130);
        Tile.setFloor(testtileC, Blocks.coreZone, Blocks.air);
        testtileC.setBlock(Blocks.coreBastion, Team.sharded);
        
        
        surround(Blocks.coreNucleus, tiles.width / 2,tiles.height / 2,Blocks.coreZone);

        // Place core in the middle of the map
       
       // Tile.setFloor(tiles.getn(tiles.width / 2-2, tiles.height / 2+2), Blocks.coreZone,Blocks.air);
        tiles.getn(tiles.width / 2, tiles.height / 2).setBlock(Blocks.coreNucleus, Team.sharded);
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
    
    private void surround(Block source, int blockX, int blockY, Block floor) {
        int blockSize = source.size;
        if (blockSize % 2 != 0) {
             blockX-= blockSize/2+1; // Adjust x to center the core zone
             blockY-= blockSize/2+1; // Adjust y to center the core zone
        } else {
            // If condition is false, adjust the coordinates differently
            blockX = blockX - blockSize / 2;
            blockY = blockY - blockSize / 2;
        }
        

        for (int i = 0; i < blockSize+2; i++) {
            for (int j = 0; j < blockSize+2; j++) {
                Tile tile = tiles.getn(blockX + i, blockY + j);
                Tile.setFloor(tile, Blocks.coreZone, Blocks.air);
            }
        }

    }

    

}
