package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class AndroidUseItemOnEntityGoal extends BaseAndroidGoal{
    public AndroidUseItemOnEntityGoal(BaseAndroidEntity android, NewAndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        if (!super.canStart())
            return false;
        if (!this.brain.isInState("usingItemOnEntity"))
            return false;
        return this.brain.getTargeting().hasEntityTarget();
    }

    @Override
    public void tick() {
        LivingEntity target = this.brain.getTargeting().getEntityTarget();

        if (isInRangeOf(target, false)) {
            this.android.swingHand(Hand.MAIN_HAND);

            this.brain.getModules().interactionModule.useHandItemOnEntity(Hand.MAIN_HAND, target);

            this.brain.setState("idle");
        } else if (this.android.getNavigation().isIdle()) {
            this.android.getNavigation().startMovingTo(target, 0.5);
        }
    }
}
