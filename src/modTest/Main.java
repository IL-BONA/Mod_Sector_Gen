package modTest;

import arc.util.Log;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import modTest.planets.Irion;

public class Main extends Mod {

    public Main() {
        Log.info("Loading Test Mod...");
    }

    @Override
    public void init() {
        Log.info("Initializing Test Mod...");
        try {
            // Add your initialization code here
            
        } catch(Exception e) {
            Log.err("Test Mod failed to initialize", e);
        }
    }

    @Override
    public void loadContent() {
        Log.info("Loading Test Mod content...");
        Planet irion = new Irion();
        irion.visible = true;
        irion.init();
    }
}
