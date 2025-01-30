package com.thunderbear06.computer;

import com.thunderbear06.entity.AI.AndroidBrain;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AndroidAPI implements ILuaAPI {
    private final IAndroidAccess android;

    public AndroidAPI(IAndroidAccess android) {
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
        if (!this.android.setTargetBlock(new BlockPos(x,y,z))) {
            return MethodResult.of("Block position must be within world build limit");
        }

        this.android.setState("movingToBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult mineBlock(int x, int y, int z) {
        if (!this.android.setTargetBlock(new BlockPos(x,y,z))) {
            return MethodResult.of("Block position must be within world build limit");
        }

        this.android.setState("miningBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult useItemOnBlock(int x, int y, int z) {
        if (!this.android.setTargetBlock(new BlockPos(x,y,z))) {
            return MethodResult.of("Block position must be within world build limit");
        }

        this.android.setState("usingItemOnBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult useItemOnEntity(String entityUUID) {
        if (!this.android.setTargetEntity(UUID.fromString(entityUUID))) {
            return MethodResult.of("Unknown entity or invalid UUID");
        }

        this.android.setState("usingItemOnEntity");
        return MethodResult.of();
    }

    /*
    * Inventory
    */

    @LuaFunction
    public final MethodResult pickup(String entityUUID) {
        ServerWorld world = (ServerWorld) this.android.getWorld();

        ItemEntity itemEntity = (ItemEntity) world.getEntity(UUID.fromString(entityUUID));

        if (itemEntity == null)
            return MethodResult.of("Unknown item or invalid UUID");

        if (this.android.getOwner().distanceTo(itemEntity) > 5)
            return MethodResult.of("Item is too far to pick up");

        if (!this.android.getOwner().getMainHandStack().isEmpty())
            return MethodResult.of("Cannot pickup item without an empty hand");

        return this.android.getOwner().pickupGroundItem(itemEntity);
    }

    @LuaFunction
    public final MethodResult dropHeldItem() {
        return this.android.getOwner().dropHandItem();
    }

    @LuaFunction
    public final MethodResult stashHeldItem(int index) {
        // Tables in lua start at index 1
        index--;

        ItemStack itemStack = this.android.getOwner().getMainHandStack();

        if (itemStack.isEmpty())
            return MethodResult.of("No item in hand to stash");

        MethodResult result = this.android.getOwner().canStash(itemStack, index);

        if (result != null)
            return result;

        itemStack = this.android.getOwner().stashStack(itemStack, index);
        this.android.getOwner().setStackInHand(Hand.MAIN_HAND, itemStack);

        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult equipFromStash(int index) {
        index--;

        ItemStack storedItemstack = this.android.getOwner().getStashItem(index, true);

        if (storedItemstack == null || storedItemstack.isEmpty())
            return MethodResult.of("Index of stash is empty");
        if (!this.android.getOwner().getMainHandStack().isEmpty())
            return MethodResult.of("Cannot equip item while holding an item");

        this.android.getOwner().setStackInHand(Hand.MAIN_HAND, storedItemstack);
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult getItemInStash(int index) {
        index--;

        ItemStack storedStack = this.android.getOwner().getStashItem(index, false);

        if (storedStack.isEmpty())
            return MethodResult.of("empty");
        return MethodResult.of(storedStack.getItem().getName().getString());
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
        AndroidBrain brain = (AndroidBrain) this.android;

        return MethodResult.of(brain.getSensorModule().getMobs(type.orElse(null)));
    }

    @LuaFunction
    public final MethodResult getClosestMobOfType(Optional<String> type) throws LuaException {
        AndroidBrain brain = (AndroidBrain) this.android;

        return MethodResult.of(brain.getSensorModule().getClosestMobOfType(type.orElse(null)));
    }

    @LuaFunction
    public final MethodResult getGroundItems(Optional<String> type, Optional<Integer> max) {
        AndroidBrain brain = (AndroidBrain) this.android;
        return MethodResult.of(brain.getSensorModule().getGroundItems(type.orElse(null), max.orElse(Integer.MAX_VALUE)));
    }

    @LuaFunction
    public final MethodResult getBlocksOfType(String type) {
        AndroidBrain brain = (AndroidBrain) this.android;

        return MethodResult.of(brain.getSensorModule().getBlocksOfType(this.android.getPosition(), this.android.getOwner().getEyePos(), this.android.getWorld(), type));
    }

    /*
    * Misc
    */

    @LuaFunction
    public final MethodResult sendChatMessage(String what) {
        this.android.sendChatMessage(what);
        return MethodResult.of();
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
}
