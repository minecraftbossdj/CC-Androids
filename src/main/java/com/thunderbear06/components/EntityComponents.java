package com.thunderbear06.components;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import com.thunderbear06.entity.android.AndroidEntity;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;

public class EntityComponents implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(AndroidEntity.class, IBaritone.KEY, BaritoneAPI.getProvider().componentFactory());
    }
}
