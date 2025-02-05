package com.thunderbear06.ai.tasks;

import com.thunderbear06.entity.android.AndroidEntity;

public abstract class Task {
    protected final AndroidEntity android;

    public String name = null;

    public Task(AndroidEntity android) {
        this.android = android;
    }

    public boolean shouldTick() {
        return false;
    }

    public abstract void firstTick();
    public abstract void tick();
    public abstract void lastTick();
}
