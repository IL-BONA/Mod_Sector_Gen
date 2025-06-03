package mod.planets;

import java.util.Random;
import arc.util.Log;
import arc.math.Mathf;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.content.Blocks;
import arc.struct.Seq;

public class RandomGen {
    private static Random utilityRandom = new Random();

    public enum Layer {
        FLOOR,    // Livello base (pavimento)
        OVERLAY,  // Livello intermedio (minerali, ecc.)
        BLOCK     // Livello superiore (muri, strutture)
    }

    public static class GenerationResult {
        public final Seq<Tile> tiles;
        public final Layer layer;
        public final int width;
        public final int height;

        public GenerationResult(Seq<Tile> tiles, Layer layer, int width, int height) {
            this.tiles = tiles;
            this.layer = layer;
            this.width = width;
            this.height = height;
        }
    }

    public static GenerationResult generate(Tiles world, Layer layer, int seed) {
        utilityRandom.setSeed(seed);
        return generate(world, layer, 4, getDefaultPercentForLayer(layer), seed);
    }

    public static GenerationResult generate(Tiles world, Layer layer, int iterations, int percentChance, int seed) {
        utilityRandom.setSeed(seed);
        Seq<Tile> resultTiles = new Seq<>();

        // Inizializza con tutti i tile
        for (int y = 0; y < world.height; y++) {
            for (int x = 0; x < world.width; x++) {
                resultTiles.add(world.getn(x, y));
            }
        }

        // Prima iterazione
        resultTiles = RandomFill(resultTiles, world.width, world.height, percentChance, layer);

        // Iterazioni successive
        for (int i = 0; i < iterations; i++) {
            resultTiles = Step(resultTiles, world.width, world.height, layer);
        }

        return new GenerationResult(resultTiles, layer, world.width, world.height);
    }

    private static int getDefaultPercentForLayer(Layer layer) {
        switch (layer) {
            case FLOOR: return 7;
            case OVERLAY: return 3;
            case BLOCK: return 5;
            default: return 7;
        }
    }

    private static Seq<Tile> RandomFill(Seq<Tile> tiles, int width, int height, int percentChance, Layer layer) {
        if (width <= 8 || height <= 8) {
            Log.warn("RandomFill called with dimensions <= 8. The generation might not behave as expected.");
        }

        // Genera una colonna casuale che sarà mantenuta libera
        int randomColumn = (width > 8) ? utilityRandom.nextInt(width - 8) + 4 : -1;
        Seq<Tile> result = new Seq<>();

        for (Tile tile : tiles) {
            int x = tile.x;
            int y = tile.y;

            if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                // Imposta i bordi come muri
                result.add(tile);
            } else if (randomColumn != -1 && x != randomColumn && utilityRandom.nextInt(100) < percentChance) {
                // Imposta muri basati sulla probabilità, evitando la colonna casuale
                result.add(tile);
            }
            // Gli altri tile vengono ignorati (spazio vuoto)
        }

        return result;
    }

    private static Seq<Tile> Step(Seq<Tile> tiles, int width, int height, Layer layer) {
        Seq<Tile> result = new Seq<>();

        for (Tile tile : tiles) {
            int x = tile.x;
            int y = tile.y;

            if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                result.add(tile);
            } else if (placeWallLogic(tiles, width, height, x, y, layer)) {
                result.add(tile);
            }
        }

        return result;
    }

    private static boolean placeWallLogic(Seq<Tile> tiles, int width, int height, int x, int y, Layer layer) {
        int adjacentWalls = CountAdjacentWalls(tiles, width, height, x, y);
        int nearbyWalls = CountNearbyWalls(tiles, width, height, x, y);

        switch (layer) {
            case FLOOR:
                // Per il pavimento, creiamo pattern più naturali
                if (adjacentWalls >= 5) {
                    return true; // Crea aree dense
                } else if (nearbyWalls <= 2) {
                    return true; // Crea piccole depressioni
                }
                break;

            case OVERLAY:
                // Per gli overlay, creiamo cluster di minerali
                if (adjacentWalls >= 4) {
                    return true; // Crea cluster densi
                } else if (nearbyWalls <= 1) {
                    return true; // Crea piccole vene
                }
                break;

            case BLOCK:
                // Per i blocchi, creiamo strutture più definite
                if (adjacentWalls >= 6) {
                    return true; // Crea muri solidi
                } else if (adjacentWalls == 0 && nearbyWalls >= 4) {
                    return true; // Crea corridoi
                }
                break;
        }

        return false;
    }

    private static int CountAdjacentWalls(Seq<Tile> tiles, int width, int height, int x, int y) {
        int walls = 0;

        for (int mapX = x - 1; mapX <= x + 1; mapX++) {
            for (int mapY = y - 1; mapY <= y + 1; mapY++) {
                if (mapX < 0 || mapY < 0 || mapX >= width || mapY >= height) {
                    walls++;
                    continue;
                }
                if (containsTile(tiles, mapX, mapY)) {
                    walls++;
                }
            }
        }

        return walls;
    }

    private static int CountNearbyWalls(Seq<Tile> tiles, int width, int height, int x, int y) {
        int walls = 0;

        for (int mapX = x - 2; mapX <= x + 2; mapX++) {
            for (int mapY = y - 2; mapY <= y + 2; mapY++) {
                if (Math.abs(mapX - x) == 2 && Math.abs(mapY - y) == 2) {
                    continue;
                }

                if (mapX < 0 || mapY < 0 || mapX >= width || mapY >= height) {
                    continue;
                }

                if (containsTile(tiles, mapX, mapY)) {
                    walls++;
                }
            }
        }

        return walls;
    }

    private static boolean containsTile(Seq<Tile> tiles, int x, int y) {
        for (Tile tile : tiles) {
            if (tile.x == x && tile.y == y) {
                return true;
            }
        }
        return false;
    }

    // Metodo di utilità per verificare se una posizione è valida per un determinato livello
    public static boolean isValidPosition(Tile tile, Layer layer) {
        switch (layer) {
            case FLOOR:
                return true; // Il pavimento può essere posizionato ovunque
            case OVERLAY:
                return tile.floor() != Blocks.water && 
                       tile.floor() != Blocks.deepwater && 
                       tile.floor() != Blocks.sand;
            case BLOCK:
                return tile.floor() != Blocks.water && 
                       tile.floor() != Blocks.deepwater;
            default:
                return true;
        }
    }
}
