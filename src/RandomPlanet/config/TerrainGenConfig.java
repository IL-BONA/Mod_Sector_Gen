package RandomPlanet.config;

import mindustry.content.Blocks;
import mindustry.world.Block;

public class TerrainGenConfig {

    public int seed = 0;
    public float waterOffset = 0.07f;
    public Block[][] arr = {
            { Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.darksand,
                    Blocks.darksand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand,
                    Blocks.darksandTaintedWater, Blocks.stone, Blocks.stone },
            { Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.sand, Blocks.sand,
                    Blocks.sand, Blocks.sand, Blocks.sand, Blocks.darksandTaintedWater, Blocks.stone,
                    Blocks.stone, Blocks.stone },
            { Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.sand, Blocks.salt, Blocks.sand,
                    Blocks.sand, Blocks.sand, Blocks.sand, Blocks.darksandTaintedWater, Blocks.stone,
                    Blocks.stone, Blocks.stone },
            { Blocks.water, Blocks.sandWater, Blocks.sand, Blocks.salt, Blocks.salt, Blocks.salt, Blocks.sand,
                    Blocks.stone, Blocks.stone, Blocks.stone, Blocks.snow, Blocks.iceSnow, Blocks.ice },
            { Blocks.deepwater, Blocks.water, Blocks.sandWater, Blocks.sand, Blocks.salt, Blocks.sand,
                    Blocks.sand, Blocks.basalt, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.snow,
                    Blocks.ice },
            { Blocks.deepwater, Blocks.water, Blocks.sandWater, Blocks.sand, Blocks.sand, Blocks.sand,
                    Blocks.moss, Blocks.iceSnow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.snow,
                    Blocks.ice },
            { Blocks.deepwater, Blocks.sandWater, Blocks.sand, Blocks.sand, Blocks.moss, Blocks.moss,
                    Blocks.snow, Blocks.basalt, Blocks.basalt, Blocks.basalt, Blocks.ice, Blocks.snow,
                    Blocks.ice },
            { Blocks.deepTaintedWater, Blocks.darksandTaintedWater, Blocks.darksand, Blocks.darksand,
                    Blocks.basalt, Blocks.moss, Blocks.basalt, Blocks.hotrock, Blocks.basalt, Blocks.ice,
                    Blocks.snow, Blocks.ice, Blocks.ice },
            { Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.moss,
                    Blocks.sporeMoss, Blocks.snow, Blocks.basalt, Blocks.basalt, Blocks.ice, Blocks.snow,
                    Blocks.ice, Blocks.ice },
            { Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.sporeMoss, Blocks.ice, Blocks.ice,
                    Blocks.snow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice },
            { Blocks.deepTaintedWater, Blocks.darksandTaintedWater, Blocks.darksand, Blocks.sporeMoss,
                    Blocks.sporeMoss, Blocks.ice, Blocks.ice, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice,
                    Blocks.ice, Blocks.ice },
            { Blocks.taintedWater, Blocks.darksandTaintedWater, Blocks.darksand, Blocks.sporeMoss, Blocks.moss,
                    Blocks.sporeMoss, Blocks.iceSnow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice,
                    Blocks.ice, Blocks.ice },
            { Blocks.darksandWater, Blocks.darksand, Blocks.snow, Blocks.ice, Blocks.iceSnow, Blocks.snow,
                    Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice }
    };

    // Noise settings
    public float noiseScale = 10f;
    public float noiseOctaves = 6f;
    public float noisePersistence = 0.5f;

}
