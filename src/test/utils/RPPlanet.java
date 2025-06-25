package test.utils;

import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;

public class RPPlanet {

    public static Planet createDefaultPlanet(String name, Planet parent, float radius, int sectorSize) {
        Planet ret = new Planet(name, parent, radius, sectorSize) {
            {
                // Whether this planet is listed in the planet access UI.
                accessible = true;
                // If true, waves are created on sector loss.
                allowWaves = true;
                // Mesh used for rendering planet clouds.
                // Null if no clouds are present.
                cloudMesh = null;
                // Default core block for launching.
                // defaultCore = Blocks.coreShard;
                // Whether to draw the orbital circle.
                drawOrbit = true;
                // Generator that will make the planet.
                // Can be null for planets that don't need to be landed on.
                // generator = RPPlanetGenerator.createDefaultPlanetGenerator();
                generator = new SerpuloPlanetGenerator();
                // Grid used for the sectors on the planet.
                // Null if this planet can't be landed on.
                // grid = (ret.grid == null) ? PlanetGrid.create(sectorSize) : ret.grid;
                // Whether this planet has an atmosphere.
                // hasAtmosphere = true;
                // Icon as displayed in the planet selection dialog.
                // This is a string, as drawables are null at load time.
                // icon = null;
                // Sets up rules on game load for any sector on this planet.
                // ruleSetter = r -> {};
                // Seed for sector base generation on this planet.
                // sectorSeed = 0;
                // The root parent of the whole solar system this planet is in.
                // solarSystem = parent.solarSystem;
                // The default starting sector displayed to the map dialog.
                // startSector = 0;
                // Default root node shown when the tech tree is opened here.
                // techTree = null;
                // Content (usually planet-specific) that is unlocked upon landing here.
                // unlockedOnLand = new Seq<>();
                // Whether this planet is displayed.
                // if false, it will not be draw in the map, but it exist
                // visible = true;
                // Whether this content is always unlocked in the tech tree.
                // But apparently it also serves to make the planet visible. Without it is useless visible.
                alwaysUnlocked = true;
            }
        };
        return ret;
    }

    public static Planet createBlankPlanet(String name, Planet parent, float radius, int sectorSize) {
        return new Planet(name, parent, radius, sectorSize);
    }

}
