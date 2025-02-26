package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class BreakBlockTask extends MoveToBlockTask{

    public BreakBlockTask(AndroidEntity android, double moveSpeed) {
        super(android, moveSpeed);
    }

    @Override
    public String getName() {
        return "breakingBlock";
    }

    @Override
    public boolean shouldTick() {
        return !this.android.getWorld().isAir(getPos());
    }

    @Override
    public void tick() {
        BlockPos pos = getPos();
        this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
        this.android.swingHand(Hand.MAIN_HAND);

        if (isInRange(3)) {
            this.android.swingHand(Hand.MAIN_HAND);
            this.android.brain.getModules().miningModule.mine(getPos());
        }
        else
            super.tick();
    }
}
