// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.rasterizers;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.chunks.Chunk;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.exoplanet.generator.ExoplanetTree;
import org.terasology.exoplanet.generator.facets.ExoplanetTreeFacet;

import java.util.Map;

public class ExoplanetTreeRasterizer implements WorldRasterizer {
    private Block trunk;
    private Block leaf;

    @Override
    public void initialize() {
        trunk = CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:RufusleafTreeTrunk");
        leaf = CoreRegistry.get(BlockManager.class).getBlock("Exoplanet:RufusleafTreeLeaf");
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion) {
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
