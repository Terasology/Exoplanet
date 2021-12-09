// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.exoplanet.teleport;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

public class ExitExoplanetEvent implements Event {
    EntityRef clientEntity;

    public ExitExoplanetEvent(EntityRef clientEntity) {
        this.clientEntity = clientEntity;
    }

    public EntityRef getClientEntity() {
        return clientEntity;
    }
}
