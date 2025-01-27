package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;

public abstract class AndroidModule {
    public final BaseAndroidEntity owner;
    public final AndroidBrain brain;

    protected AndroidModule(BaseAndroidEntity owner, AndroidBrain brain) {
        this.owner = owner;
        this.brain = brain;
    }
}
