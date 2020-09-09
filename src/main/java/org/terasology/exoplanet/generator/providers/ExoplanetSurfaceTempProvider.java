// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.providers;

import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceTempFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector2f;

@Produces(ExoplanetSurfaceTempFacet.class)
public class ExoplanetSurfaceTempProvider implements FacetProvider {
    private SubSampledNoise temperatureNoise;

    @Override
    public void setSeed(long seed) {
        temperatureNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 5), 4),
                new Vector2f(0.0006f, 0.0006f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        ExoplanetSurfaceTempFacet facet = new ExoplanetSurfaceTempFacet(region.getRegion(),
                region.getBorderForFacet(ExoplanetSurfaceTempFacet.class));
        float[] noise = this.temperatureNoise.noise(facet.getWorldRegion());

        for (int i = 0; i < noise.length; ++i) {
            noise[i] = TeraMath.clamp((noise[i] * 2.11f + 1f) * 0.5f);
        }

        facet.set(noise);
        region.setRegionFacet(ExoplanetSurfaceTempFacet.class, facet);
    }
}
