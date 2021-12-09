// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.rasterizers;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.biomesAPI.Biome;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.Chunk;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetFloraFacet;

import java.util.HashMap;
import java.util.Map;

public class ExoplanetFloraRasterizer implements WorldRasterizer {
    private final Random random = new FastRandom();
    private final Map<Block, Float> flora = new HashMap<>();

    private Block air;
    private Block albidusGrass1;
    private Block albidusGrass2;
    private Block rubidusGrass1;
    private Block rubidusGrass2;

    private ExoplanetBiome previousBiome;

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);

        air = blockManager.getBlock(BlockManager.AIR_ID);
        albidusGrass1 = blockManager.getBlock("Exoplanet:AlbidusGrass1");
        albidusGrass2 = blockManager.getBlock("Exoplanet:AlbidusGrass2");
        rubidusGrass1 = blockManager.getBlock("Exoplanet:RubidusGrass1");
        rubidusGrass2 = blockManager.getBlock("Exoplanet:RubidusGrass2");
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion) {
        ExoplanetFloraFacet facet = chunkRegion.getFacet(ExoplanetFloraFacet.class);
        ExoplanetBiomeFacet biomeFacet = chunkRegion.getFacet(ExoplanetBiomeFacet.class);

        Vector3i tempPos = new Vector3i();
        for (Vector3ic pos : chunkRegion.getRegion()) {
            Biome biome = biomeFacet.getWorld(pos.x(), pos.z());

            if (facet.getWorld(pos)
                    && chunk.getBlock(Chunks.toRelative(pos.sub(0, 1, 0, tempPos), tempPos)).getURI() != BlockManager.AIR_ID
                    && !chunk.getBlock(Chunks.toRelative(pos.sub(0, 1, 0, tempPos), tempPos)).isLiquid()) {
                chunk.setBlock(Chunks.toRelative(pos.add(0, 1, 0, tempPos), tempPos), getRandomFlora(biome));
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
                flora.put(albidusGrass1, 0.5f);
                flora.put(albidusGrass2, 0.5f);
                break;
            case SNOWMOUNTAINS:
                flora.put(albidusGrass1, 0.4f);
                flora.put(albidusGrass2, 0.4f);
                flora.put(rubidusGrass1, 0.2f);
                break;
            default:
                flora.put(albidusGrass1, 0.25f);
                flora.put(albidusGrass2, 0.25f);
                flora.put(rubidusGrass1, 0.25f);
                flora.put(rubidusGrass2, 0.25f);
                break;
        }
    }
}
