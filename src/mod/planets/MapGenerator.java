package mod.planets;

import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import arc.struct.Seq;

public class MapGenerator {
    private final Tiles tiles;
    private final Seq<GenerationParams> floorParams;
    private final Seq<GenerationParams> overlayParams;
    private final Seq<GenerationParams> wallParams;
    private int currentSeed;

    public MapGenerator(Tiles tiles) {
        this.tiles = tiles;
        this.floorParams = new Seq<>();
        this.overlayParams = new Seq<>();
        this.wallParams = new Seq<>();
        this.currentSeed = 0;
        initializeDefaultParams();
    }

    private void initializeDefaultParams() {
        // Default floor parameters
        floorParams.add(new GenerationParams.Builder()
            .block(Blocks.sand)
            .iterations(4)
            .percentChance(5)
            .seed(currentSeed++)
            .build());
        
        floorParams.add(new GenerationParams.Builder()
            .block(Blocks.stone)
            .iterations(4)
            .percentChance(5)
            .seed(currentSeed++)
            .build());
        
        floorParams.add(new GenerationParams.Builder()
            .block(Blocks.water)
            .iterations(4)
            .percentChance(2)
            .seed(currentSeed++)
            .build());

        // Default overlay parameters
        overlayParams.add(new GenerationParams.Builder()
            .block(Blocks.oreCoal)
            .iterations(4)
            .percentChance(3)
            .seed(currentSeed++)
            .requiresEmptySpace(true)
            .incompatibleWith(Blocks.water, Blocks.deepwater, Blocks.sand)
            .build());
        
        overlayParams.add(new GenerationParams.Builder()
            .block(Blocks.oreCopper)
            .iterations(4)
            .percentChance(3)
            .seed(currentSeed++)
            .requiresEmptySpace(true)
            .incompatibleWith(Blocks.water, Blocks.deepwater, Blocks.sand)
            .build());
        
        overlayParams.add(new GenerationParams.Builder()
            .block(Blocks.oreLead)
            .iterations(4)
            .percentChance(3)
            .seed(currentSeed++)
            .requiresEmptySpace(true)
            .incompatibleWith(Blocks.water, Blocks.deepwater, Blocks.sand)
            .build());

        // Default wall parameters
        wallParams.add(new GenerationParams.Builder()
            .block(Blocks.stoneWall)
            .iterations(4)
            .percentChance(5)
            .seed(currentSeed++)
            .requiresEmptySpace(true)
            .incompatibleWith(Blocks.water, Blocks.deepwater)
            .build());
    }

    public void addFloorParams(GenerationParams params) {
        floorParams.add(params);
    }

    public void addOverlayParams(GenerationParams params) {
        overlayParams.add(params);
    }

    public void addWallParams(GenerationParams params) {
        wallParams.add(params);
    }

    public Tiles generate() {
        // Initialize with stone floor
        for (int s = 0; s < tiles.height * tiles.width; s++) {
            Tile.setFloor(tiles.geti(s), Blocks.stone, Blocks.air);
        }

        // Generate floors
        for (GenerationParams params : floorParams) {
            boolean[] boolFloor = RandomGen.generate(tiles.width, tiles.height, 
                params.iterations, params.percentChance, params.seed);
            
            for (int z = 0; z < boolFloor.length; z++) {
                if (!boolFloor[z]) {
                    Tile.setFloor(tiles.geti(z), params.block, Blocks.air);
                }
            }
        }

        // Generate overlays
        for (GenerationParams params : overlayParams) {
            boolean[] boolOverlay = RandomGen.generate(tiles.width, tiles.height, 
                params.iterations, params.percentChance, params.seed);
            
            for (int z = 0; z < boolOverlay.length; z++) {
                if (!boolOverlay[z]) {
                    Tile tile = tiles.geti(z);
                    if (canPlaceBlock(tile, params)) {
                        Tile.setOverlay(tile, params.block);
                    }
                }
            }
        }

        // Generate walls
        for (GenerationParams params : wallParams) {
            boolean[] boolWalls = RandomGen.generate(tiles.width, tiles.height, 
                params.iterations, params.percentChance, params.seed);
            
            for (int z = 0; z < boolWalls.length; z++) {
                if (!boolWalls[z]) {
                    Tile tile = tiles.geti(z);
                    if (canPlaceBlock(tile, params)) {
                        tile.setBlock(params.block);
                    }
                }
            }
        }

        return tiles;
    }

    private boolean canPlaceBlock(Tile tile, GenerationParams params) {
        if (params.requiresEmptySpace && tile.block() != Blocks.air) {
            return false;
        }

        for (Block incompatible : params.incompatibleWith) {
            if (tile.floor() == incompatible || tile.block() == incompatible) {
                return false;
            }
        }

        return true;
    }
}