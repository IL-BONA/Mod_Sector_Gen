package mod.sectors;

import javax.naming.spi.DirectoryManager;

import mindustry.graphics.g3d.PlanetGrid.Ptile;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;

public class CustomSector extends Sector {

    public CustomSector(Planet planet, Ptile tile) {
        super(planet, tile);
    }

    public void addSectorPreset(SectorPreset preset) {
        this.preset = preset;
    }

    private void generatePtile(Ptile tilePlanet) {
        
    }

}
