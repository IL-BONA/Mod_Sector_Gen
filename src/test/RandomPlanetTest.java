package test;

import arc.util.Log;
import mindustry.mod.Mod;
import test.planets.RPPlanets;

public class RandomPlanetTest extends Mod {

    public RandomPlanetTest() {
        Log.info("[RandomPlanet] Loading RandomPlanet Mod...");
    }

    @Override
    public void init() {
        // Initialization code here
        Log.info("[RandomPlanet] Initializing RandomPlanet Mod...");

        
    }

    @Override
    public void loadContent() {
        // Load content here
        Log.info("[RandomPlanet] Loading RandomPlanet Mod content...");

        Log.info("[RandomPlanet] Loading planets...");
        RPPlanets.load();

        Log.info("[RandomPlanet] Initializing planets...");
        RPPlanets.init();
    }

}
