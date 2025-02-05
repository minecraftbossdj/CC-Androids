package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class AndroidUseItemOnBlockGoal extends BaseAndroidGoal{
    public AndroidUseItemOnBlockGoal(BaseAndroidEntity android, NewAndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        if (!super.canStart())
            return false;

        ItemStack heldStack = this.android.getMainHandStack();

        if (heldStack.isEmpty())
            return false;

        if (!this.brain.isInState("usingItemOnBlock"))
            return false;

        return this.brain.getTargeting().hasBlockTarget();
    }

    @Override
    public void tick() {
        BlockPos pos = this.brain.getTargeting().getBlockTarget();

        if (!isInRangeOf(pos)) {
            if (this.android.getNavigation().isIdle()) {
                this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
            }
        } else {
            this.android.swingHand(Hand.MAIN_HAND);
            this.brain.getModules().interactionModule.useHandItemOnBlock(Hand.MAIN_HAND, pos);

            this.brain.setState("idle");
        }
    }
}
