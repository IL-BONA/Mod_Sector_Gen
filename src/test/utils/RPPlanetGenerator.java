package test.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.maps.generators.BlankPlanetGenerator;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.Tiles;

public class RPPlanetGenerator {

    public static PlanetGenerator createDefaultPlanetGenerator() {
        PlanetGenerator ret = new BlankPlanetGenerator() {

            Block[][] arr = {
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

            float scl = 5f;
            float waterOffset = 0.07f;

            @Override
            public Color getColor(Vec3 position) {
                // Logic to determine the color of the planet tile based on position
                Block block = getBlock(position);
                return (block == Blocks.salt) ? Blocks.sand.mapColor : block.mapColor;
            }

            @Override
            public float getHeight(Vec3 position) {
                // return 0;
                position = Tmp.v33.set(position).scl(scl);
                return (Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f / 3f, position.x, position.y, position.z), 2.3f)
                        + waterOffset) / (1f + waterOffset);
            }

            @Override
            public void generate(Tiles tiles, Sector sec, int seed) {
                Collections.shuffle(Arrays.asList(arr));
                // Sector generation logic here
                this.tiles = tiles;
                this.seed = seed + baseSeed + rand.nextInt();
                this.sector = sec;
                this.width = tiles.width;
                this.height = tiles.height;
                this.rand.setSeed(sec.id + seed + baseSeed);

                TileGen gen = new TileGen();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        gen.reset();
                        Vec3 position = sector.rect.project(x / (float) tiles.width, y / (float) tiles.height);

                        genTile(position, gen);
                        tiles.set(x, y, new Tile(x, y, gen.floor, gen.overlay, gen.block));
                    }
                }


                tiles.getn(width/2, height/2).setBlock(Blocks.coreNucleus, Team.sharded);

                generate(tiles);
            }

            @Override
            protected void genTile(Vec3 position, TileGen tile) {
                // Logic to generate a tile here
                tile.floor = getBlock(position);
                tile.block = tile.floor.asFloor().wall;

                // Create a air room
                if (Ridged.noise3d(seed + 1, position.x, position.y, position.z, 7, 22) > 0.05) {
                    tile.block = Blocks.air;
                }
            }

            private Block getBlock(Vec3 position) {
                // Logic to determine the block based on position
                int ind1 = (int)((Simplex.noise3d(seed, 7f, 0.5f, scl, position.x, position.y, position.z) + 1f) /2f * arr.length),
                    ind2 = (int)((Simplex.noise3d(seed + 1, 7f, 0.5f, scl, position.x, position.y, position.z) + 1f) /2f * arr[ind1].length);
                Block ret = arr[ind1][ind2];
                return ret;
            }
        };

        return ret;
    }

    public static PlanetGenerator createBlankPlanetGenerator() {
        return new BlankPlanetGenerator();
    }

}
