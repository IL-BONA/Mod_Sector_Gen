package modTest.planets;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.graphics.g3d.HexMesh;
import mindustry.type.Planet;
import mindustry.content.Blocks;
import mindustry.type.Sector;
import mindustry.content.Items;
import mindustry.type.ItemStack;
import mindustry.game.Team;

public class Irion extends Planet {
    public Irion() {
        // Make sure to set Planets.sun as the parent
        super("irion", Planets.sun, 1f);
        
        generator = new IrionPlanetGenerator();
        meshLoader = () -> new HexMesh(this, 6);
        
        // Planet properties
        hasAtmosphere = true;
        updateLighting = true;
        bloom = true;
        visible = true;
        accessible = true;
        alwaysUnlocked = true;  // Temporarily set to true for testing
        allowWaves = true;
        allowWaveSimulation = true;
        allowSectorInvasion = true;
        allowLaunchLoadout = true;
        
        // Atmosphere properties
        atmosphereColor = Color.valueOf("3c1b8f");
        atmosphereRadIn = 0.02f;
        atmosphereRadOut = 0.3f;
        
        // Orbit properties
        orbitRadius = 12;
        orbitTime = 2f * 60;
        rotateTime = 24f;
        startSector = 5;
        
        // Planet physics
        radius = 1.2f;
        tidalLock = false;
        clearSectorOnLose = true;
        defaultCore = Blocks.coreShard;
        
        // Sector configuration
        sectorSeed = 1;
        allowSectorInvasion = true;
        
        // Launch configuration
        launchCapacityMultiplier = 0.5f;
        
        // Visual properties
        landCloudColor = Color.valueOf("532a9e");
        iconColor = Color.valueOf("3c1b8f");
    }
    public boolean isLandable(Sector sector){
        return accessible && sector != null && sector.hasBase() && sector.threat <= 0;
    }
    
}