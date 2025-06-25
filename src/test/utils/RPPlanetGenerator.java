package test.utils;

import mindustry.maps.generators.BlankPlanetGenerator;
import mindustry.maps.generators.PlanetGenerator;

public class RPPlanetGenerator {

    public static PlanetGenerator createDefaultPlanetGenerator() {
        PlanetGenerator ret = new BlankPlanetGenerator();

        return ret;
    }

    public static PlanetGenerator createBlankPlanetGenerator() {
        return new BlankPlanetGenerator();
    }

}
