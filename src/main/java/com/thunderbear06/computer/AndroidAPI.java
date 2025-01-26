package com.thunderbear06.computer;

import com.thunderbear06.entity.AI.tasks.AttackEntityTask;
import com.thunderbear06.entity.AI.tasks.WalkToEntityTask;
import com.thunderbear06.entity.BaseAndroidEntity;
import dan200.computercraft.api.lua.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AndroidAPI implements ILuaAPI {
    private final AndroidAccess android;

    public AndroidAPI(AndroidAccess android) {
        this.android = android;
    }

    @Override
    public String[] getNames() {
        return new String[]{"android"};
    }

    @Override
    public @Nullable String getModuleName() {
        return "android";
    }

    /*
    * Information
    */

    @LuaFunction
    public final MethodResult getPosition() {
        BlockPos blockPos = android.GetPosition();
        Map<String, Integer> posMap = new HashMap<>();
        posMap.put("x", blockPos.getX());
        posMap.put("y", blockPos.getY());
        posMap.put("z", blockPos.getZ());

        return MethodResult.of(posMap);
    }

    @LuaFunction
    public final MethodResult getState() {
        return MethodResult.of(this.android.GetState());
    }

    /*
    * Task
    */

    @LuaFunction
    public final MethodResult pushTasks() {
        return this.android.GetTaskModule().pushQueuedTasks();
    }

    @LuaFunction
    public final MethodResult isIdle() {
        return MethodResult.of(this.android.GetTaskModule().tasksComplete());
    }

    /*
    * Action
    */

    @LuaFunction
    public final MethodResult addWalkToEntityTask(String entityUUID) {
        BaseAndroidEntity androidEntity = this.android.GetAndroid();
        WalkToEntityTask task = new WalkToEntityTask(androidEntity, androidEntity.brain, 0.5, UUID.fromString(entityUUID));
        if (!task.targetValid())
            return MethodResult.of("Unknown or invalid entity UUID");
        return this.android.GetTaskModule().queueTask(task);
    }

    /*
    * Combat
    */

    @LuaFunction
    public final MethodResult addAttackTask(String entityUUID) {
        BaseAndroidEntity androidEntity = this.android.GetAndroid();
        AttackEntityTask task = new AttackEntityTask(androidEntity, androidEntity.brain, UUID.fromString(entityUUID), false);
        if (!task.targetValid())
            return MethodResult.of("Unknown or invalid entity UUID");
        return this.android.GetTaskModule().queueTask(task);
    }

    //TODO: Revamp

    @LuaFunction
    public final MethodResult getClosestPlayer() {
        PlayerEntity player = android.GetClosestPlayer();

        return MethodResult.of(player == null ? null : player.getUuidAsString());
    }

    @LuaFunction
    public final MethodResult getNearbyMobs(Optional<String> type) throws LuaException {
        return MethodResult.of(android.GetNearbyMobs(type.orElse(null)));
    }

    @LuaFunction
    public final MethodResult getClosestMobOfType(Optional<String> type) throws LuaException {
        LivingEntity entity = android.GetClosestMobOfType(type.orElse(null));

        return MethodResult.of(entity == null ? null : entity.getUuidAsString());
    }

    @LuaFunction
    public final MethodResult getMobInfo( String entityUUIDString) {
        ServerWorld world = (ServerWorld) android.GetWorld();

        LivingEntity entity = (LivingEntity) world.getEntity(UUID.fromString(entityUUIDString));

        if (entity == null)
            return MethodResult.of("Entity does not exist");

        HashMap<String, Object> infoMap = new HashMap<>();

        infoMap.put("name", Objects.requireNonNullElse(entity.getCustomName(), entity.getName()).getString());
        infoMap.put("health", entity.getHealth());
        infoMap.put("isHostile", entity instanceof HostileEntity);
        infoMap.put("posX", entity.getX());
        infoMap.put("posY", entity.getY());
        infoMap.put("posZ", entity.getZ());

        return MethodResult.of(infoMap);
    }

//    @LuaFunction
//    public final MethodResult followTarget(String entityUUIDString) {
//        return MethodResult.of(android.FollowEntity(entityUUIDString));
//    }
//
//    @LuaFunction
//    public final MethodResult attackTarget(String entityUUIDString) {
//        return MethodResult.of(android.AttackEntity(entityUUIDString));
//    }

    @LuaFunction
    public final MethodResult sendChatMessage(String what) {
        android.SendChatMessage(what);
        return MethodResult.of();
    }
}
