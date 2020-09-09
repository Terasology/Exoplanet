// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet;

import org.terasology.biomesAPI.BiomeRegistry;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;

import java.util.stream.Stream;

/**
 * Registers all Exoplanet biomes with the engine.
 */
@RegisterSystem
public class ExoplanetBiomes extends BaseComponentSystem {
    @In
    private BiomeRegistry biomeRegistry;

    /**
     * Registration of systems must be done in preBegin to be early enough.
     */
    @Override
    public void preBegin() {
        Stream.of(ExoplanetBiome.values()).forEach(biomeRegistry::registerBiome);
    }
}
