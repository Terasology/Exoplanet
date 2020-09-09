// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.facets;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.SparseObjectFacet3D;
import org.terasology.exoplanet.generator.ExoplanetTree;

public class ExoplanetTreeFacet extends SparseObjectFacet3D<ExoplanetTree> {

    public ExoplanetTreeFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
