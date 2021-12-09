// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.facets;

import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import static org.terasology.exoplanet.generator.ExoplanetWorldGenerator.EXOPLANET_HEIGHT;

public class ExoplanetSeaLevelFacet extends BaseFacet2D {
    int localSeaLevel;

    public ExoplanetSeaLevelFacet(BlockRegion targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public int getLocalSeaLevel() {
        return localSeaLevel;
    }

    public int getWorldSeaLevel() {
        return localSeaLevel + EXOPLANET_HEIGHT;
    }

    public void setLocalSeaLevel(int seaLevel) {
        this.localSeaLevel = seaLevel;
    }
}
