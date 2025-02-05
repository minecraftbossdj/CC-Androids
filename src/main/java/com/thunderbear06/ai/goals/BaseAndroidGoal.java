package com.thunderbear06.ai.goals;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public abstract class BaseAndroidGoal extends Goal {
    protected final BaseAndroidEntity android;
    protected final NewAndroidBrain brain;

    public BaseAndroidGoal(BaseAndroidEntity android, NewAndroidBrain brain) {
        this.android = android;
        this.brain = brain;
    }

    @Override
    public boolean canStart() {
        return this.android.getComputer().on;
    }

    @Override
    public void stop() {
        this.brain.setState("idle");
        this.brain.getTargeting().clearTargets();
    }

    protected boolean isInRangeOf(LivingEntity entity, boolean forMelee) {
        return forMelee ? this.android.isInAttackRange(entity) : this.android.squaredDistanceTo(entity) < 5;
    }

    protected boolean isInRangeOf(BlockPos pos) {
        return this.android.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= 2;
    }
}
