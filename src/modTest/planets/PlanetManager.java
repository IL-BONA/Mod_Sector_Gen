package modTest.planets;

import mindustry.type.Planet;

public class PlanetManager {

    private static PlanetManager instance;

    public static PlanetManager getInstance() {
        if (instance == null) {
            instance = new PlanetManager();
        }
        return instance;
    }

    private PlanetManager() {
        
    }

}
