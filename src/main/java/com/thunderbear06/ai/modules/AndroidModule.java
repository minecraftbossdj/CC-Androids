package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;

public abstract class AndroidModule {
    public final BaseAndroidEntity android;
    public final NewAndroidBrain brain;

    public AndroidModule(BaseAndroidEntity owner, NewAndroidBrain brain) {
        this.android = owner;
        this.brain = brain;
    }
}
