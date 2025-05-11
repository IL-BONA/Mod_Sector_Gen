package irion;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.maps.planet.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;

public class IrionPlanetGenerator extends PlanetGenerator {
    //these are required
    float scl = 5f;
    float waterOffset = 0.07f;
    boolean genLakes = true;

    Block[][] arr = {
        {Blocks.stone, Blocks.ice, Blocks.snow, Blocks.iceSnow},
        {Blocks.darksand, Blocks.sand, Blocks.salt, Blocks.sand},
        {Blocks.darksandTaintedWater, Blocks.sandWater, Blocks.water, Blocks.deepwater},
        {Blocks.darksandWater, Blocks.sandWater, Blocks.deepwater, Blocks.water}
    };

    {
        defaultLoadout = Loadouts.basicBastion;
    }

    @Override
    public void generateSector(Sector sector) {
        //This method is called to generate additional info for a sector
        //Optional
    }

    @Override
    public float getHeight(Vec3 position) {
        float height = Simplex.noise3d(seed, 12, 0.5f, scl, position.x, position.y, position.z) * 0.8f;
        height *= 1.2f;
        height = Mathf.clamp(height);

        return height;
    }

    @Override
    public Color getColor(Vec3 position) {
        Block block = getBlock(position);
        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public void genTile(Vec3 position, TileGen tile) {
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) > 0.31){
            tile.block = Blocks.air;
        }
    }

    Block getBlock(Vec3 position) {
        float height = getHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(scl);
        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2f) / rad);
        float tnoise = Simplex.noise3d(seed, 7, 0.56, 1f/3f, position.x, position.y + 999f, position.z);
        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        float water = 0.02f + Mathf.clamp(height * 1.3f);

        int arrIndex = Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1);

        if(water > 0.5f){
            return arr[2][arrIndex];
        } else {
            return arr[Mathf.clamp((int)(height * arr.length), 0, arr.length - 1)][arrIndex];
        }
    }
} 