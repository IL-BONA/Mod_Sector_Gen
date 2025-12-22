package randomplanet.sectors;

import arc.Core;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.Vars;

public class OreConfig {
    public Block oreType;
    public float spawnChance;
    public int minPatchSize;
    public int maxPatchSize;
    public float density;
    public float noiseScale;
    public float noiseThreshold;
    public int minDistance;
    public boolean clusterMode;
    public Block[] allowedFloors;

    public OreConfig(Block oreType,
            float spawnChance,
            int minPatchSize,
            int maxPatchSize,
            float density,
            float noiseScale,
            float noiseThreshold,
            int minDistance,
            boolean clusterMode,
            Block... allowedFloors) {

        this.oreType = oreType;
        this.spawnChance = spawnChance;
        this.minPatchSize = minPatchSize;
        this.maxPatchSize = maxPatchSize;
        this.density = density;
        this.noiseScale = noiseScale;
        this.noiseThreshold = noiseThreshold;
        this.minDistance = minDistance;
        this.clusterMode = clusterMode;
        this.allowedFloors = allowedFloors != null && allowedFloors.length > 0 ? allowedFloors
                : new Block[] { Blocks.stone, Blocks.sand };
    }

    // Save this ore config
    public void save(String prefix) {
        Core.settings.put(prefix + "-spawn-chance", spawnChance);
        Core.settings.put(prefix + "-min-patch", minPatchSize);
        Core.settings.put(prefix + "-max-patch", maxPatchSize);
        Core.settings.put(prefix + "-density", density);
        Core.settings.put(prefix + "-noise-scale", noiseScale);
        Core.settings.put(prefix + "-noise-threshold", noiseThreshold);
        Core.settings.put(prefix + "-min-distance", minDistance);
        Core.settings.put(prefix + "-cluster-mode", clusterMode);
    }

    // Load this ore config
    public void load(String prefix) {
        spawnChance = Core.settings.getFloat(prefix + "-spawn-chance", spawnChance);
        minPatchSize = Core.settings.getInt(prefix + "-min-patch", minPatchSize);
        maxPatchSize = Core.settings.getInt(prefix + "-max-patch", maxPatchSize);
        density = Core.settings.getFloat(prefix + "-density", density);
        noiseScale = Core.settings.getFloat(prefix + "-noise-scale", noiseScale);
        noiseThreshold = Core.settings.getFloat(prefix + "-noise-threshold", noiseThreshold);
        minDistance = Core.settings.getInt(prefix + "-min-distance", minDistance);
        clusterMode = Core.settings.getBool(prefix + "-cluster-mode", clusterMode);
    }

    // Clear this ore config settings
    public void clear(String prefix) {
        Core.settings.remove(prefix + "-spawn-chance");
        Core.settings.remove(prefix + "-min-patch");
        Core.settings.remove(prefix + "-max-patch");
        Core.settings.remove(prefix + "-density");
        Core.settings.remove(prefix + "-noise-scale");
        Core.settings.remove(prefix + "-noise-threshold");
        Core.settings.remove(prefix + "-min-distance");
        Core.settings.remove(prefix + "-cluster-mode");
    }

    // Create a copy with default values
    public OreConfig copy() {
        return new OreConfig(oreType, spawnChance, minPatchSize, maxPatchSize,
                density, noiseScale, noiseThreshold, minDistance, clusterMode, allowedFloors);
    }
}