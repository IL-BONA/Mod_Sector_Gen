package mod.planets;

import mindustry.world.Block;
import arc.struct.Seq;

public class GenerationParams {
    public final Block block;
    public final int iterations;
    public final int percentChance;
    public final int seed;
    public final boolean requiresEmptySpace;
    public final Seq<Block> incompatibleWith;

    public GenerationParams(Block block, int iterations, int percentChance, int seed, boolean requiresEmptySpace) {
        this(block, iterations, percentChance, seed, requiresEmptySpace, new Seq<>());
    }

    public GenerationParams(Block block, int iterations, int percentChance, int seed, boolean requiresEmptySpace, Seq<Block> incompatibleWith) {
        this.block = block;
        this.iterations = iterations;
        this.percentChance = percentChance;
        this.seed = seed;
        this.requiresEmptySpace = requiresEmptySpace;
        this.incompatibleWith = incompatibleWith;
    }

    public static class Builder {
        private Block block;
        private int iterations = 4;
        private int percentChance = 7;
        private int seed = 0;
        private boolean requiresEmptySpace = false;
        private Seq<Block> incompatibleWith = new Seq<>();

        public Builder block(Block block) {
            this.block = block;
            return this;
        }

        public Builder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder percentChance(int percentChance) {
            this.percentChance = percentChance;
            return this;
        }

        public Builder seed(int seed) {
            this.seed = seed;
            return this;
        }

        public Builder requiresEmptySpace(boolean requiresEmptySpace) {
            this.requiresEmptySpace = requiresEmptySpace;
            return this;
        }

        public Builder incompatibleWith(Block... blocks) {
            this.incompatibleWith.addAll(blocks);
            return this;
        }

        public GenerationParams build() {
            if (block == null) {
                throw new IllegalStateException("Block must be specified");
            }
            return new GenerationParams(block, iterations, percentChance, seed, requiresEmptySpace, incompatibleWith);
        }
    }
} 