package com.thunderbear06.ai.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class MoveToEntityTask extends EntityBasedTask{
    private final double moveSpeed;

    public MoveToEntityTask(AndroidEntity android, double moveSpeed) {
        super(android);
        this.moveSpeed = moveSpeed;
    }

    @Override
    public boolean shouldTick() {
        return super.shouldTick() && !isInRange(1);
    }

    @Override
    public void firstTick() {}

    @Override
    public void tick() {
        super.tick();

        if (this.android.getNavigation().isIdle()) {
            BlockPos entityPos = getTarget().getBlockPos();
            this.android.getNavigation().startMovingTo(entityPos.getX(), entityPos.getY(), entityPos.getZ(), this.moveSpeed);
        }
    }

    @Override
    public void lastTick() {}
}
