// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.providers;

import org.joml.Vector2ic;
import org.terasology.biomesAPI.Biome;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetFloraFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.TeraMath;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;

import static org.terasology.exoplanet.generator.ExoplanetWorldGenerator.EXOPLANET_BORDER;


@Produces(ExoplanetFloraFacet.class)
@Requires({@Facet(ExoplanetSurfaceHeightFacet.class), @Facet(ExoplanetBiomeFacet.class)})
public class ExoplanetFloraProvider implements FacetProvider {
    private Noise floraNoise;

    @Override
    public void setSeed(long seed) {
        floraNoise = new WhiteNoise(seed + 7);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ExoplanetFloraFacet.class);
        ExoplanetFloraFacet facet = new ExoplanetFloraFacet(region.getRegion(), border);
        ExoplanetBiomeFacet biomeFacet = region.getRegionFacet(ExoplanetBiomeFacet.class);
        ExoplanetSurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(ExoplanetSurfaceHeightFacet.class);

        for (Vector2ic position : surfaceHeightFacet.getWorldArea()) {
            int surfaceHeight = TeraMath.floorToInt(surfaceHeightFacet.getWorld(position));
            float floraDensity = 0.4f;

            Biome biome = biomeFacet.getWorld(position);
            if (biome instanceof ExoplanetBiome) {
                ExoplanetBiome exoplanetBiome = (ExoplanetBiome) biome;
                floraDensity = exoplanetBiome.getFloraDensity();
            }

            if (facet.getWorldRegion().contains(position.x(), surfaceHeight, position.y())
                    && surfaceHeight > EXOPLANET_BORDER && floraNoise.noise(position.x(), position.y()) > 1 - (floraDensity * 0.1f)) {
                facet.setWorld(position.x(), surfaceHeight, position.y(), true);
            }
        }

        region.setRegionFacet(ExoplanetFloraFacet.class, facet);
    }
}
