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
package org.terasology.exoplanet.generator;

public class ExoplanetTree {
    private final int TRUNK_HEIGHT = 4;
    private final int CROWN_HEIGHT = 2;
    private final int TOP_CROWN_HEIGHT = 1;
    private final int CROWN_RADIUS = 2;
    private final int TOP_CROWN_WIDTH = 3;

    public int getHeight() {
        return TRUNK_HEIGHT + CROWN_HEIGHT + TOP_CROWN_HEIGHT;
    }

    public int getWidth() {
        return (CROWN_RADIUS * 2) + 1;
    }

    public int getTrunkHeight() {
        return TRUNK_HEIGHT;
    }

    public int getCrownHeight() {
        return CROWN_HEIGHT;
    }

    public int getCrownRadius() {
        return CROWN_RADIUS;
    }

    public int getTopCrownHeight() {
        return TOP_CROWN_HEIGHT;
    }

    public int getTopCrownWidth() {
        return TOP_CROWN_WIDTH;
    }
}
