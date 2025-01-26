package com.thunderbear06.computer;

import com.thunderbear06.entity.AI.modules.SensorModule;
import com.thunderbear06.entity.BaseAndroidEntity;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@ApiStatus.NonExtendable
public interface AndroidAccess {

    World getWorld();
    BaseAndroidEntity getAndroid();

    SensorModule getSensorModule();

    void sendChatMessage(String rawMessage);

    boolean setTargetBlock(BlockPos pos);

    boolean setTargetEntity(UUID entityUUID);

    void setState(String state);

    @Nullable LivingEntity getTargetEntity();

    @Nullable BlockPos getTargetBlock();

    PlayerEntity getClosestPlayer();

    LivingEntity getClosestMobOfType(String type) throws LuaException;

    List<String> getNearbyMobs(@Nullable String type) throws LuaException;

    String getState();

    BlockPos getPosition();
}
