package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.BaseAndroidEntity;

import java.util.Objects;

public class AndroidFollowTargetGoal extends BaseAndroidGoal {
    public AndroidFollowTargetGoal(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && Objects.equals(this.brain.getState(), "following") && this.brain.getTargetEntity() != null;
    }

    @Override
    public void tick() {
        this.android.getNavigation().startMovingTo(this.brain.getTargetEntity(), 0.5);
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && !this.android.getNavigation().isIdle();
    }

    @Override
    public void stop() {
        super.stop();
        this.android.getNavigation().stop();
    }
}
