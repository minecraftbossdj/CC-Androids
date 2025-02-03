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
        this.pos = this.brain.getTargetBlock();

        if (this.pos == null)
            return false;

        World world = this.android.getWorld();

        if (world.getBlockState(pos.down()).isAir() && world.getBlockState(pos.down(2)).isAir() && world.getBlockState(pos.down(3)).isAir())
            return false;

        return super.canStart() && this.brain.getState().equals("miningBlock") && this.brain.getMiningModule().canMineBlock(this.pos);
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
