// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.providers;

import org.joml.Vector2ic;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetHumidityFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSeaLevelFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceTempFacet;

@Produces(ExoplanetBiomeFacet.class)
@Requires({
        @Facet(ExoplanetSeaLevelFacet.class),
        @Facet(ExoplanetSurfaceHeightFacet.class),
        @Facet(ExoplanetSurfaceTempFacet.class),
        @Facet(ExoplanetHumidityFacet.class)
})
public class ExoplanetBiomeProvider implements FacetProvider {

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
        ExoplanetSurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(ExoplanetSurfaceHeightFacet.class);
        ExoplanetSurfaceTempFacet temperatureFacet = region.getRegionFacet(ExoplanetSurfaceTempFacet.class);
        ExoplanetHumidityFacet humidityFacet = region.getRegionFacet(ExoplanetHumidityFacet.class);
        ExoplanetSeaLevelFacet seaLevelFacet = region.getRegionFacet(ExoplanetSeaLevelFacet.class);

        Border3D border = region.getBorderForFacet(ExoplanetBiomeFacet.class);
        ExoplanetBiomeFacet biomeFacet = new ExoplanetBiomeFacet(region.getRegion(), border);

        int seaLevelWorldHeight = seaLevelFacet.getWorldSeaLevel();

        for (Vector2ic pos : biomeFacet.getRelativeArea()) {
            float temp = temperatureFacet.get(pos);
            float hum = temp * humidityFacet.get(pos);
            float height = surfaceHeightFacet.get(pos);

            if (height <= seaLevelWorldHeight) {
                biomeFacet.set(pos, ExoplanetBiome.OCEAN);
            } else if (height <= seaLevelWorldHeight + 2) {
                biomeFacet.set(pos, ExoplanetBiome.BEACH);
            } else if (height >= seaLevelWorldHeight + 80) {
                biomeFacet.set(pos, ExoplanetBiome.SNOWMOUNTAINS);
            } else if (temp >= 0.65f && hum < 0.3f) {
                biomeFacet.set(pos, ExoplanetBiome.DESERT);
            } else if (temp >= 0.5 && hum >= 0.3f && hum <= 0.6f) {
                biomeFacet.set(pos, ExoplanetBiome.PLAINS);
            } else if (temp <= 0.3 && hum > 0.5f) {
                biomeFacet.set(pos, ExoplanetBiome.SNOW);
            } else if (temp < 0.5f && hum >= 0.2f && hum <= 0.6f) {
                biomeFacet.set(pos, ExoplanetBiome.MOUNTAINS);
            } else {
                biomeFacet.set(pos, ExoplanetBiome.FOREST);
            }
        }
        region.setRegionFacet(ExoplanetBiomeFacet.class, biomeFacet);
    }
}
