package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.util.math.BlockPos;

public class AndroidMoveToBlockGoal extends BaseAndroidGoal {

    protected BlockPos pos;

    public AndroidMoveToBlockGoal(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.brain.getState().equals("movingToBlock") && this.brain.hasTargetBlock();
    }

    @Override
    public void start() {
        this.pos = this.brain.getTargetBlock();

        this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
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
