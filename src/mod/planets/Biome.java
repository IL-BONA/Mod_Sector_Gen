package mod.planets;

import mindustry.content.Blocks;
import mindustry.world.Block;
import arc.struct.Seq;
import arc.math.Mathf;
import arc.util.noise.Simplex;

public class Biome {
    public final String name;
    public final Block primaryFloor;
    public final Block secondaryFloor;
    public final Block overlay;
    public final float temperature;
    public final float humidity;
    public final float elevation;
    public final Seq<Block> structures;
    public final float noiseScale;
    public final int seed;

    public Biome(String name, Block primaryFloor, Block secondaryFloor, Block overlay, 
                float temperature, float humidity, float elevation, 
                Seq<Block> structures, float noiseScale, int seed) {
        this.name = name;
        this.primaryFloor = primaryFloor;
        this.secondaryFloor = secondaryFloor;
        this.overlay = overlay;
        this.temperature = temperature;
        this.humidity = humidity;
        this.elevation = elevation;
        this.structures = structures;
        this.noiseScale = noiseScale;
        this.seed = seed;
    }

    public static class Builder {
        private String name;
        private Block primaryFloor;
        private Block secondaryFloor;
        private Block overlay;
        private float temperature = 0.5f;
        private float humidity = 0.5f;
        private float elevation = 0.5f;
        private Seq<Block> structures = new Seq<>();
        private float noiseScale = 1f;
        private int seed = 0;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder primaryFloor(Block floor) {
            this.primaryFloor = floor;
            return this;
        }

        public Builder secondaryFloor(Block floor) {
            this.secondaryFloor = floor;
            return this;
        }

        public Builder overlay(Block overlay) {
            this.overlay = overlay;
            return this;
        }

        public Builder temperature(float temp) {
            this.temperature = temp;
            return this;
        }

        public Builder humidity(float hum) {
            this.humidity = hum;
            return this;
        }

        public Builder elevation(float elev) {
            this.elevation = elev;
            return this;
        }

        public Builder addStructure(Block structure) {
            this.structures.add(structure);
            return this;
        }

        public Builder noiseScale(float scale) {
            this.noiseScale = scale;
            return this;
        }

        public Builder seed(int seed) {
            this.seed = seed;
            return this;
        }

        public Biome build() {
            if (name == null || primaryFloor == null) {
                throw new IllegalStateException("Name and primary floor must be specified");
            }
            return new Biome(name, primaryFloor, secondaryFloor, overlay, 
                           temperature, humidity, elevation, structures, noiseScale, seed);
        }
    }

    public float getNoiseValue(float x, float y) {
        return Simplex.noise2d(seed, 4, 0.5f, noiseScale, x, y);
    }

    public Block getFloorBlock(float x, float y) {
        float noise = getNoiseValue(x, y);
        return noise > 0.5f ? primaryFloor : secondaryFloor;
    }

    public boolean shouldPlaceStructure(float x, float y) {
        float noise = getNoiseValue(x, y);
        return Math.abs(noise - 0.5f) < 0.1f;
    }

    public static Seq<Biome> createDefaultBiomes() {
        Seq<Biome> biomes = new Seq<>();

        // Deserto
        biomes.add(new Biome.Builder()
            .name("Desert")
            .primaryFloor(Blocks.sand)
            .secondaryFloor(Blocks.darksand)
            .overlay(Blocks.oreCopper)
            .temperature(0.8f)
            .humidity(0.2f)
            .elevation(0.3f)
            .addStructure(Blocks.sandBoulder)
            .noiseScale(0.02f)
            .build());

        // Foresta
        biomes.add(new Biome.Builder()
            .name("Forest")
            .primaryFloor(Blocks.grass)
            .secondaryFloor(Blocks.dirt)
            .overlay(Blocks.oreLead)
            .temperature(0.6f)
            .humidity(0.7f)
            .elevation(0.5f)
            .addStructure(Blocks.pine)
            .addStructure(Blocks.sporePine)
            .noiseScale(0.03f)
            .build());

        // Montagne
        biomes.add(new Biome.Builder()
            .name("Mountains")
            .primaryFloor(Blocks.stone)
            .secondaryFloor(Blocks.stone)
            .overlay(Blocks.oreCoal)
            .temperature(0.4f)
            .humidity(0.5f)
            .elevation(0.8f)
            .addStructure(Blocks.boulder)
            .noiseScale(0.04f)
            .build());

        // Palude
        biomes.add(new Biome.Builder()
            .name("Swamp")
            .primaryFloor(Blocks.moss)
            .secondaryFloor(Blocks.sporeMoss)
            .overlay(Blocks.oreThorium)
            .temperature(0.7f)
            .humidity(0.9f)
            .elevation(0.2f)
            .addStructure(Blocks.sporeCluster)
            .noiseScale(0.025f)
            .build());

        return biomes;
    }
} 