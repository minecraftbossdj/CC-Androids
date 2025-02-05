package com.thunderbear06.ai.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class UseOnEntityTask extends MoveToEntityTask {
    private boolean complete = false;

    public UseOnEntityTask(AndroidEntity android, double moveSpeed) {
        super(android, moveSpeed);
    }

    @Override
    public boolean shouldTick() {
        return super.shouldTick() && !this.complete;
    }

    @Override
    public void tick() {
        if (isInRange(2)) {
            this.android.brain.getModules().interactionModule.useHandItemOnEntity(Hand.MAIN_HAND, getTarget());
            this.complete = true;
        }
        else
            super.tick();
    }
}
