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
        return super.canStart() && this.brain.getState().equals("attacking") && this.brain.getTargetEntity() != null && this.brain.getTargetEntity().isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = this.brain.getTargetEntity();

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
