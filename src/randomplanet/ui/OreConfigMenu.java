package randomplanet.ui;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import mindustry.game.EventType.*;
import mindustry.gen.Icon;
import mindustry.type.Sector;
import mindustry.ui.dialogs.BaseDialog;
import randomplanet.planets.RPPlanets;
import randomplanet.sectors.OreConfig;
import randomplanet.sectors.OreConfigManager;
import randomplanet.sectors.OreGenerator;

import static mindustry.Vars.*;

public class OreConfigMenu {

    // Store the currently selected sector context (null = global)
    private static Sector selectedSector = null;

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
            // Reset selection to current game context or null
            if (state.isGame()) {
                selectedSector = state.rules.sector;
            } else {
                selectedSector = null;
            }
            // Load appropriate configs
            OreConfigManager.load(selectedSector);
            showOreListDialog();
        });
    }

    // Show list of all ores
    private static void showOreListDialog() {
        String title = selectedSector != null ? "Ore Config [Sector " + selectedSector.id + "]" : "Ore Config [Global]";

        BaseDialog dialog = new BaseDialog(title);

        // Override toggle
        final boolean[] override = {
                selectedSector != null && Core.settings.getBool("sector-" + selectedSector.id + "-overridden", false) };

        dialog.cont.pane(list -> {

            // Header: Target Selection
            list.table(t -> {
                t.add("Target Context: ").left();
                t.button(selectedSector == null ? "Global" : "Sector " + selectedSector.id, Icon.map, () -> {
                    showSectorSelectDialog(dialog);
                }).size(200f, 50f).padLeft(10f);
            }).padBottom(10f).row();

            // Override toggle (if sector selected)
            if (selectedSector != null) {
                list.table(t -> {
                    t.check("Override Global Settings", override[0], val -> {
                        override[0] = val;
                        // Reload state to refresh UI enabled/disabled
                        dialog.hide();
                        // Save the override state immediately so re-opening works
                        OreConfigManager.save(selectedSector, val);
                        // Reload configs to reflect new state (global or sector)
                        OreConfigManager.load(selectedSector);
                        showOreListDialog();
                    }).padBottom(10f);
                }).row();
            }

            list.defaults().size(600f, 70f).pad(5f);

            for (OreConfig config : OreConfigManager.oreConfigs) {
                list.button(config.oreType.localizedName, () -> {
                    showOreConfigDialog(config, selectedSector, override[0]);
                }).disabled(b -> selectedSector != null && !override[0])
                        .with(b -> {
                            b.left();
                            b.image(config.oreType.uiIcon).size(40f).padRight(10f);
                            if (selectedSector != null && !override[0]) {
                                b.setColor(Color.darkGray);
                            }
                        }).row();
            }
        }).grow();

        dialog.cont.row();
        dialog.cont.table(buttons -> {

            // Regenerate Button (In-Game Only & Matching Sector)
            boolean canRegen = state.isGame() && state.rules.sector == selectedSector && selectedSector != null;

            if (canRegen) {
                buttons.button("Regenerate Ores", Icon.refresh, () -> {
                    // Confirm dialog
                    BaseDialog confirm = new BaseDialog("Confirm Regeneration");
                    confirm.cont.add("Are you sure? This will replace all ores on the map.").row();
                    confirm.cont.button("Cancel", confirm::hide).size(100f, 50f);
                    confirm.cont.button("Regenerate", Icon.warning, () -> {
                        confirm.hide();
                        dialog.hide();

                        // Save current state first
                        OreConfigManager.save(selectedSector, override[0]);

                        // Regenerate
                        OreGenerator generator = new OreGenerator(world.tiles, selectedSector.id);
                        generator.clearOres();
                        generator.generateOres(selectedSector.id, OreConfigManager.oreConfigs.toArray(OreConfig.class));

                        ui.showInfoToast("Ores Regenerated!", 3f);
                    }).size(150f, 50f);
                    confirm.show();
                }).size(200f, 60f).pad(10f).color(Color.scarlet);
            }

            buttons.button("Reset All", Icon.refresh, () -> {
                if (selectedSector != null) {
                    // Revert to global
                    override[0] = false;
                    OreConfigManager.save(selectedSector, false);
                    OreConfigManager.load(selectedSector);
                    ui.showInfoToast("Reverted to Global Configs", 3f);
                } else {
                    OreConfigManager.reset();
                    OreConfigManager.save();
                    ui.showInfoToast("Global Configs Reset", 3f);
                }
                dialog.hide();
                showOreListDialog();
            }).size(180f, 60f).pad(10f);

            buttons.button("Close", Icon.cancel, dialog::hide)
                    .size(150f, 60f).pad(10f);
        });

        dialog.show();
    }

    // Dialog to select a sector
    private static void showSectorSelectDialog(BaseDialog parentDialog) {
        BaseDialog dialog = new BaseDialog("Select Target");
        dialog.addCloseButton();

        dialog.cont.pane(pane -> {
            pane.defaults().width(400f).height(60f).pad(5f);

            // Global Option
            pane.button("Global Configuration", Icon.planet, () -> {
                selectedSector = null;
                OreConfigManager.load(null);
                dialog.hide();
                parentDialog.hide();
                showOreListDialog();
            }).row();

            // Separator
            pane.image().color(Color.gray).height(4f).fillX().pad(10f).row();

            if (RPPlanets.irion != null && RPPlanets.irion.sectors != null) {
                // List sectors
                for (Sector s : RPPlanets.irion.sectors) {
                    pane.button("Sector " + s.id, Icon.map, () -> {
                        selectedSector = s;
                        OreConfigManager.load(s);
                        dialog.hide();
                        parentDialog.hide();
                        showOreListDialog();
                    }).row();
                }
            } else {
                pane.add("No sectors available").color(Color.lightGray);
            }
        }).grow();

        dialog.show();
    }

    // Show detailed config for one ore
    private static void showOreConfigDialog(OreConfig config, Sector sector, boolean canEdit) {
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
                }).width(300f).padRight(10f).disabled(!canEdit);
                t.label(() -> String.format("%.2f", config.spawnChance)).width(50f);
            }).row();

            // Cluster Mode
            main.table(t -> {
                t.add("Cluster Mode: ").left().width(200f);
                t.check("", config.clusterMode, val -> {
                    config.clusterMode = val;
                }).padRight(10f).disabled(!canEdit);
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
                }).width(300f).padRight(10f).disabled(!canEdit);
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
                }).width(300f).padRight(10f).disabled(!canEdit);
                t.label(() -> String.valueOf(config.maxPatchSize)).width(50f);
            }).row();

            // Density
            main.table(t -> {
                t.add("Density: ").left().width(200f);
                t.slider(0f, 1f, 0.05f, config.density, val -> {
                    config.density = val;
                }).width(300f).padRight(10f).disabled(!canEdit);
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
                }).width(300f).padRight(10f).disabled(!canEdit);
                t.label(() -> String.format("%.1f", config.noiseScale)).width(50f);
            }).row();

            // Noise Threshold
            main.table(t -> {
                t.add("Noise Threshold: ").left().width(200f);
                t.slider(0f, 1f, 0.05f, config.noiseThreshold, val -> {
                    config.noiseThreshold = val;
                }).width(300f).padRight(10f).disabled(!canEdit);
                t.label(() -> String.format("%.2f", config.noiseThreshold)).width(50f);
            }).row();

            // Min Distance
            main.table(t -> {
                t.add("Min Distance Between Patches: ").left().width(200f);
                t.slider(5, 100, 5, config.minDistance, val -> {
                    config.minDistance = (int) val;
                }).width(300f).padRight(10f).disabled(!canEdit);
                t.label(() -> String.valueOf(config.minDistance)).width(50f);
            }).row();

        }).width(750f).maxHeight(550f);

        // Bottom buttons
        dialog.cont.row();
        dialog.cont.table(buttons -> {
            // Reset button
            buttons.button("Reset", Icon.refresh, () -> {
                if (!canEdit)
                    return;
                OreConfig defaultConfig = OreConfigManager.oreConfigs
                        .find(c -> c.oreType == config.oreType);
                if (defaultConfig != null) {
                    copyConfig(defaultConfig, config);
                }
            }).size(150f, 60f).pad(10f).disabled(!canEdit);

            // Save button
            buttons.button("Save", Icon.ok, () -> {
                if (!canEdit) {
                    dialog.hide();
                    return;
                }
                OreConfigManager.save(sector, true); // True because we are editing
                ui.showInfoToast("Ore config saved!", 2f);
                dialog.hide();
            }).size(150f, 60f).pad(10f).disabled(!canEdit);

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