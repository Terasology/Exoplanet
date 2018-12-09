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
package org.terasology.world;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.generator.facets.ExoplanetSurfaceHeightFacet;
import org.terasology.logic.characters.CharacterTeleportEvent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.Region3i;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.World;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.generator.WorldGenerator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.terasology.generator.ExoplanetWorldGenerator.EXOPLANET_BORDER;
import static org.terasology.generator.ExoplanetWorldGenerator.EXOPLANET_HEIGHT;
import static org.terasology.generator.ExoplanetWorldGenerator.EXOPLANET_SEA_LEVEL;

@RegisterSystem(RegisterMode.CLIENT)
public class ExoplanetClientSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    @In
    private LocalPlayer localPlayer;

    @In
    private WorldGenerator worldGenerator;

    private Map<EntityRef, Vector3f> teleportQueue = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExoplanetClientSystem.class);

    @Override
    public void update(float delta) {
        if ((!teleportQueue.isEmpty())) {
            Iterator<Map.Entry<EntityRef, Vector3f>> teleportIt = teleportQueue.entrySet().iterator();
            while (teleportIt.hasNext()) {
                Map.Entry<EntityRef, Vector3f> entry = teleportIt.next();
                EntityRef character = entry.getKey();
                Vector3f targetPos = entry.getValue();
                character.send(new CharacterTeleportEvent(targetPos));
                teleportIt.remove();
            }
        }
    }

    @ReceiveEvent(components = {ExoplanetPortalOnActivateComponent.class, BlockComponent.class})
    public void onActivate(ActivateEvent event, EntityRef entity) {
        BlockComponent blockComponent = entity.getComponent(BlockComponent.class);

        EntityRef character = localPlayer.getCharacterEntity();
        EntityRef client = localPlayer.getClientEntity();

        Vector3f spawnPos;
        if (blockComponent.position.y >= EXOPLANET_BORDER) {
            spawnPos = findEarthSpawnPos(blockComponent.position);
            if (spawnPos != null) {
                character.send(new ExitExoplanetEvent(client));
                teleportQueue.put(character, spawnPos);
                LOGGER.info("Portal Activate Event Sent - Earth");
            }
        } else {
            spawnPos = findExoplanetSpawnPos(blockComponent.position);
            if (spawnPos != null) {
                character.send(new EnterExoplanetEvent(client));
                teleportQueue.put(character, spawnPos);
                LOGGER.info("Portal Activate Event Sent - Exoplanet");
            }
        }

        event.consume();
    }

    private Vector3f findExoplanetSpawnPos (Vector3i currentPos){
        World world = worldGenerator.getWorld();
        Vector3i searchRadius = new Vector3i(32, 1, 32);
        Region3i searchArea = Region3i.createFromCenterExtents(new Vector3i(currentPos.x, EXOPLANET_HEIGHT, currentPos.z), searchRadius);
        Region worldRegion = world.getWorldData(searchArea);

        ExoplanetSurfaceHeightFacet surfaceHeightFacet = worldRegion.getFacet(ExoplanetSurfaceHeightFacet.class);
        if (surfaceHeightFacet != null) {
            for (BaseVector2i pos : surfaceHeightFacet.getWorldRegion().contents()) {
                float surfaceHeight = surfaceHeightFacet.getWorld(pos);

                if (surfaceHeight >= EXOPLANET_SEA_LEVEL){
                    return new Vector3f(pos.x(), surfaceHeight + 1, pos.y());
                }
            }
        }
        return null;
    }

    private Vector3f findEarthSpawnPos (Vector3i currentPos){
        World world = worldGenerator.getWorld();
        Vector3i searchRadius = new Vector3i(32, 1, 32);
        Region3i searchArea = Region3i.createFromCenterExtents(new Vector3i(currentPos.x, 0, currentPos.z), searchRadius);
        Region worldRegion = world.getWorldData(searchArea);

        SurfaceHeightFacet surfaceHeightFacet = worldRegion.getFacet(SurfaceHeightFacet.class);
        if (surfaceHeightFacet != null) {
            for (BaseVector2i pos : surfaceHeightFacet.getWorldRegion().contents()) {
                float surfaceHeight = surfaceHeightFacet.getWorld(pos);

                if (surfaceHeight >= 32){
                    return new Vector3f(pos.x(), surfaceHeight + 1, pos.y());
                }
            }
        }
        return null;
    }
}
