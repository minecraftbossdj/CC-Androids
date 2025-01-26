package com.thunderbear06.computer;

import com.thunderbear06.entity.AI.modules.SensorModule;
import com.thunderbear06.entity.AI.modules.TaskManagerModule;
import com.thunderbear06.entity.BaseAndroidEntity;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.NonExtendable
public interface AndroidAccess {

    World GetWorld();
    BaseAndroidEntity GetAndroid();

    SensorModule GetSensorModule();

    TaskManagerModule GetTaskModule();

    void SendChatMessage(String rawMessage);

    PlayerEntity GetClosestPlayer();

    LivingEntity GetClosestMobOfType(String type) throws LuaException;

    List<String> GetNearbyMobs(@Nullable String type) throws LuaException;

    String GetState();

    BlockPos GetPosition();
}
