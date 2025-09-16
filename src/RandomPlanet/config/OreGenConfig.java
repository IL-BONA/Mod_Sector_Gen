package RandomPlanet.config;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.world.Block;

public class OreGenConfig {

    // == TYPES == //
    public static final String TYPE_DEFAULT = "default";
    public static final String TYPE_JUNKYARD = "junkyard";
    public static final String TYPE_CRATER = "crater";
    public static final String TYPE_MAGMATIC = "magmatic";
    public static final String[] TYPES = {TYPE_DEFAULT, TYPE_JUNKYARD, TYPE_CRATER, TYPE_MAGMATIC};

    // == SHAPES == //
    public static final String SHAPE_CIRCULAR = "circular";
    public static final String SHAPE_OVAL = "oval";
    public static final String SHAPE_TRIANGLE_N = "trianglen";
    public static final String SHAPE_TRIANGLE_S = "triangles";
    public static final String SHAPE_TRIANGLE_E = "trianglee";
    public static final String SHAPE_TRIANGLE_O = "triangleo";
    public static final String SHAPE_TRIANGLE_NE = "trianglene";
    public static final String SHAPE_TRIANGLE_NW = "trianglenw";
    public static final String SHAPE_TRIANGLE_SE = "trianglese";
    public static final String SHAPE_TRIANGLE_SW = "trianglesw";
    public static final String[] SHAPES = {
        SHAPE_CIRCULAR, SHAPE_OVAL, SHAPE_TRIANGLE_N, SHAPE_TRIANGLE_S,
        SHAPE_TRIANGLE_E, SHAPE_TRIANGLE_O, SHAPE_TRIANGLE_NE,
        SHAPE_TRIANGLE_NW, SHAPE_TRIANGLE_SE, SHAPE_TRIANGLE_SW
    };

    // === Sector Settings ===
    public boolean specialSector = false;
    private String sectorType = TYPE_DEFAULT;       

    // === Patch Shape & Noise Settings ===
    private String patchShape = SHAPE_CIRCULAR;
    private float patchDensity = 0.5f;
    private float noiseScale = 10f;
    private float noiseThreshold = 0.4f;
    private int noiseSeed = 42;
    private int patchMinSize = 4;
    private int patchMaxSize = 12;
    private boolean allowMixedOres = false;
    private int minPatchDistance = 6; // Minimum distance between patches

    private Seq<Block> allowedOres = Seq.with(Blocks.oreCopper, Blocks.oreLead);
    private ObjectMap<Block, Float> oreThresholdOverrides = new ObjectMap<>();
    private float shapeSmoothness = 0.8f;
    private boolean respectTerrain = true;
    private boolean avoidCenter = true;
    private int centerAvoidRadius = 12;

    // === Mode & Pattern Parameters ===
    private int modeType = 0; // 0=cluster, 1=random, 2=veins, 3=noise-based, 4=scattered

    // Cluster Mode
    private float clusterSpread = 8f;
    private int clustersPerRegion = 3;

    // Vein Mode
    private float veinLength = 15f;
    private float veinThickness = 1.2f;
    private float veinCurvature = 0.3f;
    private int maxVeinsPerArea = 2;

    // Scattered Mode
    private float scatterRadius = 12f;
    private int scatterCount = 8;

    // Noise Mode
    private float noiseOctaves = 4f;
    private float noisePersistence = 0.6f;

    // Extra Controls
    private float edgeFalloff = 0.1f;
    private boolean avoidWater = true;
    private boolean preferElevation = false;
    private float elevationBias = 0.0f;




    public static OreGenConfig create() {
        return new OreGenConfig();
    }

    private OreGenConfig() {
    }

    // === Helper: Should Place Ore ===
    /*public boolean shouldPlaceOre(int x, int y, Block ore) {
        float threshold = oreThresholdOverrides.get(ore, noiseThreshold);
        float noise = Simplex.noise2d(noiseSeed, x / noiseScale, y / noiseScale, 0.0, 0.0, 0.0);
        return noise > threshold;
    }*/

    // === Helper: Shape Mask Multiplier ===
    public float shapeMask(int centerX, int centerY, int x, int y) {
        float dx = x - centerX;
        float dy = y - centerY;
        float base = 1f;

        switch (patchShape.toLowerCase()) {
            case SHAPE_CIRCULAR: {
                float dist = Mathf.dst(dx, dy);
                base = Mathf.clamp(1f - dist / patchMaxSize, 0f, 1f);
                break;
            }
            case SHAPE_OVAL: {
                float rx = patchMaxSize / 2f;
                float ry = patchMinSize / 2f;
                base = Mathf.clamp(1f - (dx * dx) / (rx * rx) - (dy * dy) / (ry * ry), 0f, 1f);
                break;
            }
            case SHAPE_TRIANGLE_N: {
                base = dy < 0 ? 0f : 1f - (Math.abs(dx) + dy) / patchMaxSize;
                break;
            }
            // TODO: Add triangleE, triangleS, triangleW, NE, NW etc.
            default: break;
        }

        // Apply smoothness falloff
        return Mathf.pow(base, shapeSmoothness);
    }

    // === Helper: Avoid Center Mask ===
    public boolean isOutsideCenter(int x, int y, int mapWidth, int mapHeight) {
        if (!avoidCenter) return true;
        int cx = mapWidth / 2;
        int cy = mapHeight / 2;
        return Mathf.dst(x, y, cx, cy) > centerAvoidRadius;
    }

    // === Helper: Check Patch Distance ===
    /**
     * Checks if a new patch at (x, y) is at least minPatchDistance away from all existing patches.
     * @param x X coordinate of new patch
     * @param y Y coordinate of new patch
     * @param existingPatches Seq of int[2] arrays representing patch centers
     * @return true if valid, false if too close to another patch
     */
    public boolean isPatchDistanceValid(int x, int y, Seq<int[]> existingPatches) {
        for(int[] pos : existingPatches){
            if(Mathf.dst(x, y, pos[0], pos[1]) < minPatchDistance){
                return false;
            }
        }
        return true;
    }

    // === Mode Description ===
    public String getModeDescription() {
        switch (modeType) {
            case 0: return "Cluster Mode";
            case 1: return "Random Mode";
            case 2: return "Vein Mode";
            case 3: return "Noise Mode";
            case 4: return "Scattered Mode";
            default: return "Unknown Mode";
        }
    }

    public Seq<Block> getAllowedOres() {
        return allowedOres;
    }

    

    /*public static OreGenConfig generateRandomOreGenConfig(Block oreType, long seed, boolean chaosMode) {
        OreGenConfig config = new OreGenConfig();
        Random rng = new Random(seed ^ oreType.name.hashCode());

        boolean specwialSector = rng.nextBoolean();
        String sectorType = TYPES[rng.nextInt(TYPES.length)];

        String patchShape = SHAPES[rng.nextInt(SHAPES.length)];

        float patchDensity = chaosMode ? rng.nextFloat() : 0.5f + rng.nextFloat() * 0.5f;
        float noiseScale = chaosMode ? rng.nextFloat() * 30f : 5f + rng.nextFloat() * 10f;
        float noiseThreshold = chaosMode ? rng.nextFloat() * 2f - 1f : -0.2f + rng.nextFloat() * 0.4f;



        return new OreGenConfig(
            specialSector,
            sectorType,
            patchShape,
            patchDensity,
            noiseScale,
            noiseThreshold,
            seed
        );
    }

    public static OreGenConfig generateRandomOreGenConfig(long seed, boolean chaosMode) {
        Random rng = new Random(seed);

        boolean specialSector = rng.nextBoolean();
        String sectorType = TYPES[rng.nextInt(TYPES.length)];

        String patchShape = SHAPES[rng.nextInt(SHAPES.length)];

        float patchDensity = chaosMode ? rng.nextFloat() : 0.5f + rng.nextFloat() * 0.5f;
        float noiseScale = chaosMode ? rng.nextFloat() * 50f : 5f + rng.nextFloat() * 10f;
        float noiseThreshold = chaosMode ? rng.nextFloat() * 2f - 1f : -0.1f + rng.nextFloat() * 0.3f;

        return new OreGenConfig(
            specialSector,
            sectorType,
            patchShape,
            patchDensity,
            noiseScale,
            noiseThreshold,
            seed
        );
    }*/


    // === Getters and Setters ===
    /*public boolean isSpecialSector() {
        return specialSector;
    }
    public void setSpecialSector(boolean specialSector) {
        this.specialSector = specialSector;
    }*/

    public String getSectorType() {
        return sectorType;
    }
    public void setSectorType(String sectorType) {
        
        this.sectorType = sectorType;
    }

    public String getPatchShape() {
        return patchShape;
    }
    public void setPatchShape(String patchShape) {
        this.patchShape = patchShape;
    }

    public float getPatchDensity() {
        return patchDensity;
    }
    public void setPatchDensity(float patchDensity) {
        this.patchDensity = patchDensity;
    }

    public float getNoiseScale() {
        return noiseScale;
    }
    public void setNoiseScale(float noiseScale) {
        this.noiseScale = noiseScale;
    }

    public int getNoiseSeed() {
        return noiseSeed;
    }
    public void setNoiseSeed(int noiseSeed) {
        this.noiseSeed = noiseSeed;
    }

    public int getPatchMinSize() {
        return patchMinSize;
    }
    public void setPatchMinSize(int patchMinSize) {
        this.patchMinSize = patchMinSize;
    }

    public int getPatchMaxSize() {
        return patchMaxSize;
    }
    public void setPatchMaxSize(int patchMaxSize) {
        this.patchMaxSize = patchMaxSize;
    }

    public int getMinPatchDistance() {
        return minPatchDistance;
    }
    public void setMinPatchDistance(int minPatchDistance) {
        this.minPatchDistance = minPatchDistance;
    }


}
