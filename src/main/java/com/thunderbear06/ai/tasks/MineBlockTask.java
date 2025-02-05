package com.thunderbear06.ai.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.math.BlockPos;

public class MineBlockTask extends MoveToBlockTask{
    public MineBlockTask(AndroidEntity android, double moveSpeed) {
        super(android, moveSpeed);
    }

    @Override
    public boolean shouldTick() {
        return !this.android.getWorld().isAir(getPos());
    }

    @Override
    public void tick() {
        BlockPos pos = getPos();
        this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());

        if (isInRange(2))
            this.android.brain.getModules().miningModule.mine(pos);
        else
            super.tick();
    }
}
