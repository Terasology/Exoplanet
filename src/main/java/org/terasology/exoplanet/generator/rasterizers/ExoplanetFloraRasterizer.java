// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.rasterizers;

import org.terasology.biomesAPI.Biome;
import org.terasology.engine.math.ChunkMath;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetFloraFacet;
import org.terasology.math.geom.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class ExoplanetFloraRasterizer implements WorldRasterizer {
    private final Random random = new FastRandom();
    private final Map<Block, Float> flora = new HashMap<>();

    private Block air, AlbidusGrass1, AlbidusGrass2, RubidusGrass1, RubidusGrass2;

    private ExoplanetBiome previousBiome;

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);

        air = blockManager.getBlock(BlockManager.AIR_ID);
        AlbidusGrass1 = blockManager.getBlock("Exoplanet:AlbidusGrass1");
        AlbidusGrass2 = blockManager.getBlock("Exoplanet:AlbidusGrass2");
        RubidusGrass1 = blockManager.getBlock("Exoplanet:RubidusGrass1");
        RubidusGrass2 = blockManager.getBlock("Exoplanet:RubidusGrass2");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        ExoplanetFloraFacet facet = chunkRegion.getFacet(ExoplanetFloraFacet.class);
        ExoplanetBiomeFacet biomeFacet = chunkRegion.getFacet(ExoplanetBiomeFacet.class);

        for (Vector3i position : chunkRegion.getRegion()) {
            Biome biome = biomeFacet.getWorld(position.x, position.z);

            if (facet.getWorld(position)
                    && chunk.getBlock(ChunkMath.calcRelativeBlockPos(new Vector3i(position).subY(1))).getURI() != BlockManager.AIR_ID
                    && !chunk.getBlock(ChunkMath.calcRelativeBlockPos(new Vector3i(position).subY(1))).isLiquid()) {
                chunk.setBlock(ChunkMath.calcRelativeBlockPos(position.addY(1)), getRandomFlora(biome));
            }
        }
    }

    private Block getRandomFlora(Biome biome) {
        if (biome instanceof ExoplanetBiome) {
            if (previousBiome == null) {
                previousBiome = (ExoplanetBiome) biome;
                registerFlora(biome);
            } else if (previousBiome != biome) {
                previousBiome = (ExoplanetBiome) biome;
                registerFlora(biome);
            }
        }

        if (!flora.isEmpty()) {
            float rand = random.nextFloat(0, 1);
            float cumulativeProbability = 0.0f;

            for (Map.Entry<Block, Float> entry : flora.entrySet()) {
                cumulativeProbability += entry.getValue();
                if (rand <= cumulativeProbability) {
                    return entry.getKey();
                }
            }
        }

        return air;
    }

    private void registerFlora(Biome biome) {
        flora.clear();

        switch ((ExoplanetBiome) biome) {
            case DESERT:
                break;
            case SNOW:
                flora.put(AlbidusGrass1, 0.5f);
                flora.put(AlbidusGrass2, 0.5f);
                break;
            case SNOWMOUNTAINS:
                flora.put(AlbidusGrass1, 0.4f);
                flora.put(AlbidusGrass2, 0.4f);
                flora.put(RubidusGrass1, 0.2f);
                break;
            default:
                flora.put(AlbidusGrass1, 0.25f);
                flora.put(AlbidusGrass2, 0.25f);
                flora.put(RubidusGrass1, 0.25f);
                flora.put(RubidusGrass2, 0.25f);
                break;
        }
    }
}
