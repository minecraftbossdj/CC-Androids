package com.thunderbear06.ai.goals;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class AndroidMeleeAttackGoal extends BaseAndroidGoal {

    private int attackCooldown = 0;

    public AndroidMeleeAttackGoal(BaseAndroidEntity android) {
        super(android, android.brain);
    }

    @Override
    public boolean canStart() {
        if (!super.canStart())
            return false;
        if (!this.brain.isInState("attacking"))
            return false;
        return this.brain.getTargeting().hasEntityTarget();
    }

    @Override
    public void tick() {
        LivingEntity target = this.brain.getTargeting().getEntityTarget();

        if (this.attackCooldown > 0)
            this.attackCooldown--;

        if (isInRangeOf(target, true)) {
            if (this.attackCooldown <= 0) {
                this.android.getLookControl().lookAt(target);
                this.android.swingHand(Hand.MAIN_HAND);
                this.android.tryAttack(target);
                this.attackCooldown = 10;
            }
        } else if (this.android.getNavigation().isIdle()) {
            this.android.getNavigation().startMovingTo(target, 0.5);
        }
    }

    @Override
    public void stop() {
        super.stop();
    }
}
