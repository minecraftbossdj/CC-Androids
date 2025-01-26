package com.thunderbear06.entity.AI.goals;

import com.thunderbear06.entity.BaseAndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class AndroidMeleeAttackGoal extends BaseAndroidGoal {

    private int attackCooldown = 0;

    public AndroidMeleeAttackGoal(BaseAndroidEntity android) {
        super(android, android.brain);
    }

    @Override
    public boolean canStart() {
        return this.brain.state.equals("attacking") && this.brain.targetEntity != null && this.brain.targetEntity.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = this.brain.targetEntity;

        if (this.attackCooldown > 0)
            this.attackCooldown--;

        if (this.android.isInAttackRange(target)) {
            if (this.attackCooldown <= 0) {
                this.android.getLookControl().lookAt(target);
                this.android.swingHand(Hand.MAIN_HAND);
                this.android.tryAttack(target);
                this.attackCooldown = 10;
            }
        } else {
            this.android.getNavigation().startMovingTo(target, 0.5);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.brain.targetEntity = null;
    }
}
