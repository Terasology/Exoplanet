// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.facets;

import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFieldFacet2D;

public class ExoplanetSurfaceTempFacet extends BaseFieldFacet2D {

    public ExoplanetSurfaceTempFacet(BlockRegion targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
