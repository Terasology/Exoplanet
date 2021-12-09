// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.providers;

import org.joml.Vector2f;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.exoplanet.generator.facets.ExoplanetHumidityFacet;
import org.terasology.math.TeraMath;

@Produces(ExoplanetHumidityFacet.class)
public class ExoplanetHumidityProvider implements FacetProvider {
    private SubSampledNoise humidityNoise;

    @Override
    public void setSeed(long seed) {
        humidityNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 4), 4),
                new Vector2f(0.0006f, 0.0006f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ExoplanetHumidityFacet.class);
        ExoplanetHumidityFacet facet = new ExoplanetHumidityFacet(region.getRegion(), border);

        float[] noise = humidityNoise.noise(facet.getWorldArea());
        for (int i = 0; i < noise.length; ++i) {
            noise[i] = TeraMath.clamp((noise[i] * 2.11f + 1f) * 0.5f);
        }

        facet.set(noise);
        region.setRegionFacet(ExoplanetHumidityFacet.class, facet);
    }
}
