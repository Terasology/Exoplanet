// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.rasterizers;

import org.joml.Vector3ic;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;

public class ExoplanetOceanRasterizer implements WorldRasterizer {
    private Block water;

    @Override
    public void initialize() {
        water = CoreRegistry.get(BlockManager.class).getBlock("CoreAssets:water");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        for (Vector3ic pos : Chunks.CHUNK_REGION) {
            chunk.setBlock(pos, water);
        }
    }
}
