package com.thunderbear06.entity.AI.tasks;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;

public abstract class AndroidTask {

    protected BaseAndroidEntity android;
    protected AndroidBrain brain;

    public final String statusName;

    private boolean isCancelled = false;

    public AndroidTask(BaseAndroidEntity androidEntity, AndroidBrain brain, String statusName) {
        this.android = androidEntity;
        this.brain = brain;
        this.statusName = statusName;
    }

    public abstract void startTask();

    public abstract void tickTask();

    public abstract void stopTask();

    public void cancel() { this.isCancelled = true; }

    public boolean taskCompleted() { return this.isCancelled; }

}
