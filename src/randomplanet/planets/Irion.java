package randomplanet.planets;

import mindustry.world.meta.Attribute;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.game.Team;
import mindustry.type.Planet;
import mindustry.world.meta.Env;
import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;

public class Irion extends Planet {
    public Irion() {
        super("Irion", Planets.sun, 1f, 3);

        // === CORE SETUP ===
        localizedName = "Irion";
        description = "A hostile world consumed by toxic spore storms.";
        defaultCore = Blocks.coreBastion;
        generator = new IrionPlanetGenerator();

        // === ORBITAL MECHANICS ===
        orbitRadius = 18f;
        rotateTime = 45f * 60f; // 45 minute days
        orbitTime = Mathf.pow(orbitRadius, 1.5f) * 1000;

        // === VISUAL ===
        meshLoader = () -> new HexMesh(this, 6);
        cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("eba768").a(0.75f), 2, 0.42f, 1f, 0.43f),
                new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("eea293").a(0.75f), 2, 0.42f, 1.2f, 0.45f));

        iconColor = Color.valueOf("7d4dff");
        atmosphereColor = Color.valueOf("3c1b8f");
        atmosphereRadIn = 0.02f;
        atmosphereRadOut = 0.3f;
        landCloudColor = Pal.spore.cpy().a(0.5f);
        bloom = true;

        // === LIGHTING ===
        updateLighting = true;
        lightSrcFrom = 0.3f;
        lightSrcTo = 0.95f;

        // === ENVIRONMENT ===
        defaultEnv = Env.terrestrial | Env.spores | Env.groundOil;
        defaultAttributes.set(Attribute.spores, 2.5f);
        defaultAttributes.set(Attribute.water, 0.3f);
        defaultAttributes.set(Attribute.heat, 1.8f);

        // === GAMEPLAY ===
        launchCapacityMultiplier = 0.5f;
        allowWaves = true;
        allowWaveSimulation = true;
        allowSectorInvasion = true;
        allowLaunchSchematics = true;
        allowLaunchLoadout = true;
        enemyCoreSpawnReplace = true;
        prebuildBase = false;
        enemyBuildSpeedMultiplier = 1.3f;
        clearSectorOnLose = true;

        // === RULES ===
        ruleSetter = r -> {
            r.waveTeam = Team.crux;
            r.placeRangeCheck = false;
            r.showSpawns = true;
            r.coreDestroyClear = true;
            r.fog = false; // Add fog of war?
        };

        // === PROGRESSION ===
        startSector = 15;
        alwaysUnlocked = true;

        // Hide certain items to make it unique
        // hiddenItems.addAll(Items.sand, Items.scrap);

        // Can travel to other planets
        launchCandidates.add(Planets.serpulo);
    }
    
    // === TECH TREE ===
    @Override
    public void init() {
        super.init();

        // Optionally: Explicitly use Serpulo's tree
        if (techTree == null) {
            techTree = TechTree.roots.find(n -> n.planet == Planets.serpulo);
        }
    }

}
