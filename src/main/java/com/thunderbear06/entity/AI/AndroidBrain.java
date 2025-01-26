package com.thunderbear06.entity.AI;

import com.thunderbear06.computer.AndroidAccess;
import com.thunderbear06.entity.AI.modules.SensorModule;
import com.thunderbear06.entity.AI.modules.TaskManagerModule;
import com.thunderbear06.entity.BaseAndroidEntity;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
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
    protected final TaskManagerModule taskManager;

    @Nullable
    public LivingEntity targetEntity;

    private long lastChatMessageTime = 0;

    public AndroidBrain(BaseAndroidEntity android) {
        this.owner = android;
        this.sensor = new SensorModule(10);
        this.taskManager = new TaskManagerModule(6000);
    }

    public void tickTasks() {
        this.taskManager.tick(this.owner.getWorld().getTime());
    }

    @Override
    public World GetWorld() {
        return this.owner.getWorld();
    }

    @Override
    public BaseAndroidEntity GetAndroid() {
        return this.owner;
    }

    @Override
    public SensorModule GetSensorModule() {
        return this.sensor;
    }

    @Override
    public TaskManagerModule GetTaskModule() {
        return this.taskManager;
    }

    @Override
    public void SendChatMessage(String rawMessage) {
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
    public PlayerEntity GetClosestPlayer() {
        return this.sensor.GetClosestPlayer(this.owner);
    }

    @Override
    public LivingEntity GetClosestMobOfType(String type) throws LuaException {
        return this.sensor.GetClosestMobOfType(type, this.owner);
    }

    @Override
    public List<String> GetNearbyMobs(@Nullable String type) throws LuaException {
        return this.sensor.GetMobs(type, this.owner);
    }

    @Override
    public String GetState() {
        return this.GetTaskModule().getStatus();
    }

    @Override
    public BlockPos GetPosition() {
        return this.owner.getBlockPos();
    }

}
