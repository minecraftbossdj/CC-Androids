package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class BreakBlockTask extends MoveToBlockTask{
    private boolean cancel = false;

    public BreakBlockTask(AndroidEntity android, double moveSpeed) {
        super(android, moveSpeed);
    }

    @Override
    public String getName() {
        return "breakingBlock";
    }

    @Override
    public boolean shouldTick() {
        return !this.android.getWorld().isAir(getPos()) && !this.cancel;
    }

    @Override
    public void tick() {
        BlockPos pos = getPos();
        this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
        this.android.swingHand(Hand.MAIN_HAND);

        if (isInRange(5)) {
            this.android.swingHand(Hand.MAIN_HAND);
            this.cancel = !this.android.brain.getModules().miningModule.mineTowards(pos);
        }
        else
            super.tick();
    }
}
