package randomplanet.planets;

import mindustry.type.Planet;

public class RPPlanets {

    public static Planet irion;

    public static void load() {
        irion = new Irion();
    }

    public static void init() {
        // Initialize planets here if needed
        irion.init();
    }

}
