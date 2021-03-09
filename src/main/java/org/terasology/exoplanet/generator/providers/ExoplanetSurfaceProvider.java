// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.providers;

import org.joml.Vector2f;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;

@Produces(ExoplanetSurfaceHeightFacet.class)
public class ExoplanetSurfaceProvider implements FacetProvider {
    private SubSampledNoise surfaceNoise;
    private int exoplanetWorldHeight;

    private int baseHeight = 20;

    public ExoplanetSurfaceProvider(int worldHeight) {
        this.exoplanetWorldHeight = worldHeight;
    }

    @Override
    public void setSeed(long seed) {
        BrownianNoise source = new BrownianNoise(new PerlinNoise(seed), 8);
        surfaceNoise = new SubSampledNoise(source, new Vector2f(0.004f, 0.004f),  4);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ExoplanetSurfaceHeightFacet.class);
        ExoplanetSurfaceHeightFacet facet = new ExoplanetSurfaceHeightFacet(region.getRegion(), border);
        facet.setBaseSurfaceHeight(exoplanetWorldHeight);

        BlockAreac processRegion = facet.getWorldArea();
        float[] noise = surfaceNoise.noise(processRegion);

        for (int i = 0; i < noise.length; ++i) {
            noise[i] = (baseHeight + baseHeight * ((noise[i] * 2.11f + 1f) / 2f)) + exoplanetWorldHeight;
        }

        facet.set(noise);
        region.setRegionFacet(ExoplanetSurfaceHeightFacet.class, facet);
    }
}
