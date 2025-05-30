package mod.planets;

import java.util.Random;
import arc.util.Log;
import arc.math.Mathf;

public class RandomGen {
    private static Random utilityRandom = new Random();

    public static boolean[] generate(int width, int height, int seed) {
        utilityRandom.setSeed(seed);
        return generate(width, height, 4, 7);
    }

    public static boolean[] generate(int width, int height, int iterations, int percentAreWalls, int seed) {
        utilityRandom.setSeed(seed);
        boolean[] map = new boolean[width * height];

        RandomFill(map, width, height, percentAreWalls);

        for (int i = 0; i < iterations; i++) {
            map = Step(map, width, height);
        }

        return map;
    }

    public static boolean[] generate(int width, int height) {
        return generate(width, height, 4, 7);
    }

    public static boolean[] generate(int width, int height, int iterations, int percentAreWalls) {
        boolean[] map = new boolean[width * height];

        RandomFill(map, width, height, percentAreWalls);

        for (int i = 0; i < iterations; i++) {
            map = Step(map, width, height);
        }

        return map;
    }

    private static void RandomFill(boolean[] map, int width, int height, int percentAreWalls) {
        if (width <= 8 || height <= 8) {
            Log.warn("RandomFill called with dimensions <= 8. The generation might not behave as expected.");
        }

        // Generate a random column that will be kept clear
        int randomColumn = (width > 8) ? utilityRandom.nextInt(width - 8) + 4 : -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    map[x + y * width] = true; // Set border to wall
                } else if (randomColumn != -1 && x != randomColumn && utilityRandom.nextInt(100) < percentAreWalls) {
                    map[x + y * width] = true; // Set wall based on probability, avoiding the random column
                } else {
                    map[x + y * width] = false; // Set empty space
                }
            }
        }
    }

    private static boolean[] Step(boolean[] map, int width, int height) {
        boolean[] newMap = new boolean[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    newMap[x + y * width] = true;
                } else {
                    newMap[x + y * width] = placeWallLogic(map, width, height, x, y);
                }
            }
        }

        return newMap;
    }

    private static boolean placeWallLogic(boolean[] map, int width, int height, int x, int y) {
        int adjacentWalls = CountAdjacentWalls(map, width, height, x, y);
        int nearbyWalls = CountNearbyWalls(map, width, height, x, y);

        // Modifica la logica per creare pattern più interessanti
        if (adjacentWalls >= 5) {
            return true; // Crea muri densi
        } else if (nearbyWalls <= 2) {
            return true; // Crea piccole grotte
        } else if (adjacentWalls == 0 && nearbyWalls >= 4) {
            return true; // Crea corridoi
        }

        return false;
    }

    private static int CountAdjacentWalls(boolean[] map, int width, int height, int x, int y) {
        int walls = 0;

        for (int mapX = x - 1; mapX <= x + 1; mapX++) {
            for (int mapY = y - 1; mapY <= y + 1; mapY++) {
                if (mapX < 0 || mapY < 0 || mapX >= width || mapY >= height) {
                    walls++;
                    continue;
                }
                if (map[mapX + mapY * width]) {
                    walls++;
                }
            }
        }

        return walls;
    }

    private static int CountNearbyWalls(boolean[] map, int width, int height, int x, int y) {
        int walls = 0;

        for (int mapX = x - 2; mapX <= x + 2; mapX++) {
            for (int mapY = y - 2; mapY <= y + 2; mapY++) {
                if (Math.abs(mapX - x) == 2 && Math.abs(mapY - y) == 2) {
                    continue;
                }

                if (mapX < 0 || mapY < 0 || mapX >= width || mapY >= height) {
                    continue;
                }

                if (map[mapX + mapY * width]) {
                    walls++;
                }
            }
        }

        return walls;
    }
}
