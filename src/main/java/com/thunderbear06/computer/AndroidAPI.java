package com.thunderbear06.computer;

import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        BlockPos blockPos = android.getPosition();
        Map<String, Integer> posMap = new HashMap<>();
        posMap.put("x", blockPos.getX());
        posMap.put("y", blockPos.getY());
        posMap.put("z", blockPos.getZ());

        return MethodResult.of(posMap);
    }

    @LuaFunction
    public final MethodResult getState() {
        return MethodResult.of(this.android.getState());
    }

    /*
    * Action
    */

    @LuaFunction
    public final MethodResult attack(String entityUUID) {
        if (!this.android.setTargetEntity(UUID.fromString(entityUUID))) {
            return MethodResult.of("Unknown entity or invalid UUID");
        }

        this.android.setState("attacking");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult follow(String entityUUID) {
        if (!this.android.setTargetEntity(UUID.fromString(entityUUID))) {
            return MethodResult.of("Unknown entity or invalid UUID");
        }

        this.android.setState("following");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult moveTo(int x, int y, int z) {
        this.android.setTargetBlock(new BlockPos(x,y,z));
        this.android.setState("movingToBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult mineBlock(int x, int y, int z) {
        this.android.setTargetBlock(new BlockPos(x,y,z));
        this.android.setState("miningBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult pickup(String entityUUID) {
        ServerWorld world = (ServerWorld) this.android.getWorld();

        ItemEntity itemEntity = (ItemEntity) world.getEntity(UUID.fromString(entityUUID));

        if (itemEntity == null)
            return MethodResult.of("Unknown item or invalid UUID");

        return this.android.getOwner().pickupGroundItem(itemEntity);
    }

    @LuaFunction
    public final MethodResult dropHeldItem() {
        return this.android.getOwner().dropHandItem();
    }

    /*
    * Sensory
    */

    @LuaFunction
    public final MethodResult getClosestPlayer() {
        PlayerEntity player = this.android.getClosestPlayer();

        return MethodResult.of(player == null ? null : player.getUuidAsString());
    }

    @LuaFunction
    public final MethodResult getNearbyMobs(Optional<String> type) throws LuaException {
        return MethodResult.of(this.android.getNearbyMobs(type.orElse(null)));
    }

    @LuaFunction
    public final MethodResult getClosestMobOfType(Optional<String> type) throws LuaException {
        LivingEntity entity = this.android.getClosestMobOfType(type.orElse(null));

        return MethodResult.of(entity == null ? null : entity.getUuidAsString());
    }

    @LuaFunction
    public final MethodResult getGroundItems(Optional<String> targetItemName, Optional<Integer> max) {
        return MethodResult.of(this.android.getSensorModule().getGroundItems(targetItemName.orElse(null), max.orElse(Integer.MAX_VALUE), this.android.getOwner()));
    }

    //TODO: Revamp

    @LuaFunction
    public final MethodResult getMobInfo(String entityUUIDString) {
        ServerWorld world = (ServerWorld) android.getWorld();

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

    @LuaFunction
    public final MethodResult sendChatMessage(String what) {
        this.android.sendChatMessage(what);
        return MethodResult.of();
    }
}
