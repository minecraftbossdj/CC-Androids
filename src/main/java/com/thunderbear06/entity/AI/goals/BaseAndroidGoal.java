package com.thunderbear06.entity.AI.goals;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;
import net.minecraft.entity.ai.goal.Goal;

public abstract class BaseAndroidGoal extends Goal {
    protected final BaseAndroidEntity android;
    protected final AndroidBrain brain;

    public BaseAndroidGoal(BaseAndroidEntity android, AndroidBrain brain) {
        this.android = android;
        this.brain = brain;
    }

    @Override
    public boolean canStart() {
        return this.android.on;
    }

    @Override
    public void stop() {
        this.brain.setState("idle");
    }
}
