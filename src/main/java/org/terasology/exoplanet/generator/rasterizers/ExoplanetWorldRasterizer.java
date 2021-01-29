// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.rasterizers;

import org.joml.Vector2i;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.biomesAPI.Biome;
import org.terasology.biomesAPI.BiomeRegistry;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSeaLevelFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.TeraMath;
import org.terasology.registry.CoreRegistry;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.Chunks;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import java.util.HashMap;
import java.util.Map;

import static org.terasology.exoplanet.generator.ExoplanetWorldGenerator.EXOPLANET_BORDER;

public class ExoplanetWorldRasterizer implements WorldRasterizer {
    private static final int ORE_DEPTH = 30;
    private static final int ORE_VEIN_THICKNESS = 40;

    private Block grass;
    private Block dirt;
    private Block sand;
    private Block stone;
    private Block snowyStone;
    private Block snow;
    private Block ice;
    private Block water;
    private Block borderBlock;
    private Block air;
    private BiomeRegistry biomeRegistry;
    private Map<Block, Float> ore = new HashMap<>();


    private Random random = new FastRandom();

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        biomeRegistry = CoreRegistry.get(BiomeRegistry.class);
        grass = blockManager.getBlock("Exoplanet:ExoplanetGrass");
        dirt = blockManager.getBlock("Exoplanet:ExoplanetDirt");
        stone = blockManager.getBlock("CoreAssets:Stone");
        snowyStone = blockManager.getBlock("Exoplanet:SnowyStone");
        snow = blockManager.getBlock("CoreAssets:Snow");
        sand = blockManager.getBlock("CoreAssets:Sand");
        ice = blockManager.getBlock("CoreAssets:Ice");
        water = blockManager.getBlock("CoreAssets:water");
        borderBlock = blockManager.getBlock("Exoplanet:ExoplanetBorder");
        air = blockManager.getBlock(BlockManager.AIR_ID);

        ore.put(blockManager.getBlock("CoreAssets:CoalOre"), 0.1f);
        ore.put(blockManager.getBlock("CoreAssets:CopperOre"), 0.2f);
        ore.put(blockManager.getBlock("CoreAssets:IronOre"), 0.2f);
        ore.put(blockManager.getBlock("CoreAssets:GoldOre"), 0.3f);
        ore.put(blockManager.getBlock("CoreAssets:DiamondOre"), 0.2f);
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        ExoplanetSurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(ExoplanetSurfaceHeightFacet.class);
        ExoplanetBiomeFacet biomeFacet = chunkRegion.getFacet(ExoplanetBiomeFacet.class);
        ExoplanetSeaLevelFacet seaLevelFacet = chunkRegion.getFacet(ExoplanetSeaLevelFacet.class);

        int seaLevelWorldHeight = seaLevelFacet.getWorldSeaLevel();

        if (chunkRegion.getRegion().maxY() > EXOPLANET_BORDER) {
            Vector2i pos2d = new Vector2i();
            Vector3i pos = new Vector3i();
            for (Vector3ic position : chunkRegion.getRegion()) {
                pos2d.set(position.x(), position.z());

                int surfaceHeight = TeraMath.floorToInt(surfaceHeightFacet.getWorld(pos2d));
                int rockLayerDepth = surfaceHeightFacet.getRockLayerDepth();

                if (position.y() == EXOPLANET_BORDER) {
                    chunk.setBlock(Chunks.toRelative(position, pos), borderBlock);
                } else if (position.y() > EXOPLANET_BORDER) {

                    Biome biome = biomeFacet.getWorld(pos2d);
                    biomeRegistry.setBiome(biome, chunk, Chunks.toRelative(position, pos));

                    if (position.y() == seaLevelWorldHeight && ExoplanetBiome.SNOW == biome) {
                        chunk.setBlock(Chunks.toRelative(position, pos), ice);
                    } else if (position.y() <= seaLevelWorldHeight) {
                        chunk.setBlock(Chunks.toRelative(position, pos), water);
                    }

                    if (position.y() <= surfaceHeight) {
                        Block block = getBlockToPlace(surfaceHeight, position.y(), biome, seaLevelWorldHeight,
                            rockLayerDepth);
                        chunk.setBlock(Chunks.toRelative(position, pos), block);
                    }
                }
            }
        }
    }

    private Block getBlockToPlace(int surfaceHeight, int currentHeight, Biome biome, int seaLevel, int rockLayerDepth) {
        if (currentHeight < surfaceHeight - ORE_DEPTH && currentHeight > surfaceHeight - ORE_DEPTH - ORE_VEIN_THICKNESS) {
            return getRandomOre();
        }
        if (biome instanceof ExoplanetBiome) {
            switch ((ExoplanetBiome) biome) {
                case FOREST:
                    if (surfaceHeight == currentHeight) {
                        return grass;
                    } else if (currentHeight < surfaceHeight - rockLayerDepth) {
                        return stone;
                    } else {
                        return dirt;
                    }
                case PLAINS:
                    if (surfaceHeight == currentHeight) {
                        return grass;
                    } else if (currentHeight < surfaceHeight - rockLayerDepth) {
                        return stone;
                    } else {
                        return dirt;
                    }
                case MOUNTAINS:
                    if (surfaceHeight == currentHeight && currentHeight > seaLevel && currentHeight < seaLevel + 100) {
                        return grass;
                    } else if (currentHeight < surfaceHeight - rockLayerDepth) {
                        return stone;
                    } else {
                        return dirt;
                    }
                case SNOWMOUNTAINS:
                    if (surfaceHeight == currentHeight) {
                        if (currentHeight >= seaLevel + 80 && currentHeight < seaLevel + 100) {
                            return snow;
                        } else if (currentHeight >= seaLevel + 100) {
                            return snowyStone;
                        }
                    } else if (currentHeight < surfaceHeight - rockLayerDepth) {
                        return stone;
                    } else if (currentHeight < surfaceHeight && currentHeight >= seaLevel + 100) {
                        return stone;
                    } else {
                        return dirt;
                    }
                case SNOW:
                    if (currentHeight > seaLevel && currentHeight == surfaceHeight) {
                        // Snow on top
                        return snow;
                    } else if (currentHeight < surfaceHeight - rockLayerDepth) {
                        // Stone
                        return stone;
                    } else {
                        // Dirt
                        return dirt;
                    }
                case DESERT:
                    if (currentHeight < surfaceHeight - 6) {
                        // Stone
                        return stone;
                    } else {
                        return sand;
                    }
                case OCEAN:
                    if (surfaceHeight == currentHeight) {
                        return sand;
                    } else {
                        return stone;
                    }
                case BEACH:
                    if (currentHeight >= surfaceHeight - 2) {
                        return sand;
                    } else {
                        return stone;
                    }
            }
        }
        return air;
    }

    private Block getRandomOre() {
        float rand = random.nextFloat(0, 1);
        float cumulativeProbability = 0.0f;

        for (Map.Entry<Block, Float> entry : ore.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (rand <= cumulativeProbability) {
                return entry.getKey();
            }
        }
        return stone;
    }
}
