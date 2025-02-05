package com.thunderbear06.ai.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class UseOnBlockTask extends MoveToBlockTask{
    private boolean complete = false;

    public UseOnBlockTask(AndroidEntity android, double moveSpeed) {
        super(android, moveSpeed);
    }

    @Override
    public boolean shouldTick() {
        return !complete;
    }

    @Override
    public void tick() {
        if (isInRange(2)) {
            this.android.brain.getModules().interactionModule.useHandItemOnBlock(Hand.MAIN_HAND, getPos());
            this.complete = true;
        } else
            super.tick();
    }
}
