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

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.terasology.core.world.generator.facetProviders.*;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.core.world.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.SimpleUri;
import org.terasology.exoplanet.generator.providers.*;
import org.terasology.exoplanet.generator.rasterizers.*;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.registry.In;
import org.terasology.world.generation.BaseFacetedWorldGenerator;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

@RegisterWorldGenerator(id = "exoplanetWorld", displayName = "Exoplanet")
public class ExoplanetWorldGenerator extends BaseFacetedWorldGenerator {
    public static final int EXOPLANET_HEIGHT = 10000;
    public static final int EXOPLANET_BORDER = 9900;
    private static final Vector2ic SPAWN_POS = new Vector2i(0, 0);

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public ExoplanetWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    protected WorldBuilder createWorld() {
        int earthSeaLevel = 15;
        int exoplanetSeaLevel = 37;

        ImmutableVector2i spawnPos = new ImmutableVector2i(0, 0);

        return new WorldBuilder(worldGeneratorPluginLibrary)
                .setSeaLevel(earthSeaLevel)
                .addProvider(new SeaLevelProvider(earthSeaLevel))
                .addProvider(new SimplexHumidityProvider())
                .addProvider(new SimplexSurfaceTemperatureProvider())
                .addProvider(new SimplexBaseSurfaceProvider())
                .addProvider(new SimplexRiverProvider())
                .addProvider(new SimplexRoughnessProvider())
                .addProvider(new BiomeProvider())
                .addProvider(new SurfaceToDensityProvider())
                .addProvider(new DensityNoiseProvider())
                .addProvider(new DefaultFloraProvider())
                .addProvider(new DefaultTreeProvider())
                .addProvider(new SpawnPlateauProvider(SPAWN_POS))
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
                .addPlugins()
                .addRasterizer(new ExoplanetFloraRasterizer())
                .addRasterizer(new ExoplanetTreeRasterizer());
    }
}
