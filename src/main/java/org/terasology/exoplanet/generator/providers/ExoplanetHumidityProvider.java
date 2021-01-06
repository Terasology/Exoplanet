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
import org.terasology.exoplanet.generator.facets.ExoplanetHumidityFacet;
import org.terasology.math.TeraMath;
import org.terasology.utilities.procedural.BrownianNoise;
import org.terasology.utilities.procedural.PerlinNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;

@Produces(ExoplanetHumidityFacet.class)
public class ExoplanetHumidityProvider implements FacetProvider {
    private SubSampledNoise humidityNoise;

    @Override
    public void setSeed(long seed) {
        humidityNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 4), 4),
                new Vector2f(0.0006f, 0.0006f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ExoplanetHumidityFacet.class);
        ExoplanetHumidityFacet facet = new ExoplanetHumidityFacet(region.getRegion(), border);

        float[] noise = humidityNoise.noise(facet.getWorldArea());
        for (int i = 0; i < noise.length; ++i) {
            noise[i] = TeraMath.clamp((noise[i] * 2.11f + 1f) * 0.5f);
        }

        facet.set(noise);
        region.setRegionFacet(ExoplanetHumidityFacet.class, facet);
    }
}
