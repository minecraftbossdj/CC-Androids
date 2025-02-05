package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;

public class AndroidFollowTargetGoal extends BaseAndroidGoal {
    public AndroidFollowTargetGoal(BaseAndroidEntity android, NewAndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        if (!super.canStart())
            return false;
        if (!this.brain.isInState("following"))
            return false;

        return this.brain.getTargeting().hasEntityTarget();
    }

    @Override
    public void tick() {
        this.android.getNavigation().startMovingTo(this.brain.getTargeting().getEntityTarget(), 0.5);
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && !this.android.getNavigation().isIdle();
    }

    @Override
    public void stop() {
        this.android.getNavigation().stop();
        super.stop();
    }
}
