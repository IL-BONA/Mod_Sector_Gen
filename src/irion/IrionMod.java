package irion;

import arc.*;
import arc.util.*;
import arc.graphics.Color;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.world.blocks.storage.*;
import mindustry.graphics.g3d.*;
import mindustry.maps.planet.*;

public class IrionMod extends Mod {
    public static Planet irion;

    public IrionMod() {
        Log.info("Loaded IrionMod constructor.");
        
        Events.on(ClientLoadEvent.class, e -> {
            // Load custom planet when game loads
            loadContent();
        });
    }

    @Override
    public void loadContent() {
        irion = new Planet("irion", Planets.sun, 1f, 3) {{
            generator = new IrionPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            atmosphereColor = Color.valueOf("3c1b8f");
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            startSector = 15;
            alwaysUnlocked = true;
            landCloudColor = Color.valueOf("532b9f");
            orbitRadius = 85f;
            orbitTime = 120f;
            rotateTime = 30f;
            accessible = true;
            updateLighting = true;
            bloom = true;
            visible = true;
            hasAtmosphere = true;
            allowWaveSimulation = true;
            allowLaunchToNumbered = true;
            clearSectorOnLose = true;
            allowWaves = true;
            defaultCore = Blocks.coreShard;
            hiddenItems.addAll(Items.serpuloItems).removeAll(Items.erekirItems);
            allowLaunchLoadout = true;
            allowLaunchSchematics = true;
            allowSectorInvasion = true;
            allowWaveSimulation = true;
        }};
    }
} 