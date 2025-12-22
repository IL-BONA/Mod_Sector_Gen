package randomplanet.sectors;

import arc.Core;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.type.Sector;
import mindustry.world.Block;

public class OreConfigManager {

    // Store all ore configurations
    public static Seq<OreConfig> oreConfigs = new Seq<>();

    // Default configs (used for reset)
    private static Seq<OreConfig> defaultConfigs = new Seq<>();

    // Save for specific sector
    public static void save(Sector sector, boolean forceOverride) {
        if (sector == null) {
            save(); // Save global if no sector
            return;
        }

        // Store override flag
        Core.settings.put("sector-" + sector.id + "-overridden", forceOverride);

        for (OreConfig config : oreConfigs) {
            String prefix = "sector-" + sector.id + "-ore-config-" + config.oreType.name;
            if (forceOverride) {
                config.save(prefix);
            } else {
                config.clear(prefix);
            }
        }
    }

    // Load for specific sector
    public static void load(Sector sector) {
        // Always load global first to reset state
        load();

        if (sector == null)
            return;

        // Check if overrides exist
        if (Core.settings.getBool("sector-" + sector.id + "-overridden", false)) {
            for (OreConfig config : oreConfigs) {
                String prefix = "sector-" + sector.id + "-ore-config-" + config.oreType.name;
                config.load(prefix);
            }
        }
    }

    // Initialize with default ore configurations
    public static void init() {
        // Clear existing
        oreConfigs.clear();
        defaultConfigs.clear();

        // Add default ore configs
        addOreConfig(new OreConfig(
                Blocks.oreCopper,
                0.8f, // spawnChance
                5, // minPatchSize
                15, // maxPatchSize
                0.6f, // density
                3.0f, // noiseScale
                0.5f, // noiseThreshold
                20, // minDistance
                true, // clusterMode
                Blocks.stone, Blocks.sand));

        addOreConfig(new OreConfig(
                Blocks.oreLead,
                0.6f,
                4,
                12,
                0.5f,
                2.5f,
                0.6f,
                25,
                true,
                Blocks.stone));

        addOreConfig(new OreConfig(
                Blocks.oreCoal,
                0.5f,
                3,
                10,
                0.4f,
                2.0f,
                0.55f,
                30,
                false,
                Blocks.stone, Blocks.sand));

        addOreConfig(new OreConfig(
                Blocks.oreTitanium,
                0.4f,
                2,
                8,
                0.3f,
                1.5f,
                0.65f,
                35,
                true,
                Blocks.stone));

        addOreConfig(new OreConfig(
                Blocks.oreThorium,
                0.2f,
                1,
                5,
                0.2f,
                1.0f,
                0.7f,
                40,
                true,
                Blocks.stone));

        // Load saved settings
        load();
    }

    private static void addOreConfig(OreConfig config) {
        oreConfigs.add(config);
        defaultConfigs.add(config.copy()); // Store default
    }

    // Save all ore configs
    public static void save() {
        for (int i = 0; i < oreConfigs.size; i++) {
            OreConfig config = oreConfigs.get(i);
            String prefix = "ore-config-" + config.oreType.name;
            config.save(prefix);
        }
    }

    // Load all ore configs
    public static void load() {
        for (OreConfig config : oreConfigs) {
            String prefix = "ore-config-" + config.oreType.name;
            config.load(prefix);
        }
    }

    // Reset to defaults
    public static void reset() {
        oreConfigs.clear();
        for (OreConfig defaultConfig : defaultConfigs) {
            oreConfigs.add(defaultConfig.copy());
        }
    }

    // Get config for specific ore type
    public static OreConfig getConfig(Block oreType) {
        return oreConfigs.find(c -> c.oreType == oreType);
    }

}
