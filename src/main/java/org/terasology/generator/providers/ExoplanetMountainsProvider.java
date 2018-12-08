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

import org.terasology.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.utilities.procedural.*;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Updates;

@Updates(@Facet(ExoplanetSurfaceHeightFacet.class))
public class ExoplanetMountainsProvider implements FacetProvider {
    private Noise mountainNoise;
    private int mountainHeight = 500;

    public ExoplanetMountainsProvider(int mountainHeight) {
        this.mountainHeight = mountainHeight;
    }

    @Override
    public void setSeed(long seed) {
        mountainNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 2), 8),
                new Vector2f(0.001f, 0.001f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        ExoplanetSurfaceHeightFacet facet = region.getRegionFacet(ExoplanetSurfaceHeightFacet.class);

        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position : processRegion.contents()) {
            float additiveMountainHeight = mountainNoise.noise(position.x(), position.y()) * mountainHeight;
            additiveMountainHeight = TeraMath.clamp(additiveMountainHeight, 0, mountainHeight);

            facet.setWorld(position, facet.getWorld(position) + additiveMountainHeight);
        }
    }
}
