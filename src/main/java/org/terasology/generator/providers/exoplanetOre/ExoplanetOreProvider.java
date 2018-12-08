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
package org.terasology.generator.providers.exoplanetOre;

import org.slf4j.LoggerFactory;
import org.terasology.generator.facets.ExoplanetOreFacet;
import org.terasology.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.math.geom.Vector3i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.world.generation.*;
import org.slf4j.Logger;

import static org.terasology.generator.ExoplanetWorldGenerator.EXOPLANET_BORDER;

@Requires(@Facet(ExoplanetSurfaceHeightFacet.class))
public abstract class ExoplanetOreProvider implements FacetProviderPlugin{
    public Noise oreNoise;

    private static final Logger LOG = LoggerFactory.getLogger(ExoplanetOreProvider.class);

    //Maximum height (y pos) that ore generate
    protected final int MAX_HEIGHT[] = new int[]
            {
                    -20,    //Coal
                    -30,    //Copper
                    -30,    //Iron
                    -50,    //Gold
                    -60     //Diamond
            };

    //Rarity of ore; More closer to 1, more rare; 1 - never generated
    protected final float RARITY[] = new float[]
            {
                    0.2f,    //Coal
                    0.3f,    //Copper
                    0.3f,    //Iron
                    0.5f,    //Gold
                    0.7f     //Diamond
            };

    //Vein size
    protected  final int MAX_SIZE[] = new int[]
            {
                    5,    //Coal
                    5,    //Copper
                    5,    //Iron
                    3,    //Gold
                    2     //Diamond
            };

    @Override
    public void setSeed(long seed) {
        oreNoise = new SimplexNoise(seed);
    }

    protected ExoplanetOreFacet baseProcess(GeneratingRegion region, Border3D border, int index) {
        ExoplanetOreFacet facet = new ExoplanetOreFacet(region.getRegion(), border);
        ExoplanetSurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(ExoplanetSurfaceHeightFacet.class);

        for (Vector3i pos : region.getRegion()) {
            if (pos.y < surfaceHeightFacet.getWorld(pos.x, pos.z) + MAX_HEIGHT[index] && pos.y > EXOPLANET_BORDER) {
                float noiseLevel = oreNoise.noise(pos.x, pos.y, pos.z);
                //if noise is not much, less than, equal to rarity then set the value to 0
                if (noiseLevel <= RARITY[index]) {
                    facet.setWorld(pos, 0);
                } else {
                    float subNoiseLevel = (1 - noiseLevel) / (1 - RARITY[index]);
                    float interval = subNoiseLevel / MAX_SIZE[index];
                    //Loop to set the facet's value (this will be used to set vein size in rasterizer)
                    for (int i = MAX_SIZE[index]; i > 0; i--) {
                        if (subNoiseLevel > interval * (i - 1)) {
                            facet.setWorld(pos, i);
                            break;
                        }
                    }
                }
            }
        }
        return facet;
    }
}
