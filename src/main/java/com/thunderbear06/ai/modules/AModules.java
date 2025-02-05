package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;

public class AModules {
    public final MiningModule miningModule;
    public final SensorModule sensorModule;
    public final InteractionModule interactionModule;

    public AModules(BaseAndroidEntity android, NewAndroidBrain brain) {
        this.miningModule = new MiningModule(android, brain);
        this.sensorModule = new SensorModule(android, brain, android.getEntitySearchRadius(), android.getBlockSearchRadius());
        this.interactionModule = new InteractionModule(android, brain);
    }
}
