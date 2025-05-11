package modTest;

import arc.Events;
import arc.util.Log;
import mindustry.game.EventType.*;
import mindustry.mod.Mod;
import modTest.planets.Irion;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.type.Planet;
import mindustry.content.Items;
import mindustry.type.ItemStack;

public class Main extends Mod {
    public static Planet irion;

    public Main() {
        Log.info("Loading Test Mod constructor...");
    }

    @Override
    public void init() {
        Log.info("Initializing Test Mod...");
    }

    @Override
    public void loadContent() {
        Log.info("Loading Test Mod content...");
        
        // Create and initialize planet
        irion = new Irion();
        
        // Set up planet requirements and tech tree
        Events.on(ClientLoadEvent.class, e -> {
            Log.info("Client loaded, initializing planet...");
            
            // Initialize the planet and its mesh
            if(irion != null) {
                irion.load();
                irion.mesh = irion.meshLoader.get();
                
                // Add the planet to the tech tree
                TechTree.TechNode parentNode = TechTree.all.find(n -> n.content == Planets.sun);
                if(parentNode != null){
                    new TechTree.TechNode(parentNode, irion, ItemStack.empty);
                }
            }
        });
    }
}
