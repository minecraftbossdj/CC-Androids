package com.thunderbear06.entity.AI.tasks;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Hand;

import java.util.UUID;

public class AttackEntityTask extends TargetedAndroidTask{
    private final boolean once;

    private long lastHitTime = 0;

    public AttackEntityTask(BaseAndroidEntity androidEntity, AndroidBrain brain, UUID entityUUID, boolean once) {
        super(androidEntity, brain, entityUUID, "attacking");
        this.once = once;
    }

    @Override
    public void startTask() {}

    @Override
    public void tickTask() {

        if (!this.targetValid())
            cancel();

        if (!this.android.isInAttackRange(this.targetEntity)) {
            this.android.getNavigation().startMovingTo(this.targetEntity, 0.5);
            return;
        }

        if (this.android.getWorld().getTime() - this.lastHitTime < 20)
            return;

        attack();

        if (this.once)
            cancel();
    }

    @Override
    public void stopTask() {}

    private void attack() {
        this.android.setJumping(true);
        this.android.swingHand(Hand.MAIN_HAND);
        this.android.tryAttack(this.targetEntity);
        this.lastHitTime = this.android.getWorld().getTime();
    }
}
