/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.exoplanet.generator.providers;

import org.joml.Vector2ic;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetHumidityFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSeaLevelFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceTempFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;

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
