package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class AndroidUseItemOnEntityGoal extends BaseAndroidGoal{
    public AndroidUseItemOnEntityGoal(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.brain.getState().equals("usingItemOnEntity") && this.brain.hasTargetEntity();
    }

    @Override
    public void tick() {
        LivingEntity target = this.brain.getTargetEntity();

        if (isInRangeOf(target, false)) {
            this.android.swingHand(Hand.MAIN_HAND);

            this.brain.getInteractionModule().useHandItemOnEntity(Hand.MAIN_HAND, target);

            this.brain.setState("idle");
        } else if (this.android.getNavigation().isIdle()) {
            this.android.getNavigation().startMovingTo(target, 0.5);
        }
    }
}
