// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.teleport;

import org.joml.Vector2ic;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.characters.CharacterTeleportEvent;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.logic.spawner.FixedSpawner;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.World;
import org.terasology.engine.world.generator.WorldGenerator;
import org.terasology.exoplanet.generator.facets.ExoplanetSeaLevelFacet;
import org.terasology.exoplanet.generator.facets.ExoplanetSurfaceHeightFacet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.terasology.exoplanet.generator.ExoplanetWorldGenerator.EXOPLANET_BORDER;
import static org.terasology.exoplanet.generator.ExoplanetWorldGenerator.EXOPLANET_HEIGHT;

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
        if (blockComponent.getPosition().y() >= EXOPLANET_BORDER) {
            spawnPos = findEarthSpawnPos(blockComponent.getPosition(new Vector3i()), character);
            if (spawnPos != null) {
                character.send(new ExitExoplanetEvent(client));
                teleportQueue.put(character, spawnPos);
                LOGGER.info("Portal Activate Event Sent - Earth");
            }
        } else {
            spawnPos = findExoplanetSpawnPos(blockComponent.getPosition(new Vector3i()));
            if (spawnPos != null) {
                character.send(new EnterExoplanetEvent(client));
                teleportQueue.put(character, spawnPos);
                LOGGER.info("Portal Activate Event Sent - Exoplanet");
            }
        }

        event.consume();
    }

    private Vector3f findExoplanetSpawnPos(Vector3i currentPos) {
        World world = worldGenerator.getWorld();
        Vector3i searchRadius = new Vector3i(32, 1, 32);
        BlockRegion searchArea = new BlockRegion(currentPos.x, EXOPLANET_HEIGHT, currentPos.z).expand(searchRadius);
        Region worldRegion = world.getWorldData(searchArea);

        ExoplanetSeaLevelFacet seaLevelFacet = worldRegion.getFacet(ExoplanetSeaLevelFacet.class);
        int seaLevelWorldHeight = seaLevelFacet.getWorldSeaLevel();

        ExoplanetSurfaceHeightFacet surfaceHeightFacet = worldRegion.getFacet(ExoplanetSurfaceHeightFacet.class);
        if (surfaceHeightFacet != null) {
            for (Vector2ic pos : surfaceHeightFacet.getWorldArea()) {
                float surfaceHeight = surfaceHeightFacet.getWorld(pos);

                if (surfaceHeight >= seaLevelWorldHeight) {
                    return new Vector3f(pos.x(), surfaceHeight + 1, pos.y());
                }
            }
        }
        return null;
    }

    private Vector3f findEarthSpawnPos(Vector3i currentPos, EntityRef character) {
        return new FixedSpawner(currentPos.x, currentPos.z).getSpawnPosition(worldGenerator.getWorld(), character);
    }
}
