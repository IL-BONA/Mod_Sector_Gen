package modTest.planets;

import arc.graphics.Color;
import arc.math.*;
import arc.math.geom.*;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.maps.generators.*;
import mindustry.type.Sector;
import mindustry.world.Block;

public class IrionPlanetGenerator extends PlanetGenerator {

    Block blockList[] = {
            Blocks.water, Blocks.sand, Blocks.deepwater, Blocks.darksand, Blocks.darksandWater, Blocks.sandWater,
            Blocks.stone
    };

    float scl = 5f;
    float water = 2f / 13;
    float waterOffset = 0.07f;

    float rawHeight(Vec3 position) {
        position = Tmp.v33.set(position).scl(scl);
        return (Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f / 3f, position.x, position.y, position.z), 2.3f)
                + waterOffset) / (1f + waterOffset);
    }

    @Override
    public float getHeight(Vec3 position) {
        float height = rawHeight(position);
        return Math.max(height, water);
    }

    @Override
    public Color getColor(Vec3 position) {
        Block block = getBlock(position);
        // replace salt with sand color
        if (block == Blocks.salt)
            return Blocks.sand.mapColor;
        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public void generateSector(Sector sector) {
        // Generate basic sector
        super.generateSector(sector);
        
    }

    Block getBlock(Vec3 position) {
        Block ret = blockList[(int) (Math.random() % blockList.length)];

        return ret;
    }
}
