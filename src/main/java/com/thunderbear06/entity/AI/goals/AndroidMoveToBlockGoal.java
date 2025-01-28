package com.thunderbear06.entity.AI.goals;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.math.BlockPos;

public class AndroidMoveToBlockGoal extends BaseAndroidGoal {

    public AndroidMoveToBlockGoal(AndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.brain.getState().equals("movingToBlock") && this.brain.hasTargetBlock();
    }

    @Override
    public void start() {
        BlockPos pos = this.brain.getTargetBlock();

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
