// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator.providers;

import org.terasology.biomesAPI.Biome;
import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.WhiteNoise;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.exoplanet.ExoplanetBiome;
import org.terasology.exoplanet.generator.ExoplanetTree;
import org.terasology.exoplanet.generator.facets.ExoplanetBiomeFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSeaLevelFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetTreeFacet;
import org.terasology.math.TeraMath;

@Produces(ExoplanetTreeFacet.class)
@Requires({@Facet(ExoplanetSurfaceHeightFacet.class), @Facet(ExoplanetSeaLevelFacet.class), @Facet(ExoplanetBiomeFacet.class)})
public class ExoplanetTreeProvider implements FacetProvider {
    private Noise treeNoise;

    @Override
    public void setSeed(long seed) {
        treeNoise = new WhiteNoise(seed + 8);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ExoplanetTreeFacet.class).extendBy(0, 7, 1);
        ExoplanetTreeFacet facet = new ExoplanetTreeFacet(region.getRegion(), border);

        ExoplanetSurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(ExoplanetSurfaceHeightFacet.class);
        ExoplanetBiomeFacet biomeFacet = region.getRegionFacet(ExoplanetBiomeFacet.class);
        ExoplanetSeaLevelFacet seaLevelFacet = region.getRegionFacet(ExoplanetSeaLevelFacet.class);

        int seaLevelWorldHeight = seaLevelFacet.getWorldSeaLevel();
        int maxGrowableAltitude = seaLevelWorldHeight + 100;
        BlockAreac worldRegion = surfaceHeightFacet.getWorldArea();

        for (int wz = worldRegion.minY(); wz <= worldRegion.maxY(); wz++) {
            for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
                int surfaceHeight = TeraMath.floorToInt(surfaceHeightFacet.getWorld(wx, wz));
                float treeDensity = 0f;

                Biome biome = biomeFacet.getWorld(wx, wz);
                if (biome instanceof ExoplanetBiome) {
                    ExoplanetBiome exoplanetBiome = (ExoplanetBiome) biome;
                    treeDensity = exoplanetBiome.getTreeDensity();
                }

                // check if height is within this region
                if (surfaceHeight >= facet.getWorldRegion().minY()
                        && surfaceHeight <= facet.getWorldRegion().maxY()
                        && surfaceHeight > seaLevelWorldHeight
                        && surfaceHeight < maxGrowableAltitude) {

                    if (treeNoise.noise(wx, wz) > 1 - (treeDensity * 0.1f / 2f)) {
                        facet.setWorld(wx, surfaceHeight, wz, new ExoplanetTree());
                    }
                }
            }
        }

        region.setRegionFacet(ExoplanetTreeFacet.class, facet);
    }
}
