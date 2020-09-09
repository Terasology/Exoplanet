// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.rasterizers;

import org.terasology.engine.math.ChunkMath;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.exoplanet.generator.ExoplanetTree;
import org.terasology.exoplanet.generator.facets.ExoplanetTreeFacet;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;

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

        for (Map.Entry<BaseVector3i, ExoplanetTree> entry : facet.getWorldEntries().entrySet()) {
            Vector3i treePosition = new Vector3i(entry.getKey()).addY(1);

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
            Region3i treeRegion = Region3i.createFromMinAndSize(treeMinimumPos, new Vector3i(width, height, width));
            Region3i treeTrunk = Region3i.createFromMinAndSize(treePosition, new Vector3i(1, trunkHeight, 1));
            Region3i treeCrown = Region3i.createFromMinAndSize(new Vector3i(treeMinimumPos).addY(trunkHeight - 1),
                    new Vector3i(width, crownHeight, width));
            Region3i treeTop = Region3i.createFromMinAndSize(
                    new Vector3i(treeMinimumPos).add((width - topCrownWidth) / 2, trunkHeight + crownHeight - 1,
                            (width - topCrownWidth) / 2),
                    new Vector3i(topCrownWidth, topCrownHeight, topCrownWidth));

            // loop through each of the positions in the created regions and placing blocks
            for (Vector3i newBlockPosition : treeRegion) {
                if (chunkRegion.getRegion().encompasses(newBlockPosition)) {
                    if (treeTrunk.encompasses(newBlockPosition)) {
                        chunk.setBlock(ChunkMath.calcRelativeBlockPos(newBlockPosition), trunk);
                    } else if (!treeTrunk.encompasses(newBlockPosition)) {

                        if (treeCrown.encompasses(newBlockPosition) || treeTop.encompasses(newBlockPosition)) {
                            chunk.setBlock(ChunkMath.calcRelativeBlockPos(newBlockPosition), leaf);
                        }
                    }
                }
            }
        }
    }
}
