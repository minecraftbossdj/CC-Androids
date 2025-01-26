package com.thunderbear06.entity.AI.tasks;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;

import java.util.UUID;

public class WalkToEntityTask extends TargetedAndroidTask{
    private final double speed;

    public WalkToEntityTask(BaseAndroidEntity androidEntity, AndroidBrain brain, double speed, UUID entityUUID) {
        super(androidEntity, brain, entityUUID, "moving");
        this.speed = speed;
    }

    @Override
    public void startTask() {
        this.android.getNavigation().startMovingTo(this.targetEntity, this.speed);
    }

    @Override
    public void tickTask() {
        if (this.android.getNavigation().isIdle())
            this.android.getNavigation().startMovingTo(this.targetEntity, this.speed);
    }

    @Override
    public boolean taskCompleted() {
        return super.taskCompleted() || this.android.squaredDistanceTo(this.targetEntity) < 10;
    }

    @Override
    public void stopTask() {
        this.android.getNavigation().stop();
    }
}
