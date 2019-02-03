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

import org.terasology.exoplanet.generator.facets.ExoplanetHumidityFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceTempFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.utilities.procedural.*;
import org.terasology.world.generation.*;

import java.util.Iterator;

@Updates(@Facet(ExoplanetSurfaceHeightFacet.class))
@Requires({@Facet(ExoplanetSurfaceTempFacet.class), @Facet(ExoplanetHumidityFacet.class)})
public class ExoplanetMountainsProvider implements FacetProvider {
    private SubSampledNoise mountainNoise;
    private SubSampledNoise hillNoise;

    private float amplitude;

    public ExoplanetMountainsProvider(float amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public void setSeed(long seed) {
        mountainNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 2), 8),
                new Vector2f(0.001f, 0.001f), 1);
        hillNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 3)),
                new Vector2f(0.0004f, 0.0004f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        ExoplanetSurfaceHeightFacet facet = region.getRegionFacet(ExoplanetSurfaceHeightFacet.class);

        float[] mountainData = mountainNoise.noise(facet.getWorldRegion());
        float[] hillData = hillNoise.noise(facet.getWorldRegion());

        ExoplanetSurfaceTempFacet tempFacet = region.getRegionFacet(ExoplanetSurfaceTempFacet.class);
        ExoplanetHumidityFacet humidityFacet = region.getRegionFacet(ExoplanetHumidityFacet.class);

        float[] heightData = facet.getInternal();
        Iterator<BaseVector2i> positionIterator = facet.getRelativeRegion().contents().iterator();
        for (int i = 0; i < heightData.length; ++i) {
            BaseVector2i pos = positionIterator.next();
            float temp = tempFacet.get(pos);
            float hum = humidityFacet.get(pos);
            Vector2f distanceToMountainBiome = new Vector2f(temp - 0.25f, (temp * hum) - 0.35f);
            float mIntens = TeraMath.clamp(1.0f - distanceToMountainBiome.length() * 3.0f);
            float densityMountains = Math.max(mountainData[i] * 2.12f, 0) * mIntens * amplitude;
            float densityHills = Math.max(hillData[i] * 2.12f - 0.1f, 0) * (1.0f - mIntens) * amplitude;

            heightData[i] = heightData[i] + 512 * densityMountains + 64 * densityHills;
        }
    }
}

