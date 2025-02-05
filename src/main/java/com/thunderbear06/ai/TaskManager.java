package com.thunderbear06.ai;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<String, Task> tasks = new HashMap<>();
    private final Task idleTask;
    private Task currentTask = null;

    public TaskManager(Task idleTask) {
        this.idleTask = idleTask;
    }

    public void addTask(Task task) {
        this.tasks.put(task.name, task);
    }

    public void setCurrentTask(String taskName) {
        if (!tasks.containsKey(taskName)) {
            CCAndroids.LOGGER.error("Unrecognized task name {}", taskName);
            return;
        }

        clearCurrentTask();

        this.currentTask = this.tasks.get(taskName);
        this.currentTask.firstTick();
    }

    public void clearCurrentTask() {
        this.currentTask.lastTick();
        this.currentTask = null;
    }

    public void tick() {
        if (this.currentTask == null) {
            this.idleTask.tick();
            return;
        }

        if (this.currentTask.shouldTick()) {
            this.currentTask.tick();
            return;
        }

        clearCurrentTask();
    }
}
