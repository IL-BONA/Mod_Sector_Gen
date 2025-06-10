package mod.sectors;

import arc.math.Mathf;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;

import java.util.Random;

public class OrePatchGenerator {
    private final Tiles tiles;
    private final int width, height;
    
    public OrePatchGenerator(Tiles tiles) {
        this.tiles = tiles;
        this.width = tiles.width;
        this.height = tiles.height;
    }
    
    public void generateCircularPatch(OreConfig config, int centerX, int centerY, int size, Random patchRandom) {
        float radius = size / 2f;
        
        for (int dy = -size; dy <= size; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;
                
                if (x < 0 || x >= width || y < 0 || y >= height) continue;
                
                float distance = Mathf.sqrt(dx * dx + dy * dy);
                if (distance <= radius) {
                    float falloff = 1f - (distance / radius);
                    if (patchRandom.nextFloat() < config.density * falloff) {
                        placeOre(config, x, y);
                    }
                }
            }
        }
    }
    
    public void generateOvalPatch(OreConfig config, int centerX, int centerY, int size, Random patchRandom) {
        float radiusX = size / 2f;
        float radiusY = size / 3f;
        
        for (int dy = -size; dy <= size; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;
                
                if (x < 0 || x >= width || y < 0 || y >= height) continue;
                
                float normalizedX = dx / radiusX;
                float normalizedY = dy / radiusY;
                
                if (normalizedX * normalizedX + normalizedY * normalizedY <= 1f) {
                    if (patchRandom.nextFloat() < config.density) {
                        placeOre(config, x, y);
                    }
                }
            }
        }
    }
    
    public void generateIrregularPatch(OreConfig config, int centerX, int centerY, int size, Random patchRandom) {
        float radius = size / 2f;
        
        for (int dy = -size; dy <= size; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;
                
                if (x < 0 || x >= width || y < 0 || y >= height) continue;
                
                float distance = Mathf.sqrt(dx * dx + dy * dy);
                float noiseOffset = Simplex.noise2d(0, (double)(x * 3f), (double)(y * 3f), 0.5, 0.0, 0.0) * radius * 0.3f;
                
                if (distance + noiseOffset <= radius) {
                    float falloff = 1f - (distance / radius);
                    if (patchRandom.nextFloat() < config.density * falloff) {
                        placeOre(config, x, y);
                    }
                }
            }
        }
    }
    
    public void generateLinearPatch(OreConfig config, int centerX, int centerY, int size, Random patchRandom) {
        float angle = patchRandom.nextFloat() * 6.28f;
        int length = size * 2;
        int thickness = Math.max(1, size / 3);
        
        for (int i = 0; i < length; i++) {
            int x = centerX + (int)(Mathf.cos(angle) * i);
            int y = centerY + (int)(Mathf.sin(angle) * i);
            
            for (int t = -thickness; t <= thickness; t++) {
                int offsetX = x + (int)(Mathf.cos(angle + 1.57f) * t); // perpendicular offset
                int offsetY = y + (int)(Mathf.sin(angle + 1.57f) * t);
                
                if (offsetX >= 0 && offsetX < width && offsetY >= 0 && offsetY < height) {
                    if (patchRandom.nextFloat() < config.density * (1f - Math.abs(t) / (float)thickness)) {
                        placeOre(config, offsetX, offsetY);
                    }
                }
            }
        }
    }
    
    private void placeOre(OreConfig config, int x, int y) {
        Tile tile = tiles.getn(x, y);
        
        if (isValidFloorForOre(tile, config) && tile.overlay() == Blocks.air) {
            tile.setOverlay(config.oreType);
        }
    }
    
    private boolean isValidFloorForOre(Tile tile, OreConfig config) {
        if (tile == null) return false;
        
        Block floor = tile.floor();
        
        // Don't place on water or deep water unless specifically allowed
        if (floor == Blocks.water || floor == Blocks.deepwater || 
            floor == Blocks.taintedWater || floor == Blocks.deepTaintedWater ||
            floor.hasLiquids) {
            return false;
        }
        
        // Check if floor is in allowed list
        for (Block allowedFloor : config.allowedFloors) {
            if (floor == allowedFloor) {
                return true;
            }
        }
        
        return false;
    }
} 