package RandomPlanet.utils;

import java.util.Arrays;
import java.util.Collections;

import RandomPlanet.config.TerrainGenConfig;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.Tiles;

public class RPPlanetGenerator extends PlanetGenerator {

        public RPPlanetGenerator() {
                config.seed = rand.nextInt();
                Collections.shuffle(Arrays.asList(config.arr));
        }

        private TerrainGenConfig config = new TerrainGenConfig();

        @Override
        public Color getColor(Vec3 position) {
                // Logic to determine the color of the planet tile based on position
                Block block = getBlock(position);
                return (block == Blocks.salt) ? Blocks.sand.mapColor : block.mapColor;
        }

        @Override
        public float getHeight(Vec3 position) {
                position = Tmp.v33.set(position).scl(config.noiseScale);
                return (Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f / 3f, position.x, position.y, position.z), 2.3f)
                                + config.waterOffset) / (1f + config.waterOffset);
        }

        @Override
        public void generate(Tiles tiles, Sector sec, int seed) {
                // Sector generation logic here
                this.tiles = tiles;
                this.seed = seed + baseSeed;
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

                tiles.getn(width / 2, height / 2).setBlock(Blocks.coreNucleus, Team.sharded);

                generate(tiles);
        }

        @Override
        protected void genTile(Vec3 position, TileGen tile) {
                // Logic to generate a tile here
                tile.floor = getBlock(position);
                tile.block = tile.floor.asFloor().wall;

                // Create a air room
                if (Ridged.noise3d(config.seed + 1, position.x, position.y, position.z, 7, 22) > 0.05) {
                        tile.block = Blocks.air;
                }
        }

        private Block getBlock(Vec3 position) {
                // Logic to determine the block based on position
                int ind1 = (int) ((Simplex.noise3d(config.seed, config.noiseOctaves, config.noisePersistence,
                                config.noiseScale, position.x, position.y,
                                position.z) + 1f) / 2f * config.arr.length);
                int ind2 = (int) ((Simplex.noise3d(config.seed + 1, config.noiseOctaves, config.noisePersistence,
                                config.noiseScale, position.x, position.y,
                                position.z) + 1f) / 2f * config.arr[ind1].length);
                Block ret = config.arr[ind1][ind2];
                return ret;
        }

}
