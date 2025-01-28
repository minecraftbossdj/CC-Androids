package com.thunderbear06.entity.AI.goals;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.util.math.BlockPos;

public class AndroidMineBlockGoal extends BaseAndroidGoal{
    private BlockPos pos;

    public AndroidMineBlockGoal(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        this.pos = this.brain.getTargetBlock();
        return super.canStart() && this.brain.getState().equals("miningBlock") && this.pos != null && this.brain.getMiningModule().canMineBlock(this.pos);
    }

    @Override
    public void tick() {
        BlockPos pos = this.pos;

        if (isInRangeOf(pos)) {
            this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
            this.brain.getMiningModule().mineWith(pos, this.android.getMainHandStack());
        } else if (this.android.getNavigation().isIdle()) {
            this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
        }
    }
}
