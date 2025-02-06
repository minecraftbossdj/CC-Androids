package com.thunderbear06.ai;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<String, Task> tasks = new HashMap<>();
    private Task currentTask = null;

    public void addTask(Task task) {
        this.tasks.put(task.getName(), task);
    }

    public void setCurrentTask(String taskName) {
        if (!tasks.containsKey(taskName)) {
            CCAndroids.LOGGER.error("Unrecognized task name {}", taskName);
            return;
        }

        if (this.currentTask != null)
            clearCurrentTask();

        this.currentTask = this.tasks.get(taskName);
        this.currentTask.firstTick();
    }

    public String getCurrentTaskName() {
        return this.currentTask == null ? "idle" : this.currentTask.getName();
    }

    public void clearCurrentTask() {
        this.currentTask.lastTick();
        this.currentTask = null;
    }

    public void tick() {
        if (this.currentTask == null) {
            return;
        }

        if (this.currentTask.shouldTick()) {
            this.currentTask.tick();
            return;
        }

        clearCurrentTask();
    }
}
