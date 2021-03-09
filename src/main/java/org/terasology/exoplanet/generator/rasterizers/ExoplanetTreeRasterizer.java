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
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.exoplanet.generator.ExoplanetTree;
import org.terasology.exoplanet.generator.facets.ExoplanetTreeFacet;

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
            Vector3i treePosition = new Vector3i(entry.getKey()).add(0, 1, 0);

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
            BlockRegion treeRegion = new BlockRegion(treeMinimumPos).setSize(width, height, width);
            BlockRegion treeTrunk = new BlockRegion(treePosition).setSize(1, trunkHeight, 1);
            BlockRegion treeCrown = new BlockRegion(treeMinimumPos.add(0, trunkHeight - 1, 0)).setSize(width,
                    crownHeight, width);
            BlockRegion treeTop = new BlockRegion(treeMinimumPos.add((width - topCrownWidth) / 2,
                    trunkHeight + crownHeight - 1,
                    (width - topCrownWidth) / 2)).setSize(topCrownWidth, topCrownHeight, topCrownWidth);

            // loop through each of the positions in the created regions and placing blocks
            for (Vector3ic newBlockPosition : treeRegion) {
                if (chunkRegion.getRegion().contains(newBlockPosition)) {
                    if (treeTrunk.contains(newBlockPosition)) {
                        chunk.setBlock(Chunks.toRelative(newBlockPosition, new Vector3i()), trunk);
                    } else if (!treeTrunk.contains(newBlockPosition)) {

                        if (treeCrown.contains(newBlockPosition) || treeTop.contains(newBlockPosition)) {
                            chunk.setBlock(Chunks.toRelative(newBlockPosition, new Vector3i()), leaf);
                        }
                    }
                }
            }
        }
    }
}
