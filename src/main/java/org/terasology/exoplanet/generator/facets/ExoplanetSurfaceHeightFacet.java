// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.facets;

import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFieldFacet2D;

public class ExoplanetSurfaceHeightFacet extends BaseFieldFacet2D {
    private int baseSurfaceHeight;

    private final int rockyLayerDepth = 10;

    public ExoplanetSurfaceHeightFacet(BlockRegion targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void setBaseSurfaceHeight(int baseSurfaceHeight) {
        this.baseSurfaceHeight = baseSurfaceHeight;
    }

    public int getBaseSurfaceHeight() {
        return baseSurfaceHeight;
    }

    public int getRockLayerDepth() {
        return rockyLayerDepth;
    }
}
