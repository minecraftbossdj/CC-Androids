package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.util.math.BlockPos;

public class AndroidMineBlockGoal extends BaseAndroidGoal{

    public AndroidMineBlockGoal(BaseAndroidEntity android, NewAndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        if (!super.canStart())
            return false;
        if (!this.brain.isInState("miningBlock"))
            return false;
        if (!this.brain.getTargeting().hasBlockTarget())
            return false;
        return this.brain.getModules().miningModule.canMineBlock(this.brain.getTargeting().getBlockTarget());
    }

    @Override
    public void tick() {
        BlockPos pos = this.brain.getTargeting().getBlockTarget();

        if (isInRangeOf(pos)) {
            this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
            this.brain.getModules().miningModule.mine(pos);
        } else if (this.android.getNavigation().isIdle()) {
            this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
        }
    }
}
