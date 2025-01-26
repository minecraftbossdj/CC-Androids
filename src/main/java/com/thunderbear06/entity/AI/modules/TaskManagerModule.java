package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.AI.tasks.AndroidTask;
import dan200.computercraft.api.lua.MethodResult;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerModule {

    public final int maxTasks = 5;

    public final List<AndroidTask> queuedTasks = new ArrayList<>();

    public final List<AndroidTask> currentTasks = new ArrayList<>();

    private final long taskTimeout;

    private AndroidTask currentTask;
    private long taskStartTime;

    public TaskManagerModule(long taskTimeout) {
        this.taskTimeout = taskTimeout;
    }

    public String getStatus() {
        return this.tasksComplete() || this.currentTask == null ? "idle" : this.currentTask.statusName;
    }

    public MethodResult queueTask(AndroidTask androidTask) {
        if (this.queuedTasks.size() >= this.maxTasks)
            return MethodResult.of("Maximum task queue size -10- reached. Please call pushTasks() before queueing anymore tasks");

        CCAndroids.LOGGER.info("Queued new task \"{}\"", androidTask.getClass().getName());
        this.queuedTasks.add(androidTask);
        return MethodResult.of();
    }

    public MethodResult pushQueuedTasks() {
        if (this.queuedTasks.isEmpty())
            return MethodResult.of("Task queue is empty!");

        if (!this.currentTasks.isEmpty())
            clearTasks();
        else if (this.currentTask != null)
            clearCurrentTask("queued tasks are being pushed");
        this.currentTasks.addAll(this.queuedTasks);
        this.queuedTasks.clear();
        return MethodResult.of();
    }

    public void clearCurrentTask(String reason) {
        CCAndroids.LOGGER.info("Cleared task because {}", reason);
        this.currentTask.stopTask();
        this.currentTask = null;
    }

    public void clearTasks() {
        if (this.currentTask != null)
            clearCurrentTask("clearing all tasks");
        this.currentTasks.clear();
    }

    public void tick(long gameTime) {
        if (this.currentTask == null) {
            if (this.currentTasks.isEmpty()) {
                return;
            }
            this.currentTask = this.currentTasks.get(0);
            this.currentTasks.remove(0);
            this.currentTask.startTask();
            this.taskStartTime = gameTime;
        } else if (this.currentTask.taskCompleted()) {
            clearCurrentTask("task reached completion or was cancelled");
        } else if (gameTime - this.taskStartTime > this.taskTimeout) {
            clearCurrentTask("task timed out");
        } else {
            this.currentTask.tickTask();
        }
    }

    public boolean tasksComplete() {
        return this.currentTask == null && this.currentTasks.isEmpty();
    }
}
