/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.exoplanet.generator.rasterizers;

import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.ChunkMath;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector2i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.biomes.Biome;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.liquid.LiquidData;
import org.terasology.world.liquid.LiquidType;

import java.util.LinkedHashMap;
import java.util.Map;


import static org.terasology.exoplanet.generator.ExoplanetWorldGenerator.*;

public class ExoplanetWorldRasterizer implements WorldRasterizer {
    private Block grass, dirt, sand, stone, snow, ice, water, borderBlock, air;
    private Map<Block, Float> ore = new LinkedHashMap<>();

    private final int ORE_DEPTH = 30;
    private final int ORE_VEIN_THICKNESS = 40;

    private Random random = new FastRandom();

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        grass = blockManager.getBlock("Exoplanet:ExoplanetGrass");
        dirt = blockManager.getBlock("Exoplanet:ExoplanetDirt");
        stone = blockManager.getBlock("Core:Stone");
        snow = blockManager.getBlock("Core:Snow");
        sand = blockManager.getBlock("Core:Sand");
        ice = blockManager.getBlock("Core:Ice");
        water = blockManager.getBlock("core:water");
        borderBlock = blockManager.getBlock("Exoplanet:ExoplanetBorder");
        air = blockManager.getBlock(BlockManager.AIR_ID);

        ore.put(blockManager.getBlock("Core:CoalOre"), 0.1f);
        ore.put(blockManager.getBlock("Core:CopperOre"), 0.2f);
        ore.put(blockManager.getBlock("Core:IronOre"), 0.2f);
        ore.put(blockManager.getBlock("Core:GoldOre"), 0.3f);
        ore.put(blockManager.getBlock("Core:DiamondOre"), 0.2f);
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        LiquidData waterLiquid = new LiquidData(LiquidType.WATER, LiquidData.MAX_LIQUID_DEPTH);
        ExoplanetSurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(ExoplanetSurfaceHeightFacet.class);
        ExoplanetBiomeFacet biomeFacet = chunkRegion.getFacet(ExoplanetBiomeFacet.class);

        Vector2i pos2d = new Vector2i();
        if (chunkRegion.getRegion().maxY() > EXOPLANET_BORDER) {
            for (Vector3i position : chunkRegion.getRegion()) {
                pos2d.set(position.x, position.z);

                int surfaceHeight = TeraMath.floorToInt(surfaceHeightFacet.getWorld(pos2d));
                int rockLayerDepth = surfaceHeightFacet.getRockLayerDepth();

                if (position.y == EXOPLANET_BORDER) {
                    chunk.setBlock(ChunkMath.calcBlockPos(position), borderBlock);
                } else if (position.y > EXOPLANET_BORDER) {

                    Biome biome = biomeFacet.getWorld(pos2d);
                    chunk.setBiome(ChunkMath.calcBlockPos(position), biome);

                    if (position.y == EXOPLANET_SEA_LEVEL && ExoplanetBiome.SNOW == biome) {
                        chunk.setBlock(ChunkMath.calcBlockPos(position), ice);
                    } else if (position.y <= EXOPLANET_SEA_LEVEL) {
                        chunk.setBlock(ChunkMath.calcBlockPos(position), water);
                        chunk.setLiquid(ChunkMath.calcBlockPos(position), waterLiquid);
                    }

                    if (position.y <= surfaceHeight) {
                        Block block = getBlockToPlace(surfaceHeight, position.y, biome, EXOPLANET_SEA_LEVEL, rockLayerDepth);
                        chunk.setBlock(ChunkMath.calcBlockPos(position), block);
                    }
                }
            }
        }
    }

    private Block getBlockToPlace(int surfaceHeight, int currentHeight, Biome type, int seaLevel, int rockLayerDepth) {
        if (currentHeight < surfaceHeight - ORE_DEPTH && currentHeight > surfaceHeight - ORE_DEPTH - ORE_VEIN_THICKNESS) {
            return getRandomOre();
        }
        if (type instanceof ExoplanetBiome) {
            switch ((ExoplanetBiome) type) {
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
                    if (surfaceHeight == currentHeight && currentHeight > seaLevel && currentHeight < seaLevel + 125) {
                        return grass;
                    } else if (surfaceHeight == currentHeight && currentHeight >= seaLevel + 125) {
                        return snow;
                    } else if (currentHeight < surfaceHeight - rockLayerDepth) {
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
        return null;
    }
}
