package com.thunderbear06.ai.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MineBlockTask extends MoveToBlockTask{
    public MineBlockTask(AndroidEntity android, double moveSpeed) {
        super(android, moveSpeed);
    }

    @Override
    public String getName() {
        return "miningBlock";
    }

    @Override
    public boolean shouldTick() {
        return !this.android.getWorld().isAir(getPos());
    }

    @Override
    public void tick() {
        BlockPos pos = getPos();
        this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());

        if (isInRange(1)) {
            this.android.swingHand(Hand.MAIN_HAND);
            this.android.brain.getModules().miningModule.mine(pos);
        }
        else
            super.tick();
    }
}
