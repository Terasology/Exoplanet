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

import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.ChunkMath;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import java.util.LinkedHashMap;
import java.util.Map;


import static org.terasology.exoplanet.generator.ExoplanetWorldGenerator.*;

public class ExoplanetWorldRasterizer implements WorldRasterizer {
    private Block grass, dirt, sand, stone, snow, borderBlock;
    private Map<Block, Float> ore = new LinkedHashMap<>();

    private final int ORE_DEPTH = 30;
    private final int ORE_VEIN_THICKNESS = 40;

    private Random random = new FastRandom();

    @Override
    public void initialize() {
        grass = CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:ExoplanetGrass");
        dirt = CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:ExoplanetDirt");
        stone = CoreRegistry.get(BlockManager.class).getBlock("Core:Stone");
        snow = CoreRegistry.get(BlockManager.class).getBlock("Core:Snow");
        sand = CoreRegistry.get(BlockManager.class).getBlock("Core:Sand");
        borderBlock = CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:ExoplanetBorder");

        ore.put(CoreRegistry.get(BlockManager.class).getBlock("Core:CoalOre"), 0.1f);
        ore.put(CoreRegistry.get(BlockManager.class).getBlock("Core:CopperOre"), 0.2f);
        ore.put(CoreRegistry.get(BlockManager.class).getBlock("Core:IronOre"), 0.2f);
        ore.put(CoreRegistry.get(BlockManager.class).getBlock("Core:GoldOre"), 0.3f);
        ore.put(CoreRegistry.get(BlockManager.class).getBlock("Core:DiamondOre"), 0.2f);
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        ExoplanetSurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(ExoplanetSurfaceHeightFacet.class);

        if (chunkRegion.getRegion().maxY() > EXOPLANET_BORDER) {
            for (Vector3i position : chunkRegion.getRegion()) {
                float surfaceHeight = surfaceHeightFacet.getWorld(position.x, position.z);
                int rockLayerDepth = surfaceHeightFacet.getRockLayerDepth();

                if (position.y == EXOPLANET_BORDER) {
                    chunk.setBlock(ChunkMath.calcBlockPos(position), borderBlock);
                } else if (position.y > EXOPLANET_BORDER) {
                    if (position.y < surfaceHeight - rockLayerDepth) {
                        chunk.setBlock(ChunkMath.calcBlockPos(position), stone);
                        if (position.y < surfaceHeight - ORE_DEPTH && position.y > surfaceHeight - ORE_DEPTH - ORE_VEIN_THICKNESS) {
                            chunk.setBlock(ChunkMath.calcBlockPos(position), getRandomOre());
                        }
                    } else if (position.y < surfaceHeight - 1 && position.y >= surfaceHeight - rockLayerDepth) {
                        chunk.setBlock(ChunkMath.calcBlockPos(position), dirt);
                    } else if (position.y < surfaceHeight) {
                        if (position.y <= EXOPLANET_SEA_LEVEL) {
                            chunk.setBlock(ChunkMath.calcBlockPos(position), sand);
                        } else if (position.y <= EXOPLANET_HEIGHT + EXOPLANET_MOUNTAIN_HEIGHT
                                && position.y >= EXOPLANET_HEIGHT + 125) {
                            chunk.setBlock(ChunkMath.calcBlockPos(position), snow);
                        } else {
                            chunk.setBlock(ChunkMath.calcBlockPos(position), grass);
                        }
                    }
                }
            }
        }
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
