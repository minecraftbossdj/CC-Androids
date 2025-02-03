package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AndroidMineBlockGoal extends BaseAndroidGoal{
    private BlockPos pos;

    public AndroidMineBlockGoal(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.brain.getState().equals("miningBlock") && this.brain.hasTargetBlock() && this.brain.getMiningModule().canMineBlock(this.pos);
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
