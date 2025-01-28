package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;

public abstract class AndroidModule {
    public final BaseAndroidEntity owner;
    public final AndroidBrain brain;

    public AndroidModule(BaseAndroidEntity owner, AndroidBrain brain) {
        this.owner = owner;
        this.brain = brain;
    }
}
