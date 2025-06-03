package mod.planets;

import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import arc.struct.Seq;
import arc.struct.ObjectMap;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.noise.Simplex;

public class MapGenerator {
    private final Tiles tiles;
    private final Seq<Biome> biomes;
    private int currentSeed;
    private final Block[] ores = {
        Blocks.oreCopper,
        Blocks.oreLead,
        Blocks.oreScrap,
        Blocks.oreCoal,
        Blocks.oreTitanium,
        Blocks.oreThorium,
        Blocks.oreBeryllium,
        Blocks.oreTungsten,
        Blocks.oreCrystalThorium
    };

    public MapGenerator(Tiles tiles) {
        this.tiles = tiles;
        this.biomes = Biome.createDefaultBiomes();
        this.currentSeed = 0;
    }

    public void addBiome(Biome biome) {
        biomes.add(biome);
    }

    public Tiles generate() {
        // Inizializza tutti i blocchi come aria
        initializeAirBlocks();
        
        // Genera il terreno base
        generateTerrain();
        
        // Genera i muri
        generateWalls();
        
        // Genera le isole di minerali
        generateOreIslands();

        return tiles;
    }

    private void initializeAirBlocks() {
        for (int x = 0; x < tiles.width; x++) {
            for (int y = 0; y < tiles.height; y++) {
                Tile tile = tiles.getn(x, y);
                tile.setBlock(Blocks.air);
            }
        }
    }

    private void generateTerrain() {
        float scale = 0.005f; // Scala ridotta per una migliore distribuzione
        
        for (int x = 0; x < tiles.width; x++) {
            for (int y = 0; y < tiles.height; y++) {
                // Calcola i valori di temperatura, umidità ed elevazione usando il rumore
                float temperature = Simplex.noise2d(currentSeed, 4, 0.5f, scale, x, y);
                float humidity = Simplex.noise2d(currentSeed + 1, 4, 0.5f, scale, x, y);
                float elevation = Simplex.noise2d(currentSeed + 2, 4, 0.5f, scale, x, y);

                // Normalizza i valori tra 0 e 1
                temperature = (temperature + 1) / 2;
                humidity = (humidity + 1) / 2;
                elevation = (elevation + 1) / 2;

                // Trova il bioma più adatto con una transizione più naturale
                Biome selectedBiome = findBestBiome(temperature, humidity, elevation);
                
                // Applica il bioma al tile con una transizione più fluida
                Tile tile = tiles.getn(x, y);
                Block floorBlock = selectedBiome.getFloorBlock(x, y);
                Tile.setFloor(tile, floorBlock, Blocks.air);

                // Aggiungi overlay con una probabilità basata sull'elevazione
                if (selectedBiome.overlay != null && Mathf.random() < 0.1f * elevation) {
                    Tile.setOverlay(tile, selectedBiome.overlay);
                }
            }
        }
    }

    private void generateWalls() {
        float scale = 0.03f; // Scala per la generazione dei muri
        
        for (int x = 0; x < tiles.width; x++) {
            for (int y = 0; y < tiles.height; y++) {
                Tile tile = tiles.getn(x, y);
                
                // Non generare muri sull'acqua
                if (tile.floor() == Blocks.water || tile.floor() == Blocks.deepwater) {
                    continue;
                }
                
                // Usa il rumore per determinare se posizionare un muro
                float noise = Simplex.noise2d(currentSeed + 3, 4, 0.5f, scale, x, y);
                noise = (noise + 1) / 2; // Normalizza il rumore
                
                // Calcola la distanza dal centro della mappa
                float distFromCenter = Mathf.dst(x, y, tiles.width/2f, tiles.height/2f);
                float maxDist = Mathf.dst(0, 0, tiles.width/2f, tiles.height/2f);
                float normalizedDist = distFromCenter / maxDist;
                
                // Aumenta la probabilità di muri verso i bordi
                float wallChance = Mathf.lerp(0.1f, 0.4f, normalizedDist);
                
                // Posiziona il muro se il rumore è abbastanza alto
                if (noise > (1f - wallChance)) {
                    // Scegli il tipo di muro in base al bioma
                    Block wallBlock = Blocks.stoneWall;
                    if (tile.floor() == Blocks.sand || tile.floor() == Blocks.darksand) {
                        wallBlock = Blocks.duneWall;
                    } else if (tile.floor() == Blocks.snow || tile.floor() == Blocks.iceSnow) {
                        wallBlock = Blocks.snowWall;
                    }
                    
                    tile.setBlock(wallBlock);
                }
            }
        }
    }

    private void generateOreIslands() {
        float scale = 0.015f; // Scala ridotta per isole più grandi
        
        // Definisci il numero di isole per ogni tipo di minerale in base alla rarità
        ObjectMap<Block, Integer> oreCounts = new ObjectMap<>();
        oreCounts.put(Blocks.oreCopper, Mathf.random(6, 8));     // Comune
        oreCounts.put(Blocks.oreLead, Mathf.random(5, 7));       // Comune
        oreCounts.put(Blocks.oreScrap, Mathf.random(4, 6));      // Medio
        oreCounts.put(Blocks.oreCoal, Mathf.random(4, 6));       // Medio
        oreCounts.put(Blocks.oreTitanium, Mathf.random(3, 5));   // Raro
        oreCounts.put(Blocks.oreThorium, Mathf.random(3, 4));    // Raro
        oreCounts.put(Blocks.oreBeryllium, Mathf.random(2, 4));  // Molto raro
        oreCounts.put(Blocks.oreTungsten, Mathf.random(2, 4));   // Molto raro
        oreCounts.put(Blocks.oreCrystalThorium, Mathf.random(2, 3)); // Estremamente raro
        
        // Genera le isole per ogni tipo di minerale
        for (Block ore : ores) {
            int numIslands = oreCounts.get(ore, 4); // Default a 4 se non specificato
            
            // Crea un array di posizioni possibili
            Seq<Point2> possiblePositions = new Seq<>();
            for (int x = 0; x < tiles.width; x++) {
                for (int y = 0; y < tiles.height; y++) {
                    Tile tile = tiles.getn(x, y);
                    if (tile.floor() != Blocks.water && tile.floor() != Blocks.deepwater) {
                        possiblePositions.add(new Point2(x, y));
                    }
                }
            }
            
            // Mescola le posizioni possibili
            possiblePositions.shuffle();
            
            // Genera le isole
            for (int i = 0; i < numIslands && possiblePositions.size > 0; i++) {
                // Prendi una posizione casuale
                Point2 pos = possiblePositions.pop();
                
                // Genera l'isola
                generateOreIsland(pos.x, pos.y, ore, scale);
                
                // Rimuovi le posizioni vicine per evitare sovrapposizioni
                possiblePositions.removeAll(p -> Mathf.dst(p.x, p.y, pos.x, pos.y) < 15);
            }
        }
    }

    private void generateOreIsland(int centerX, int centerY, Block ore, float scale) {
        // Dimensione base dell'isola
        int baseSize = Mathf.random(8, 12); // Isole più grandi
        
        // Crea una forma più organica usando più livelli di rumore
        for (int x = -baseSize; x <= baseSize; x++) {
            for (int y = -baseSize; y <= baseSize; y++) {
                int worldX = centerX + x;
                int worldY = centerY + y;
                
                // Verifica che le coordinate siano valide
                if (worldX < 0 || worldX >= tiles.width || worldY < 0 || worldY >= tiles.height) {
                    continue;
                }
                
                // Calcola la distanza dal centro
                float dist = Mathf.dst(x, y, 0, 0);
                
                // Usa più livelli di rumore per una forma più naturale
                float noise1 = Simplex.noise2d(currentSeed + ore.id, 4, 0.5f, scale, worldX, worldY);
                float noise2 = Simplex.noise2d(currentSeed + ore.id + 1, 2, 0.3f, scale * 2, worldX, worldY);
                float noise3 = Simplex.noise2d(currentSeed + ore.id + 2, 1, 0.2f, scale * 4, worldX, worldY);
                
                // Combina i livelli di rumore
                float combinedNoise = (noise1 + noise2 + noise3) / 3f;
                combinedNoise = (combinedNoise + 1) / 2; // Normalizza il rumore
                
                // Crea una forma più organica usando una funzione di falloff
                float falloff = 1f - (dist / baseSize);
                falloff = Mathf.pow(falloff, 1.2f); // Falloff più graduale
                
                // Aggiungi variazioni casuali per rendere l'isola più irregolare
                float randomVariation = Mathf.random(0.15f);
                
                // Calcola la probabilità finale di posizionare il minerale
                float finalChance = combinedNoise * falloff + randomVariation;
                
                // Posiziona il minerale se la probabilità è abbastanza alta
                if (finalChance > 0.55f) { // Soglia più bassa per isole più dense
                    Tile tile = tiles.getn(worldX, worldY);
                    // Verifica che il tile sia valido per l'overlay
                    if (tile.floor() != Blocks.water && tile.floor() != Blocks.deepwater) {
                        // Aggiungi una piccola probabilità di creare un cluster più denso
                        if (Mathf.random() < 0.4f) { // Probabilità aumentata per cluster
                            // Crea un cluster di minerali più grande
                            for (int dx = -2; dx <= 2; dx++) {
                                for (int dy = -2; dy <= 2; dy++) {
                                    int clusterX = worldX + dx;
                                    int clusterY = worldY + dy;
                                    
                                    if (clusterX >= 0 && clusterX < tiles.width && 
                                        clusterY >= 0 && clusterY < tiles.height) {
                                        Tile clusterTile = tiles.getn(clusterX, clusterY);
                                        if (clusterTile.floor() != Blocks.water && 
                                            clusterTile.floor() != Blocks.deepwater) {
                                            // Probabilità decrescente con la distanza dal centro
                                            float clusterDist = Mathf.dst(dx, dy, 0, 0);
                                            if (Mathf.random() < (1f - clusterDist/3f)) {
                                                Tile.setOverlay(clusterTile, ore);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Tile.setOverlay(tile, ore);
                        }
                    }
                }
            }
        }
    }

    private Biome findBestBiome(float temperature, float humidity, float elevation) {
        Biome bestBiome = biomes.first();
        float bestScore = Float.MAX_VALUE;

        for (Biome biome : biomes) {
            // Calcola un punteggio pesato che considera le caratteristiche del bioma
            float tempScore = Math.abs(biome.temperature - temperature) * 1.5f;
            float humScore = Math.abs(biome.humidity - humidity) * 1.2f;
            float elevScore = Math.abs(biome.elevation - elevation) * 1.0f;
            
            float score = tempScore + humScore + elevScore;
            
            // Aggiungi una piccola variazione casuale per rendere le transizioni più naturali
            score += Mathf.random(0.1f);
            
            if (score < bestScore) {
                bestScore = score;
                bestBiome = biome;
            }
        }

        return bestBiome;
    }
}