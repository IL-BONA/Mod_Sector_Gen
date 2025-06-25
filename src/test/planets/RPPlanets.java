package test.planets;

import mindustry.content.Planets;
import mindustry.type.Planet;
import test.utils.RPPlanet;

public class RPPlanets {

    public static Planet irion;

    public static void load() {
        irion = RPPlanet.createDefaultPlanet("Irion", Planets.sun, 1f, 3);
    }

    public static void init() {
        // Initialize planets here if needed
        irion.init();
    }

}
