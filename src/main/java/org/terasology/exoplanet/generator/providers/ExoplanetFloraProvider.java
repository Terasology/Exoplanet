// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.providers;

import org.terasology.biomesAPI.Biome;
import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.WhiteNoise;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetFloraFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;

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

        for (BaseVector2i position : surfaceHeightFacet.getWorldRegion().contents()) {
            int surfaceHeight = TeraMath.floorToInt(surfaceHeightFacet.getWorld(position));
            float floraDensity = 0.4f;

            Biome biome = biomeFacet.getWorld(position);
            if (biome instanceof ExoplanetBiome) {
                ExoplanetBiome exoplanetBiome = (ExoplanetBiome) biome;
                floraDensity = exoplanetBiome.getFloraDensity();
            }

            if (facet.getWorldRegion().encompasses(position.x(), surfaceHeight, position.y())
                    && surfaceHeight > EXOPLANET_BORDER && floraNoise.noise(position.x(), position.y()) > 1 - (floraDensity * 0.1f)) {
                facet.setWorld(position.x(), surfaceHeight, position.y(), true);
            }
        }

        region.setRegionFacet(ExoplanetFloraFacet.class, facet);
    }
}
