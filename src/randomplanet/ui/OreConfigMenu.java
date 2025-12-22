package randomplanet.ui;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import mindustry.game.EventType.*;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import randomplanet.sectors.OreConfig;
import randomplanet.sectors.OreConfigManager;

import static mindustry.Vars.*;

public class OreConfigMenu {

    // Call this from your mod's init() method
    public static void init() {
        // Initialize ore configs
        OreConfigManager.init();

        // Wait for client to load
        Events.on(ClientLoadEvent.class, e -> {
            addMenuButton();
        });
    }

    // Add button to main menu
    private static void addMenuButton() {
        ui.menufrag.addButton("Ore Config", Icon.settings, () -> {
            showOreListDialog();
        });
    }

    // Show list of all ores
    private static void showOreListDialog() {
        BaseDialog dialog = new BaseDialog("Ore Generation Configuration");

        dialog.cont.pane(list -> {
            list.defaults().size(600f, 70f).pad(5f);

            for (OreConfig config : OreConfigManager.oreConfigs) {
                list.button(config.oreType.localizedName, () -> {
                    showOreConfigDialog(config);
                }).with(b -> {
                    b.left();
                    b.image(config.oreType.uiIcon).size(40f).padRight(10f);
                }).row();
            }
        }).grow();

        dialog.cont.row();
        dialog.cont.table(buttons -> {
            buttons.button("Reset All to Defaults", Icon.refresh, () -> {
                OreConfigManager.reset();
                OreConfigManager.save();
                ui.showInfoToast("All ore configs reset!", 3f);
                dialog.hide();
            }).size(250f, 60f).pad(10f);

            buttons.button("Close", Icon.cancel, dialog::hide)
                    .size(150f, 60f).pad(10f);
        });

        dialog.show();
    }

    // Show detailed config for one ore
    private static void showOreConfigDialog(OreConfig config) {
        BaseDialog dialog = new BaseDialog(config.oreType.localizedName + " Configuration");

        // Store original values for cancel
        OreConfig backup = config.copy();

        dialog.cont.pane(main -> {
            main.defaults().size(650f, 60f).pad(5f);

            // Ore icon and name
            main.table(t -> {
                t.image(config.oreType.uiIcon).size(48f).padRight(15f);
                t.add(config.oreType.localizedName)
                        .color(Color.valueOf("84f491"))
                        .fontScale(1.2f);
            }).padBottom(20f).row();

            // === SPAWN SETTINGS ===
            main.add("=== Spawn Settings ===").color(Color.valueOf("84f491")).row();

            // Spawn Chance
            main.table(t -> {
                t.add("Spawn Chance: ").left().width(200f);
                t.slider(0f, 1f, 0.05f, config.spawnChance, val -> {
                    config.spawnChance = val;
                }).width(300f).padRight(10f);
                t.label(() -> String.format("%.2f", config.spawnChance)).width(50f);
            }).row();

            // Cluster Mode
            main.table(t -> {
                t.add("Cluster Mode: ").left().width(200f);
                t.check("", config.clusterMode, val -> {
                    config.clusterMode = val;
                }).padRight(10f);
                t.add("(Group ores together)").color(Color.lightGray).left();
            }).row();

            main.row();

            // === PATCH SIZE ===
            main.add("=== Patch Size ===").color(Color.valueOf("f4a584")).row();

            // Min Patch Size
            main.table(t -> {
                t.add("Min Patch Size: ").left().width(200f);
                t.slider(1, 30, 1, config.minPatchSize, val -> {
                    config.minPatchSize = (int) val;
                    if (config.minPatchSize > config.maxPatchSize) {
                        config.maxPatchSize = config.minPatchSize;
                    }
                }).width(300f).padRight(10f);
                t.label(() -> String.valueOf(config.minPatchSize)).width(50f);
            }).row();

            // Max Patch Size
            main.table(t -> {
                t.add("Max Patch Size: ").left().width(200f);
                t.slider(1, 50, 1, config.maxPatchSize, val -> {
                    config.maxPatchSize = (int) val;
                    if (config.maxPatchSize < config.minPatchSize) {
                        config.minPatchSize = config.maxPatchSize;
                    }
                }).width(300f).padRight(10f);
                t.label(() -> String.valueOf(config.maxPatchSize)).width(50f);
            }).row();

            // Density
            main.table(t -> {
                t.add("Density: ").left().width(200f);
                t.slider(0f, 1f, 0.05f, config.density, val -> {
                    config.density = val;
                }).width(300f).padRight(10f);
                t.label(() -> String.format("%.2f", config.density)).width(50f);
            }).row();

            main.row();

            // === DISTRIBUTION ===
            main.add("=== Distribution ===").color(Color.valueOf("84b8f4")).row();

            // Noise Scale
            main.table(t -> {
                t.add("Noise Scale: ").left().width(200f);
                t.slider(0.5f, 5f, 0.1f, config.noiseScale, val -> {
                    config.noiseScale = val;
                }).width(300f).padRight(10f);
                t.label(() -> String.format("%.1f", config.noiseScale)).width(50f);
            }).row();

            // Noise Threshold
            main.table(t -> {
                t.add("Noise Threshold: ").left().width(200f);
                t.slider(0f, 1f, 0.05f, config.noiseThreshold, val -> {
                    config.noiseThreshold = val;
                }).width(300f).padRight(10f);
                t.label(() -> String.format("%.2f", config.noiseThreshold)).width(50f);
            }).row();

            // Min Distance
            main.table(t -> {
                t.add("Min Distance Between Patches: ").left().width(200f);
                t.slider(5, 100, 5, config.minDistance, val -> {
                    config.minDistance = (int) val;
                }).width(300f).padRight(10f);
                t.label(() -> String.valueOf(config.minDistance)).width(50f);
            }).row();

        }).width(750f).maxHeight(550f);

        // Bottom buttons
        dialog.cont.row();
        dialog.cont.table(buttons -> {
            // Reset button
            buttons.button("Reset", Icon.refresh, () -> {
                OreConfig defaultConfig = OreConfigManager.oreConfigs
                        .find(c -> c.oreType == config.oreType);
                if (defaultConfig != null) {
                    copyConfig(defaultConfig, config);
                }
            }).size(150f, 60f).pad(10f);

            // Save button
            buttons.button("Save", Icon.ok, () -> {
                OreConfigManager.save();
                ui.showInfoToast("Ore config saved!", 2f);
                dialog.hide();
            }).size(150f, 60f).pad(10f);

            // Cancel button
            buttons.button("Cancel", Icon.cancel, () -> {
                copyConfig(backup, config); // Restore backup
                dialog.hide();
            }).size(150f, 60f).pad(10f);
        });

        dialog.show();
    }

    // Helper to copy config values
    private static void copyConfig(OreConfig from, OreConfig to) {
        to.spawnChance = from.spawnChance;
        to.minPatchSize = from.minPatchSize;
        to.maxPatchSize = from.maxPatchSize;
        to.density = from.density;
        to.noiseScale = from.noiseScale;
        to.noiseThreshold = from.noiseThreshold;
        to.minDistance = from.minDistance;
        to.clusterMode = from.clusterMode;
    }
}