// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.providers;

import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.exoplanet.generator.facets.ExoplanetSeaLevelFacet;

@Produces(ExoplanetSeaLevelFacet.class)
public class ExoplanetSeaLevelProvider implements FacetProvider {
    private final int seaLevel;

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
