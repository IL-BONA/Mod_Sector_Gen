package mod.planets;

import arc.struct.ObjectMap;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.content.SerpuloTechTree;
import mindustry.content.TechTree.TechNode;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;

import static mindustry.content.TechTree.*;
import static mindustry.content.SectorPresets.*;

public class IrionTechTree {
    private static Irion planet;

    public static void load(Irion irionPlanet) {
        planet = irionPlanet;

        // Imposta il pianeta per tutti i nodi
        Planets.serpulo.techTree.planet = planet;
        Planets.serpulo.techTree.children.each(c -> c.planet = planet);
    }
} 