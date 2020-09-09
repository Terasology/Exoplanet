// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.facets;

import org.terasology.biomesAPI.Biome;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseObjectFacet2D;

public class ExoplanetBiomeFacet extends BaseObjectFacet2D<Biome> {
    public ExoplanetBiomeFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border, Biome.class);
    }
}
