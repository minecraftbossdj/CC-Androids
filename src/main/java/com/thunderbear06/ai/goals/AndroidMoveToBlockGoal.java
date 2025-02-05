package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.util.math.BlockPos;

public class AndroidMoveToBlockGoal extends BaseAndroidGoal {

    protected BlockPos pos;

    public AndroidMoveToBlockGoal(BaseAndroidEntity android, NewAndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        if (!super.canStart())
            return false;
        if (!this.brain.isInState("movingToBlock"))
            return false;
        return this.brain.getTargeting().hasBlockTarget();
    }

    @Override
    public void start() {
        this.pos = this.brain.getTargeting().getBlockTarget();

        this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
    }

    @Override
    public void tick() {
        if (this.android.getNavigation().isIdle()) {
            this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
        }
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && !isInRangeOf(this.pos);
    }

    @Override
    public void stop() {
        super.stop();
        this.android.getNavigation().stop();
    }
}
