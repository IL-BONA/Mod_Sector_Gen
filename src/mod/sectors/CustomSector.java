package mod.sectors;

import mindustry.graphics.g3d.PlanetGrid.Ptile;
import mindustry.type.Planet;
import mindustry.type.SectorPreset;

public class CustomSector extends AbstactSector {

    public CustomSector(Planet planet, Ptile tile) {
        super(planet, tile);
    }

    public void addSectorPreset(SectorPreset preset) {
        this.preset = preset;
    }

}
