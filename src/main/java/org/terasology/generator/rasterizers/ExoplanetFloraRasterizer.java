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

import org.terasology.generator.facets.ExoplanetFloraFacet;
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

public class ExoplanetFloraRasterizer implements WorldRasterizer {
    private Random random = new FastRandom();
    private Map<Block, Float> flora = new LinkedHashMap<>();

    @Override
    public void initialize() {
        flora.put(CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:ExoplanetTallGrass1"), 0.35f);
        flora.put(CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:ExoplanetTallGrass2"), 0.45f);
        flora.put(CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:ExoplanetTallGrass3"), 0.45f);
        flora.put(CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:ExoplanetTallGrass4"), 0.3f);
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        ExoplanetFloraFacet facet = chunkRegion.getFacet(ExoplanetFloraFacet.class);
        for (Vector3i position : chunkRegion.getRegion()) {
            if (facet.getWorld(position)
                    && chunk.getBlock(ChunkMath.calcBlockPos(new Vector3i(position).subY(1))).getURI() != BlockManager.AIR_ID
                    && !chunk.getBlock(ChunkMath.calcBlockPos(new Vector3i(position).subY(1))).isLiquid()) {
                chunk.setBlock(ChunkMath.calcBlockPos(position.addY(1)), getRandomFlora(flora));
            }
        }
    }

    private Block getRandomFlora(Map<Block, Float> floraMap) {
        float rand = random.nextFloat(0, 1);
        float cumulativeProbability = 0.0f;

        for (Map.Entry<Block, Float> entry : floraMap.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (rand <= cumulativeProbability) {
                return entry.getKey();
            }
        }
        return null;
    }
}
