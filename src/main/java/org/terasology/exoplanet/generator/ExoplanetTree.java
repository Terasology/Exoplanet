// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator;

public class ExoplanetTree {
    private final int trunkHeight = 4;
    private final int crownHeight = 2;
    private final int topCrownHeight = 1;
    private final int crownRadius = 2;
    private final int topCrownWidth = 3;

    public int getHeight() {
        return trunkHeight + crownHeight + topCrownHeight;
    }

    public int getWidth() {
        return (crownRadius * 2) + 1;
    }

    public int getTrunkHeight() {
        return trunkHeight;
    }

    public int getCrownHeight() {
        return crownHeight;
    }

    public int getCrownRadius() {
        return crownRadius;
    }

    public int getTopCrownHeight() {
        return topCrownHeight;
    }

    public int getTopCrownWidth() {
        return topCrownWidth;
    }
}
