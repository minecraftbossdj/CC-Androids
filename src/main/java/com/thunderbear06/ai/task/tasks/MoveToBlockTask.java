package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.math.BlockPos;

public class MoveToBlockTask extends BlockBasedTask{
    private final double moveSpeed;

    public MoveToBlockTask(AndroidEntity android, double moveSpeed) {
        super(android);
        this.moveSpeed = moveSpeed;
    }

    @Override
    public String getName() {
        return "movingToBlock";
    }

    @Override
    public boolean shouldTick() {
        return !isInRange(1);
    }

    @Override
    public void firstTick() {}

    @Override
    public void tick() {
        if (!this.android.getNavigation().isIdle())
            return;

        BlockPos pos = getPos();
        this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), this.moveSpeed);
    }

    @Override
    public void lastTick() {}
}
