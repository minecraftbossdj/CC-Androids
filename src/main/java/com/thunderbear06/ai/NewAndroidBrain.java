package com.thunderbear06.ai;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.modules.AModules;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

public class NewAndroidBrain {
    protected final BaseAndroidEntity android;
    protected final AndroidTargets targeting;
    protected final AModules modules;

    protected String state = "idle";

    @Deprecated
    public AndroidPlayer fakePlayer;
    private GameProfile owningPlayerProfile;

    public NewAndroidBrain(BaseAndroidEntity android) {
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

    public void setState(String newState) {
        CCAndroids.LOGGER.info("Set android state to {}", newState);
        this.state = newState;
    }

    public String getState() {
        return this.state;
    }

    public boolean isInState(String state) {
        return this.state.equals(state);
    }

    public BaseAndroidEntity getAndroid() {
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
