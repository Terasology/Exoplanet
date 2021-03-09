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
package org.terasology.exoplanet.teleport;

import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.chat.ChatMessageEvent;
import org.terasology.engine.logic.inventory.InventoryComponent;
import org.terasology.engine.logic.inventory.InventoryManager;
import org.terasology.engine.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.items.BlockItemFactory;

@RegisterSystem(RegisterMode.AUTHORITY)
public class ExoplanetAuthoritySystem extends BaseComponentSystem {
    @In
    EntityManager entityManager;
    @In
    BlockManager blockManager;
    @In
    InventoryManager inventoryManager;

    @ReceiveEvent
    public void onEnterExoplanet(EnterExoplanetEvent event, EntityRef clientEntity) {
        event.getClientEntity().send(new ChatMessageEvent("Teleporting to Exoplanet: TS-180812R", EntityRef.NULL));
    }

    @ReceiveEvent
    public void onExitExoplanet(ExitExoplanetEvent event, EntityRef clientEntity) {
        event.getClientEntity().send(new ChatMessageEvent("Teleporting back to Earth", EntityRef.NULL));
    }

    @ReceiveEvent(components = InventoryComponent.class)
    public void onPlayerSpawnedEvent(OnPlayerSpawnedEvent event, EntityRef player) {
        BlockItemFactory blockFactory = new BlockItemFactory(entityManager);

        inventoryManager.giveItem(player, EntityRef.NULL, blockFactory.newInstance(blockManager.getBlockFamily("Exoplanet:ExoplanetPortal"), 2));
    }
}
