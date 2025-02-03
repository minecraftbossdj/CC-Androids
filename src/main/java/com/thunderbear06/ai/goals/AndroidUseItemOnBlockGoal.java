package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class AndroidUseItemOnBlockGoal extends BaseAndroidGoal{
    public AndroidUseItemOnBlockGoal(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        ItemStack heldStack = this.android.getMainHandStack();

        return super.canStart() && this.brain.getState().equals("usingItemOnBlock") && !heldStack.isEmpty() && this.brain.getTargetBlock() != null;
    }

    @Override
    public void tick() {
        BlockPos pos = this.brain.getTargetBlock();

        if (!isInRangeOf(pos)) {
            if (this.android.getNavigation().isIdle()) {
                this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
            }
        } else {
            this.android.swingHand(Hand.MAIN_HAND);
            this.brain.getInteractionModule().useHandItemOnBlock(Hand.MAIN_HAND, pos);

            this.brain.setState("idle");
        }
    }
}
