package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;

public abstract class AndroidModule {
    public final BaseAndroidEntity owner;
    public final AndroidBrain brain;

    public AndroidModule(BaseAndroidEntity owner, AndroidBrain brain) {
        this.owner = owner;
        this.brain = brain;
    }
}
