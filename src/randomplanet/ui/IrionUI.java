package randomplanet.ui;

import arc.Events;
import arc.scene.ui.Dialog;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.ui.Styles;
import randomplanet.planets.RPPlanets;

public class IrionUI {

    public static void init() {
        // Wait for the client to load so UI is initialized
        Events.on(ClientLoadEvent.class, e -> {
            build();
        });
    }

    public static void build() {
        // 1. Create the detailed menu (Dialog)
        Dialog irionMenu = new Dialog("Irion Controls");

        // This 'cont' is the main Table of the dialog where you add content
        Table content = irionMenu.cont;

        content.add("Irion Command Station").fontScale(1.5f).pad(10f).row();
        if (RPPlanets.irion.uiIcon != null) {
            content.image(RPPlanets.irion.uiIcon).size(80f).pad(20f).row();
        }

        // TEXT BOX (TextField)
        content.add("Enter Coordinates:").left().padTop(10f).row();
        TextField inputField = new TextField();
        inputField.setMessageText("x, y");
        content.add(inputField).width(300f).padBottom(10f).row();

        // BUTTONS
        content.table(buttons -> {
            buttons.button("Launch", Styles.defaultt, () -> {
                String text = inputField.getText();
                Vars.ui.showInfo("Launching to: " + text);
            }).size(120f, 50f).pad(5f);

            buttons.button("Cancel", Styles.defaultt, irionMenu::hide)
                    .size(120f, 50f).pad(5f);
        }).row();

        // 2. Add an overlay button directly to the Planet Dialog
        // This ensures it moves with the dialog and shows/hides automatically
        Vars.ui.planet.shown(() -> {
            // We can check or rebuild here if needed, but adding a permanent child is
            // simpler
        });

        // Create a wrapper table for alignment
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.top().right(); // Align to top-right of the planet screen

        overlay.button("Irion Menu", Styles.defaultt, irionMenu::show)
                .size(150f, 50f)
                .padTop(60f) // Avoid the back button and title
                .padRight(10f);

        // Add to the PlanetDialog group. This ensures it renders on top of the map.
        Vars.ui.planet.addChild(overlay);
    }
}
