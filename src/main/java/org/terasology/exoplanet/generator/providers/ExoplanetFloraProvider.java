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
import org.terasology.biomesAPI.Biome;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetFloraFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
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
