package com.thunderbear06.ai;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.modules.AModules;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

public class NewAndroidBrain {
    protected final AndroidEntity android;
    protected final AndroidTargets targeting;
    protected final AModules modules;

    //protected String state = "idle";

    @Deprecated
    public AndroidPlayer fakePlayer;
    private GameProfile owningPlayerProfile;

    public NewAndroidBrain(AndroidEntity android) {
        this.android = android;
        this.targeting = new AndroidTargets();
        this.modules = new AModules(android, this);

        if (android.getWorld() instanceof ServerWorld) {
            this.fakePlayer = AndroidPlayer.get(this);
        } else {
            this.fakePlayer = null;
        }
    }

    public void onShutdown() {
        this.targeting.clearTargets();
        setState("idle");
    }

    public void setTask(String taskName) {
        CCAndroids.LOGGER.info("Set current android task to {}", taskName);
        this.android.getTaskManager().setCurrentTask(taskName);
    }

    public String getCurrentTask() {
        return this.android.getTaskManager().getCurrentTaskName();
    }

    public void setState(String newState) {
        //this.state = newState;
    }

    public String getState() {
        return "idle"; // this.android.getTaskManager().getCurrentTaskName();
    }

    public boolean isInState(String state) {
        return false; //this.state.equals(state);
    }

    public AndroidEntity getAndroid() {
        return this.android;
    }

    public AndroidTargets getTargeting() {
        return this.targeting;
    }

    public AModules getModules() {
        return this.modules;
    }

    public GameProfile getOwningPlayerProfile() {
        return this.owningPlayerProfile;
    }

    public void setOwningPlayer(GameProfile gameProfile) {
        this.owningPlayerProfile = gameProfile;
    }

    public void writeNbt(NbtCompound computerCompound) {
        if (this.owningPlayerProfile == null)
            return;

        computerCompound.putUuid("OwningPlayerUUID", this.owningPlayerProfile.getId());
        computerCompound.putString("OwningPlayerName", this.owningPlayerProfile.getName());
    }

    public void readNbt(NbtCompound computerCompound) {
        if (!computerCompound.contains("OwningPlayerUUID"))
            return;

        this.owningPlayerProfile = new GameProfile(computerCompound.getUuid("OwningPlayerUUID"), computerCompound.getString("OwningPlayerName"));
    }
}
