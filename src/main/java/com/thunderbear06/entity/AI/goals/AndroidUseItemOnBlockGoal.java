package com.thunderbear06.entity.AI.goals;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AndroidUseItemOnBlockGoal extends BaseAndroidGoal{
    public AndroidUseItemOnBlockGoal(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        ItemStack heldStack = this.android.getMainHandStack();

        return super.canStart() && this.brain.state.equals("usingItemOnBlock") && !heldStack.isEmpty() && this.brain.getTargetBlock() != null;
    }

    @Override
    public void tick() {
        BlockPos pos = this.brain.getTargetBlock();

        if (!isInRangeOf(pos)) {
            if (this.android.getNavigation().isIdle()) {
                this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
            }
        } else {
            this.brain.interactionModule.useHandItemOnBlock(Hand.MAIN_HAND, pos);

            this.brain.setState("idle");
        }
    }
}
