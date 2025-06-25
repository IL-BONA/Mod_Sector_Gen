package RandomPlanet.planets;

import mindustry.type.Planet;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.content.SerpuloTechTree;
import mindustry.content.UnitTypes;
import mindustry.game.Objectives.Research;
import mindustry.game.Objectives.SectorComplete;
import mindustry.game.Team;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;

public class Irion extends Planet {
    public Irion() {
        super("Irion", Planets.sun, 1f, 3);
        generator = new IrionPlanetGenerator();
        meshLoader = () -> new HexMesh(this, 6);
        /*cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(Pal.spore).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f,
                        0.38f),
                new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.75f), 2, 0.45f, 1f,
                        0.41f));*/
        cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("eba768").a(0.75f), 2, 0.42f, 1f, 0.43f),
                new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("eea293").a(0.75f), 2, 0.42f, 1.2f, 0.45f)
            );
        
        launchCapacityMultiplier = 0.5f;
        allowWaves = true;
        allowWaveSimulation = true;
        allowSectorInvasion = true;
        allowLaunchSchematics = true;
        enemyCoreSpawnReplace = true;
        allowLaunchLoadout = true;
        // doesn't play well with configs
        prebuildBase = false;
        ruleSetter = r -> {
            r.waveTeam = Team.crux;
            r.placeRangeCheck = false;
            r.showSpawns = true;
            r.coreDestroyClear = true;
        };
        iconColor = Color.valueOf("7d4dff");
        atmosphereColor = Color.valueOf("3c1b8f");
        atmosphereRadIn = 0.02f;
        atmosphereRadOut = 0.3f;
        startSector = 15;
        alwaysUnlocked = true;
        landCloudColor = Pal.spore.cpy().a(0.5f);

        // Vedere il techtree
    }

}
