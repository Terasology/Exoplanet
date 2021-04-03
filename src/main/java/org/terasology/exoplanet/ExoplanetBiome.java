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
package org.terasology.exoplanet;

import org.terasology.biomesAPI.Biome;
import org.terasology.gestalt.naming.Name;

public enum ExoplanetBiome implements Biome {
    MOUNTAINS("Mountains", 0.4f, 0.4f, 0.4f, 0.4f),
    SNOWMOUNTAINS("Snow Mountains", 0.5f, 0.3f, 0.3f, 0.25f),
    SNOW("Snow", 0.6f, 0.2f, 0.3f, 0.3f),
    DESERT("Desert", 0.15f, 0.75f, 0f, 0f),
    FOREST("Forest", 0.6f, 0.5f, 0.4f, 0.5f),
    OCEAN("Ocean", 1.0f, 0.4f, 0.4f, 0f),
    BEACH("Beach", 0.5f, 0.4f, 0.3f, 0f),
    PLAINS("Plains", 0.5f, 0.55f, 0.45f, 0.25f);

    private final Name id;
    private final String name;
    private final float humidity;
    private final float temperature;
    private final float floraDensity;
    private final float treeDensity;

    ExoplanetBiome(String name, float humidity, float temperature, float floraDensity, float treeDensity) {
        this.id = new Name("Exoplanet:" + name().toLowerCase());
        this.name = name;
        this.humidity = humidity;
        this.temperature = temperature;
        this.floraDensity = floraDensity;
        this.treeDensity = treeDensity;
    }

    @Override
    public Name getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getFloraDensity() {
        return floraDensity;
    }

    public float getTreeDensity() {
        return treeDensity;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
