package mod.planets;

import mindustry.content.Blocks;
import mindustry.world.Block;

public class OreConfig {
    public Block oreType;
    public float spawnChance;        // Base chance for this ore to spawn (0.0 to 1.0)
    public int minPatchSize;         // Minimum size of ore patches
    public int maxPatchSize;         // Maximum size of ore patches
    public float density;            // How dense the ore patches should be (0.0 to 1.0)
    public float noiseScale;         // Scale for noise-based distribution
    public float noiseThreshold;     // Threshold for noise-based placement
    public int minDistance;          // Minimum distance between large patches
    public boolean clusterMode;      // Whether to use cluster-based generation
    public Block[] allowedFloors;    // Which floor types this ore can spawn on
    
    public OreConfig(Block oreType, float spawnChance, int minPatchSize, int maxPatchSize, 
                    float density, float noiseScale, float noiseThreshold, int minDistance,
                    boolean clusterMode, Block... allowedFloors) {
        this.oreType = oreType;
        this.spawnChance = spawnChance;
        this.minPatchSize = minPatchSize;
        this.maxPatchSize = maxPatchSize;
        this.density = density;
        this.noiseScale = noiseScale;
        this.noiseThreshold = noiseThreshold;
        this.minDistance = minDistance;
        this.clusterMode = clusterMode;
        this.allowedFloors = allowedFloors != null && allowedFloors.length > 0 ? 
                           allowedFloors : new Block[]{Blocks.stone, Blocks.sand};
    }
} 