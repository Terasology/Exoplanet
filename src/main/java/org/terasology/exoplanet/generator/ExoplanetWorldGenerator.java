// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.generator;

import org.terasology.coreworlds.generator.facetProviders.BiomeProvider;
import org.terasology.coreworlds.generator.facetProviders.DefaultFloraProvider;
import org.terasology.coreworlds.generator.facetProviders.DefaultTreeProvider;
import org.terasology.coreworlds.generator.facetProviders.PerlinBaseSurfaceProvider;
import org.terasology.coreworlds.generator.facetProviders.PerlinHillsAndMountainsProvider;
import org.terasology.coreworlds.generator.facetProviders.PerlinHumidityProvider;
import org.terasology.coreworlds.generator.facetProviders.PerlinOceanProvider;
import org.terasology.coreworlds.generator.facetProviders.PerlinRiverProvider;
import org.terasology.coreworlds.generator.facetProviders.PerlinSurfaceTemperatureProvider;
import org.terasology.coreworlds.generator.facetProviders.PlateauProvider;
import org.terasology.coreworlds.generator.facetProviders.SeaLevelProvider;
import org.terasology.coreworlds.generator.facetProviders.SurfaceToDensityProvider;
import org.terasology.coreworlds.generator.rasterizers.FloraRasterizer;
import org.terasology.coreworlds.generator.rasterizers.SolidRasterizer;
import org.terasology.coreworlds.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.generation.BaseFacetedWorldGenerator;
import org.terasology.engine.world.generation.WorldBuilder;
import org.terasology.engine.world.generator.RegisterWorldGenerator;
import org.terasology.engine.world.generator.plugin.WorldGeneratorPluginLibrary;
import org.terasology.exoplanet.generator.providers.ExoplanetBiomeProvider;
import org.terasology.exoplanet.generator.providers.ExoplanetFloraProvider;
import org.terasology.exoplanet.generator.providers.ExoplanetHumidityProvider;
import org.terasology.exoplanet.generator.providers.ExoplanetMountainsProvider;
import org.terasology.exoplanet.generator.providers.ExoplanetSeaLevelProvider;
import org.terasology.exoplanet.generator.providers.ExoplanetSurfaceProvider;
import org.terasology.exoplanet.generator.providers.ExoplanetSurfaceTempProvider;
import org.terasology.exoplanet.generator.providers.ExoplanetTreeProvider;
import org.terasology.exoplanet.generator.rasterizers.ExoplanetFloraRasterizer;
import org.terasology.exoplanet.generator.rasterizers.ExoplanetTreeRasterizer;
import org.terasology.exoplanet.generator.rasterizers.ExoplanetWorldRasterizer;
import org.terasology.math.geom.ImmutableVector2i;

@RegisterWorldGenerator(id = "exoplanetWorld", displayName = "Exoplanet")
public class ExoplanetWorldGenerator extends BaseFacetedWorldGenerator {
    public static final int EXOPLANET_HEIGHT = 10000;
    public static final int EXOPLANET_BORDER = 9900;

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public ExoplanetWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    protected WorldBuilder createWorld() {
        int perlinSeaLevel = 32;
        int exoplanetSeaLevel = 37;

        ImmutableVector2i spawnPos = new ImmutableVector2i(0, 0);

        return new WorldBuilder(worldGeneratorPluginLibrary)
                // Perlin World (Earth)
                .setSeaLevel(perlinSeaLevel)
                .addProvider(new SeaLevelProvider(perlinSeaLevel))
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
                .addProvider(new PlateauProvider(spawnPos, perlinSeaLevel + 4, 10, 30))
                .addRasterizer(new SolidRasterizer())
                .addRasterizer(new FloraRasterizer())
                .addRasterizer(new TreeRasterizer())
                // Exoplanet World
                .addProvider(new ExoplanetSeaLevelProvider(exoplanetSeaLevel))
                .addProvider(new ExoplanetSurfaceProvider(EXOPLANET_HEIGHT))
                .addProvider(new ExoplanetHumidityProvider())
                .addProvider(new ExoplanetSurfaceTempProvider())
                .addProvider(new ExoplanetMountainsProvider(1.2f))
                .addProvider(new ExoplanetBiomeProvider())
                .addRasterizer(new ExoplanetWorldRasterizer())
                .addProvider(new ExoplanetFloraProvider())
                .addProvider(new ExoplanetTreeProvider())
                .addRasterizer(new ExoplanetFloraRasterizer())
                .addRasterizer(new ExoplanetTreeRasterizer());
    }
}
