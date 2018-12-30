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

import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetHumidityFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceTempFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.world.generation.*;

import static org.terasology.exoplanet.generator.ExoplanetWorldGenerator.EXOPLANET_SEA_LEVEL;

@Produces(BiomeFacet.class)
@Requires({
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
        ExoplanetSurfaceHeightFacet heightFacet = region.getRegionFacet(ExoplanetSurfaceHeightFacet.class);
        ExoplanetSurfaceTempFacet temperatureFacet = region.getRegionFacet(ExoplanetSurfaceTempFacet.class);
        ExoplanetHumidityFacet humidityFacet = region.getRegionFacet(ExoplanetHumidityFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        BiomeFacet biomeFacet = new BiomeFacet(region.getRegion(), border);

        int seaLevel = EXOPLANET_SEA_LEVEL;

        for (BaseVector2i pos : biomeFacet.getRelativeRegion().contents()) {
            float temp = temperatureFacet.get(pos);
            float hum = temp * humidityFacet.get(pos);
            float height = heightFacet.get(pos);

            if (height <= seaLevel) {
                biomeFacet.set(pos, ExoplanetBiome.OCEAN);
            } else if (height <= seaLevel + 2) {
                biomeFacet.set(pos, ExoplanetBiome.BEACH);
            } else if (temp >= 0.7f && hum < 0.2f) {
                biomeFacet.set(pos, ExoplanetBiome.DESERT);
            } else if (hum >= 0.2f && hum <= 0.6f && temp >= 0.5f) {
                biomeFacet.set(pos, ExoplanetBiome.PLAINS);
            } else if (temp <= 0.3f && hum > 0.5f) {
                biomeFacet.set(pos, ExoplanetBiome.SNOW);
            } else if (hum >= 0.2f && hum <= 0.6f && temp < 0.5f) {
                biomeFacet.set(pos, ExoplanetBiome.MOUNTAINS);
            } else {
                biomeFacet.set(pos, ExoplanetBiome.FOREST);
            }
        }
        region.setRegionFacet(BiomeFacet.class, biomeFacet);
    }
}
