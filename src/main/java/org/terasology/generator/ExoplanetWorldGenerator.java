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
package org.terasology.generator;

import org.terasology.core.world.generator.facetProviders.*;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.core.world.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.SimpleUri;
import org.terasology.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.generator.providers.ExoplanetMountainsProvider;
import org.terasology.generator.providers.ExoplanetSurfaceProvider;
import org.terasology.generator.providers.ExoplanetTreeProvider;
import org.terasology.generator.rasterizers.ExoplanetOceanRasterizer;
import org.terasology.generator.rasterizers.ExoplanetTreeRasterizer;
import org.terasology.generator.rasterizers.ExoplanetWorldRasterizer;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.registry.In;
import org.terasology.world.generation.BaseFacetedWorldGenerator;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;
import org.terasology.world.zones.ConstantLayerThickness;
import org.terasology.world.zones.LayeredZoneRegionFunction;
import org.terasology.world.zones.SingleBlockRasterizer;
import org.terasology.world.zones.Zone;

import static org.terasology.world.zones.LayeredZoneRegionFunction.LayeredZoneOrdering.ABOVE_GROUND;
import static org.terasology.world.zones.LayeredZoneRegionFunction.LayeredZoneOrdering.GROUND;

@RegisterWorldGenerator(id = "exoplanetWorld", displayName = "Exoplanet")
public class ExoplanetWorldGenerator extends BaseFacetedWorldGenerator {
    public static final int EXOPLANET_HEIGHT = 10000;
    public static final int EXOPLANET_BORDER = 9900;
    public static final int EXOPLANET_SEA_LEVEL = 10020;
    public static final int EXOPLANET_MOUNTAIN_HEIGHT = 400;

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public ExoplanetWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    protected WorldBuilder createWorld() {
        int seaLevel = 32;
        ImmutableVector2i spawnPos = new ImmutableVector2i(0, 0);

        return new WorldBuilder(worldGeneratorPluginLibrary)
                // Perlin World (Earth)
                .setSeaLevel(seaLevel)
                .addProvider(new SeaLevelProvider(seaLevel))
                .addProvider(new PerlinHumidityProvider())
                .addProvider(new PerlinSurfaceTemperatureProvider())
                .addProvider(new PerlinBaseSurfaceProvider())
                .addProvider(new PerlinRiverProvider())
                .addProvider(new PerlinOceanProvider())
                .addProvider(new PerlinHillsAndMountainsProvider())
                .addProvider(new BiomeProvider())
                .addProvider(new SurfaceToDensityProvider())
                .addProvider(new DefaultFloraProvider())
                .addProvider(new DefaultTreeProvider())
                .addProvider(new PlateauProvider(spawnPos, seaLevel + 4, 10, 30))
                .addRasterizer(new SolidRasterizer())
                .addRasterizer(new FloraRasterizer())
                .addRasterizer(new TreeRasterizer())
                // Exoplanet World
                .addProvider(new ExoplanetSurfaceProvider(EXOPLANET_HEIGHT))
                .addProvider(new ExoplanetMountainsProvider(EXOPLANET_MOUNTAIN_HEIGHT))
                .addRasterizer(new ExoplanetWorldRasterizer())
                .addZone(new Zone("ExoplanetSurface", new LayeredZoneRegionFunction(new ConstantLayerThickness(10),
                        ABOVE_GROUND + EXOPLANET_HEIGHT))

                    .addZone(new Zone("ExoplanetOcean", (x, y, z, region) ->
                            TeraMath.floorToInt(region.getFacet(ExoplanetSurfaceHeightFacet.class).getWorld(x, z)) < y
                                    && y <= EXOPLANET_SEA_LEVEL )

                            .addRasterizer(new ExoplanetOceanRasterizer())
                            ))
                .addProvider(new ExoplanetTreeProvider())
                .addRasterizer(new ExoplanetTreeRasterizer());

//                .addZone(new Zone("ExoplanetGround", new LayeredZoneRegionFunction(new ConstantLayerThickness(10),
//                        GROUND + EXOPLANET_HEIGHT))
//
//                        .addZone(new Zone("ExoplanetBeach", (x, y, z, region) ->
//                                region.getFacet(ExoplanetSurfaceHeightFacet.class).getWorld(x, z) <= EXOPLANET_SEA_LEVEL + 3)
//                                .addRasterizer(new SingleBlockRasterizer("core:sand")))
//                );
    }
}
