package com.thunderbear06.entity.AI.goals;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;
import net.minecraft.util.math.BlockPos;

public class AndroidMineBlockGoal extends BaseAndroidGoal{
    private BlockPos pos;

    public AndroidMineBlockGoal(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        this.pos = this.brain.targetBlock;
        return super.canStart() && this.brain.state.equals("miningBlock") && this.pos != null && this.brain.miningModule.canMineBlock(this.pos);
    }

    @Override
    public void tick() {
        BlockPos pos = this.pos;

        if (isInRangeOf(pos)) {
            if (!this.android.getNavigation().isIdle())
                return;
            this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
        } else {
            this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
            this.brain.miningModule.mineWith(pos, this.android.getMainHandStack());
        }
    }
}
