package mod.planets;

import java.util.Random;

import arc.util.Log;

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

        for (int i = 0; i < iterations; i++)
            map = Step(map, width, height);

        return map;
    }


    public static boolean[] generate(int width, int height) {
        return generate(width, height, 4, 7);
    }

    public static boolean[] generate(int width, int height, int iterations, int percentAreWalls) {
        boolean[] map = new boolean[width * height];

        RandomFill(map, width, height, percentAreWalls);

        for (int i = 0; i < iterations; i++)
            map = Step(map, width, height);

        return map;
    }

    private static void RandomFill(boolean[] map, int width, int height) {
        // Corrected: A void method cannot return the result of another void method.
        RandomFill(map, width, height, 7);

    }

    private static void RandomFill(boolean[] map, int width, int height, int percentAreWalls) {
        // C# Random.Shared.Next(4, width - 4) generates an int in [4, width - 4)
        // Java: utilityRandom.nextInt((width - 4) - 4) + 4
        // This requires width - 8 > 0, so width > 8.
        // If width <= 8, C# would throw ArgumentOutOfRangeException.
        // Java's nextInt would throw IllegalArgumentException if (width - 8) <= 0.
        // This behavior is consistent.
        int randomColumn = (width > 8) ? utilityRandom.nextInt(width - 8) + 4 : -1; // Use -1 or handle error if width
                                                                                    // <=8
        if (width <= 8) {
            Log.warn(
                    "RandomFill called with width <= 8. The 'randomColumn' logic might not behave as expected or could be skipped.");
            // If randomColumn is not used when width is small, setting to -1 might be fine.
            // Otherwise, consider throwing an error or having specific logic for small
            // widths.
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1)
                    map[x + y * width] = true; // Set border to wall
                else if (randomColumn != -1 && x != randomColumn && utilityRandom.nextInt(100) < percentAreWalls) // Check
                                                                                                                  // randomColumn
                                                                                                                  // !=
                                                                                                                  // -1
                                                                                                                  // if
                                                                                                                  // it
                                                                                                                  // could
                                                                                                                  // be
                                                                                                                  // set
                                                                                                                  // to
                                                                                                                  // an
                                                                                                                  // error
                                                                                                                  // indicator
                    map[x + y * width] = true; // Set wall based on probability, avoiding the random column
                else if (randomColumn == -1 && utilityRandom.nextInt(100) < percentAreWalls) // Fallback if no random
                                                                                             // column (small width)
                    map[x + y * width] = true; // Set wall based on probability
            }
        }
    }

    private static boolean[] Step(boolean[] map, int width, int height) {
        boolean[] newMap = new boolean[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1)
                    newMap[x + y * width] = true;
                else
                    newMap[x + y * width] = placeWallLogic(map, width, height, x, y);
            }
        }

        return newMap;
    }

    private static boolean placeWallLogic(boolean[] map, int width, int height, int x, int y) {
        return CountAdjacentWalls(map, width, height, x, y) >= 5 ||
                CountNearbyWalls(map, width, height, x, y) <= 2;
    }

    private static int CountAdjacentWalls(boolean[] map, int width, int height, int x, int y) {
        int walls = 0;

        for (int mapX = x - 1; mapX <= x + 1; mapX++) {
            for (int mapY = y - 1; mapY <= y + 1; mapY++) {
                if (map[mapX + mapY * width])
                    walls++;
            }
        }

        return walls;
    }

    private static int CountNearbyWalls(boolean[] map, int width, int height, int x, int y) {
        int walls = 0;

        for (int mapX = x - 2; mapX <= x + 2; mapX++) {
            for (int mapY = y - 2; mapY <= y + 2; mapY++) {
                if (Math.abs(mapX - x) == 2 && Math.abs(mapY - y) == 2)
                    continue;

                if (mapX < 0 || mapY < 0 || mapX >= width || mapY >= height)
                    continue;

                if (map[mapX + mapY * width])
                    walls++;
            }
        }

        return walls;
    }

}
