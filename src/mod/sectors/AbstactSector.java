package mod.sectors;

import mindustry.graphics.g3d.PlanetGrid.Ptile;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;

public abstract class AbstactSector extends Sector{

    public AbstactSector(Planet planet, Ptile tile) {
        super(planet, tile);
    }

    public void setSectorPreset(SectorPreset preset) {
        this.preset = preset;
    }

    

}
