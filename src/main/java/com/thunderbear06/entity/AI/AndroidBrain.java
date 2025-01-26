package com.thunderbear06.entity.AI;

import com.thunderbear06.computer.AndroidAccess;
import com.thunderbear06.entity.AI.modules.SensorModule;
import com.thunderbear06.entity.BaseAndroidEntity;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class AndroidBrain implements AndroidAccess {
    protected final BaseAndroidEntity owner;
    protected final SensorModule sensor;

    @Nullable
    public LivingEntity targetEntity;
    @Nullable
    public BlockPos targetBlock;

    public String state = "idle";

    private long lastChatMessageTime = 0;

    public AndroidBrain(BaseAndroidEntity android) {
        this.owner = android;
        this.sensor = new SensorModule(10);
    }

    @Override
    public World getWorld() {
        return this.owner.getWorld();
    }

    @Override
    public BaseAndroidEntity getAndroid() {
        return this.owner;
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
        return this.sensor.getClosestPlayer(this.owner);
    }

    @Override
    public LivingEntity getClosestMobOfType(String type) throws LuaException {
        return this.sensor.getClosestMobOfType(type, this.owner);
    }

    @Override
    public List<String> getNearbyMobs(@Nullable String type) throws LuaException {
        return this.sensor.getMobs(type, this.owner);
    }

    @Override
    public String getState() {
        return this.state;
    }

    @Override
    public BlockPos getPosition() {
        return this.owner.getBlockPos();
    }

}
