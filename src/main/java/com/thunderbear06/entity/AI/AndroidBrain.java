package com.thunderbear06.entity.AI;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.computer.IAndroidAccess;
import com.thunderbear06.entity.AI.modules.InteractionModule;
import com.thunderbear06.entity.AI.modules.MiningModule;
import com.thunderbear06.entity.AI.modules.SensorModule;
import com.thunderbear06.entity.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class AndroidBrain implements IAndroidAccess {
    public final BaseAndroidEntity owner;
    public final SensorModule sensor;
    public final MiningModule miningModule;
    public final InteractionModule interactionModule;

    @Nullable
    public GameProfile owningPlayer;

    public AndroidPlayer fakePlayer;

    @Nullable
    public LivingEntity targetEntity;
    @Nullable
    public BlockPos targetBlock;

    public String state = "idle";

    private long lastChatMessageTime = 0;

    public AndroidBrain(BaseAndroidEntity android) {
        this.owner = android;
        this.sensor = new SensorModule(this.owner, this, 10);
        this.miningModule = new MiningModule(this.owner, this);
        this.interactionModule = new InteractionModule(this.owner, this);
        if (android.getWorld() instanceof ServerWorld)
            this.fakePlayer = AndroidPlayer.get(this);
    }

    @Override
    public World getWorld() {
        return this.owner.getWorld();
    }

    @Override
    public BaseAndroidEntity getOwner() {
        return this.owner;
    }

    @Override
    public @Nullable GameProfile getOwningPlayer() {
        return this.owningPlayer;
    }

    @Override
    public void setOwningPlayer(@NotNull GameProfile player) {
        this.owningPlayer = player;
    }

    @Override
    public SensorModule getSensorModule() {
        return this.sensor;
    }

    @Override
    public void sendChatMessage(String rawMessage) {
        ServerWorld world = (ServerWorld) this.owner.getWorld();

        long time = this.owner.getWorld().getTime();

        if (time - this.lastChatMessageTime < 20)
            return;

        this.lastChatMessageTime = this.owner.getWorld().getTime();

        world.getPlayers().forEach(serverPlayerEntity -> {
            if (this.owner.getPos().distanceTo(serverPlayerEntity.getPos()) < 50)
                serverPlayerEntity.sendChatMessage(SentMessage.of(SignedMessage.ofUnsigned(rawMessage)), false, MessageType.params(MessageType.CHAT, this.owner));
        });
    }

    @Override
    public boolean setTargetBlock(BlockPos pos) {
        if (!this.owner.getWorld().isInBuildLimit(pos))
            return false;
        this.targetBlock = pos;
        return true;
    }

    @Override
    public boolean setTargetEntity(UUID entityUUID) {
        ServerWorld world = (ServerWorld) this.owner.getWorld();

        this.targetEntity = (LivingEntity) world.getEntity(entityUUID);

        return this.targetEntity != null && !this.targetEntity.isDead();
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public @Nullable LivingEntity getTargetEntity() {
        return this.targetEntity;
    }

    @Override
    public @Nullable BlockPos getTargetBlock() {
        return this.targetBlock;
    }

    @Override
    public PlayerEntity getClosestPlayer() {
        return this.sensor.getClosestPlayer();
    }

    @Override
    public LivingEntity getClosestMobOfType(String type) throws LuaException {
        return this.sensor.getClosestMobOfType(type);
    }

    @Override
    public List<String> getNearbyMobs(@Nullable String type) throws LuaException {
        return this.sensor.getMobs(type);
    }

    @Override
    public String getState() {
        return this.state;
    }

    @Override
    public BlockPos getPosition() {
        return this.owner.getBlockPos();
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        if (this.getOwningPlayer() != null) {
            NbtCompound owner = new NbtCompound();
            owner.putString("Name", this.getOwningPlayer().getName());
            owner.putUuid("UUID", this.getOwningPlayer().getId());
            nbt.put("OwningPlayer", owner);

        }
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("OwningPlayer")) {
            NbtCompound owner = nbt.getCompound("OwningPlayer");
            this.owningPlayer = new GameProfile(NbtHelper.toUuid(owner.get("UUID")), owner.getString("Name"));
        }
    }
}
