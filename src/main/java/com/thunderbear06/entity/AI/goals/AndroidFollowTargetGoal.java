package com.thunderbear06.entity.AI.goals;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.AndroidEntity;

import java.util.Objects;

public class AndroidFollowTargetGoal extends BaseAndroidGoal {
    public AndroidFollowTargetGoal(AndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && Objects.equals(this.brain.state, "following") && this.brain.targetEntity != null;
    }

    @Override
    public void tick() {
        this.android.getNavigation().startMovingTo(this.brain.targetEntity, 0.5);
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && !this.android.getNavigation().isIdle();
    }

    @Override
    public void stop() {
        super.stop();
        this.android.getNavigation().stop();
        this.brain.targetEntity = null;
    }
}
