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
package org.terasology.generator.providers;

import org.terasology.generator.ExoplanetTree;
import org.terasology.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.generator.facets.ExoplanetTreeFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.*;

@Produces(ExoplanetTreeFacet.class)
@Requires(@Facet(ExoplanetSurfaceHeightFacet.class))
public class ExoplanetTreeProvider implements FacetProvider {
    private Noise treeNoise;

    @Override
    public void setSeed(long seed) {
        treeNoise = new WhiteNoise(seed + 20);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ExoplanetTreeFacet.class).extendBy(0, 7, 1);
        ExoplanetTreeFacet facet = new ExoplanetTreeFacet(region.getRegion(), border);

        ExoplanetSurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(ExoplanetSurfaceHeightFacet.class);
        Rect2i worldRegion = surfaceHeightFacet.getWorldRegion();

        for (int wz = worldRegion.minY(); wz <= worldRegion.maxY(); wz++) {
            for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
                int surfaceHeight = TeraMath.floorToInt(surfaceHeightFacet.getWorld(wx, wz));

                // check if height is within this region
                if (surfaceHeight >= facet.getWorldRegion().minY() &&
                        surfaceHeight <= facet.getWorldRegion().maxY()) {

                    if (treeNoise.noise(wx, wz) > 0.99) {
                        facet.setWorld(wx, surfaceHeight, wz, new ExoplanetTree());
                    }
                }
            }
        }

        region.setRegionFacet(ExoplanetTreeFacet.class, facet);
    }
}
