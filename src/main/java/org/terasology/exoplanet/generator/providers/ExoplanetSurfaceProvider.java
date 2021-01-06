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

import org.joml.Vector2f;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.geom.Rect2i;
import org.terasology.utilities.procedural.BrownianNoise;
import org.terasology.utilities.procedural.PerlinNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.block.BlockAreac;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;

@Produces(ExoplanetSurfaceHeightFacet.class)
public class ExoplanetSurfaceProvider implements FacetProvider {
    private SubSampledNoise surfaceNoise;
    private int exoplanetWorldHeight;

    private int baseHeight = 20;

    public ExoplanetSurfaceProvider(int worldHeight) {
        this.exoplanetWorldHeight = worldHeight;
    }

    @Override
    public void setSeed(long seed) {
        BrownianNoise source = new BrownianNoise(new PerlinNoise(seed), 8);
        surfaceNoise = new SubSampledNoise(source, new Vector2f(0.004f, 0.004f),  4);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ExoplanetSurfaceHeightFacet.class);
        ExoplanetSurfaceHeightFacet facet = new ExoplanetSurfaceHeightFacet(region.getRegion(), border);
        facet.setBaseSurfaceHeight(exoplanetWorldHeight);

        BlockAreac processRegion = facet.getWorldArea();
        float[] noise = surfaceNoise.noise(processRegion);

        for (int i = 0; i < noise.length; ++i) {
            noise[i] = (baseHeight + baseHeight * ((noise[i] * 2.11f + 1f) / 2f)) + exoplanetWorldHeight;
        }

        facet.set(noise);
        region.setRegionFacet(ExoplanetSurfaceHeightFacet.class, facet);
    }
}
