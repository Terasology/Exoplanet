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
package org.terasology.generator.rasterizers;

import org.terasology.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.ChunkMath;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import static org.terasology.generator.ExoplanetWorldGenerator.*;

public class ExoplanetWorldRasterizer implements WorldRasterizer {
    private Block grass, dirt, sand, stone, snow;

    @Override
    public void initialize() {
        grass = CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:ExoplanetGrass");
        dirt = CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:ExoplanetDirt");
        stone = CoreRegistry.get(BlockManager.class).getBlock("Core:Stone");
        snow = CoreRegistry.get(BlockManager.class).getBlock("Core:Snow");
        sand = CoreRegistry.get(BlockManager.class).getBlock("Core:Sand");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        ExoplanetSurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(ExoplanetSurfaceHeightFacet.class);

        if (chunkRegion.getRegion().maxY() > EXOPLANET_BORDER) {
            for (Vector3i position : chunkRegion.getRegion()) {
                float surfaceHeight = surfaceHeightFacet.getWorld(position.x, position.z);
                int rockLayerDepth = surfaceHeightFacet.getRockLayerDepth();

                if (position.y > EXOPLANET_BORDER) {
                    if (position.y < surfaceHeight - rockLayerDepth) {
                        chunk.setBlock(ChunkMath.calcBlockPos(position), stone);
                    } else if (position.y < surfaceHeight - 1 && position.y >= surfaceHeight - rockLayerDepth) {
                        chunk.setBlock(ChunkMath.calcBlockPos(position), dirt);
                    } else if (position.y < surfaceHeight) {
                        if (position.y < EXOPLANET_SEA_LEVEL || position.y == EXOPLANET_SEA_LEVEL) {
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
}
