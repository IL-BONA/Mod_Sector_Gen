package RandomPlanet.utils;

import mindustry.graphics.g3d.HexMesh;
import mindustry.type.Planet;

public class RPPlanet {

    public static Planet createDefaultPlanet(String name, Planet parent, float radius, int sectorSize) {
        Planet ret = new Planet(name, parent, radius, sectorSize) {
            {
                // Generator that will make the planet.
                // Can be null for planets that don't need to be landed on.
                // If you want to use a this.mashloader, first create a planet generator
                generator = new RPPlanetGenerator();
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
                startSector = 255;
                // Default root node shown when the tech tree is opened here.
                // techTree = null;
                // Content (usually planet-specific) that is unlocked upon landing here.
                // unlockedOnLand = new Seq<>();
                // Whether this planet is displayed.
                // if false, it will not be draw in the map, but it exist
                // visible = true;
                // Whether this content is always unlocked in the tech tree.
                // But apparently it also serves to make the planet visible. Without it is
                // useless visible.
                alwaysUnlocked = true;
                // Loads the mesh. Clientside only. Defaults to a boring sphere mesh.
                meshLoader = () -> new HexMesh(this, 6);
                // Loads the mesh. Clientside only. Defaults to a boring sphere mesh.
                // cloudMeshLoader
                orbitTime = 60f;
            }
        };
        return ret;
    }

}
