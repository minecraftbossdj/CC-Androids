package com.thunderbear06.entity.AI.goals;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.AndroidEntity;
import net.minecraft.util.math.BlockPos;

public class MoveToBlockGoal extends BaseAndroidGoal {

    public MoveToBlockGoal(AndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        return this.brain.state.equals("movingToBlock") && this.brain.targetBlock != null;
    }

    @Override
    public void start() {
        BlockPos pos = this.brain.targetBlock;

        this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && !this.android.getNavigation().isIdle();
    }

    @Override
    public void stop() {
        super.stop();
        this.android.getNavigation().stop();
        this.brain.targetBlock = null;
    }
}
