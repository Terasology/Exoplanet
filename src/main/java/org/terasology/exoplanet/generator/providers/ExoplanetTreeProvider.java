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

import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.ExoplanetTree;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSeaLevelFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetTreeFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.biomes.Biome;
import org.terasology.world.generation.*;

@Produces(ExoplanetTreeFacet.class)
@Requires({@Facet(ExoplanetSurfaceHeightFacet.class), @Facet(ExoplanetSeaLevelFacet.class), @Facet(ExoplanetBiomeFacet.class)})
public class ExoplanetTreeProvider implements FacetProvider {
    private Noise treeNoise;

    @Override
    public void setSeed(long seed) {
        treeNoise = new WhiteNoise(seed + 8);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ExoplanetTreeFacet.class).extendBy(0, 7, 1);
        ExoplanetTreeFacet facet = new ExoplanetTreeFacet(region.getRegion(), border);

        ExoplanetSurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(ExoplanetSurfaceHeightFacet.class);
        ExoplanetBiomeFacet biomeFacet = region.getRegionFacet(ExoplanetBiomeFacet.class);
        ExoplanetSeaLevelFacet seaLevelFacet = region.getRegionFacet(ExoplanetSeaLevelFacet.class);

        int seaLevelWorldHeight = seaLevelFacet.getWorldSeaLevel();
        int maxGrowableAltitude = seaLevelWorldHeight + 100;
        Rect2i worldRegion = surfaceHeightFacet.getWorldRegion();

        for (int wz = worldRegion.minY(); wz <= worldRegion.maxY(); wz++) {
            for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
                int surfaceHeight = TeraMath.floorToInt(surfaceHeightFacet.getWorld(wx, wz));
                float treeDensity = 0f;

                Biome biome = biomeFacet.getWorld(wx, wz);
                if (biome instanceof ExoplanetBiome) {
                    ExoplanetBiome exoplanetBiome = (ExoplanetBiome) biome;
                    treeDensity = exoplanetBiome.getTreeDensity();
                }

                // check if height is within this region
                if (surfaceHeight >= facet.getWorldRegion().minY() &&
                        surfaceHeight <= facet.getWorldRegion().maxY() && surfaceHeight > seaLevelWorldHeight && surfaceHeight < maxGrowableAltitude) {

                    if (treeNoise.noise(wx, wz) > 1 - (treeDensity * 0.1f / 2f)) {
                        facet.setWorld(wx, surfaceHeight, wz, new ExoplanetTree());
                    }
                }
            }
        }

        region.setRegionFacet(ExoplanetTreeFacet.class, facet);
    }
}
