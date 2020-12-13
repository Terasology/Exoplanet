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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.exoplanet.generator.ExoplanetTree;
import org.terasology.exoplanet.generator.facets.ExoplanetTreeFacet;
import org.terasology.math.ChunkMath;
import org.terasology.math.Region3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.block.BlockRegions;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import java.util.Map;

public class ExoplanetTreeRasterizer implements WorldRasterizer {
    private Block trunk, leaf;

    @Override
    public void initialize() {
        trunk = CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:RufusleafTreeTrunk");
        leaf = CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:RufusleafTreeLeaf");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        ExoplanetTreeFacet facet = chunkRegion.getFacet(ExoplanetTreeFacet.class);

        for (Map.Entry<Vector3ic, ExoplanetTree> entry : facet.getWorldEntries().entrySet()) {
            Vector3i treePosition = new Vector3i(entry.getKey()).add(0,1,0);

            int height = entry.getValue().getHeight();
            int width = entry.getValue().getWidth();
            int radius = entry.getValue().getCrownRadius();
            int trunkHeight = entry.getValue().getTrunkHeight();
            int crownHeight = entry.getValue().getCrownHeight();
            int topCrownHeight = entry.getValue().getTopCrownHeight();
            int topCrownWidth = entry.getValue().getTopCrownWidth();

            // the position at the far top left corner - used as origin to create regions
            Vector3i treeMinimumPos = new Vector3i(treePosition).sub(radius, 0, radius);

            // creates regions for different parts of a tree
            BlockRegion treeRegion = BlockRegions.createFromMinAndSize(treeMinimumPos, new Vector3i(width, height, width));
            BlockRegion treeTrunk = BlockRegions.createFromMinAndSize(treePosition, new Vector3i(1, trunkHeight, 1));
            BlockRegion treeCrown = BlockRegions.createFromMinAndSize(new Vector3i(treeMinimumPos).add(0,trunkHeight - 1,0),
                    new Vector3i(width, crownHeight, width));
            BlockRegion treeTop = BlockRegions.createFromMinAndSize(
                    new Vector3i(treeMinimumPos).add((width - topCrownWidth) / 2, trunkHeight + crownHeight - 1,
                            (width - topCrownWidth) / 2),
                    new Vector3i(topCrownWidth, topCrownHeight, topCrownWidth));

            // loop through each of the positions in the created regions and placing blocks
            for (Vector3ic newBlockPosition : BlockRegions.iterableInPlace(treeRegion)) {
                if (chunkRegion.getRegion().containsBlock(newBlockPosition)) {
                    if (treeTrunk.containsBlock(newBlockPosition)) {
                        chunk.setBlock(ChunkMath.calcRelativeBlockPos(newBlockPosition, new Vector3i()), trunk);
                    } else if (!treeTrunk.containsBlock(newBlockPosition)) {

                        if (treeCrown.containsBlock(newBlockPosition) || treeTop.containsBlock(newBlockPosition)) {
                            chunk.setBlock(ChunkMath.calcRelativeBlockPos(newBlockPosition, new Vector3i()), leaf);
                        }
                    }
                }
            }
        }
    }
}
