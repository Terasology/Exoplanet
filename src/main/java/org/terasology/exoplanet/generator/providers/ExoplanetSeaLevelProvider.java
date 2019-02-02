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

import org.terasology.exoplanet.generator.facets.ExoplanetSeaLevelFacet;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;

@Produces(ExoplanetSeaLevelFacet.class)
public class ExoplanetSeaLevelProvider implements FacetProvider {
    private int seaLevel;

    public ExoplanetSeaLevelProvider() {
        seaLevel = 20;
    }

    public ExoplanetSeaLevelProvider(int seaLevel) {
        this.seaLevel = seaLevel;
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ExoplanetSeaLevelFacet.class);
        ExoplanetSeaLevelFacet facet = new ExoplanetSeaLevelFacet(region.getRegion(), border);
        facet.setLocalSeaLevel(seaLevel);
        region.setRegionFacet(ExoplanetSeaLevelFacet.class, facet);
    }
}
