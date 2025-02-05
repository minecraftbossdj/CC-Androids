package com.thunderbear06.computer.api;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.util.PathReachChecker;
import dan200.computercraft.api.lua.ILuaAPI;
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
    private final NewAndroidBrain brain;

    public AndroidAPI(NewAndroidBrain android) {
        this.brain = android;
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

    @LuaFunction(mainThread = true)
    public final MethodResult getPosition() {
        BlockPos blockPos = this.brain.getAndroid().getBlockPos();
        Map<String, Integer> posMap = new HashMap<>();
        posMap.put("x", blockPos.getX());
        posMap.put("y", blockPos.getY());
        posMap.put("z", blockPos.getZ());

        return MethodResult.of(posMap);
    }

    @LuaFunction
    public final MethodResult getState() {
        return MethodResult.of(this.brain.getState());
    }

    /*
    * Action
    */

    @LuaFunction
    public final MethodResult attack(String entityUUID) {
        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null)
            return MethodResult.of("Unknown entity or invalid UUID");

        this.brain.getTargeting().setEntityTarget(target);
        this.brain.setState("attacking");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult goTo(String entityUUID) {
        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null || target.isRemoved())
            return MethodResult.of("Unknown entity or invalid UUID");

        this.brain.getTargeting().setEntityTarget(target);
        this.brain.setState("following");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult moveTo(int x, int y, int z) {
        BlockPos pos = new BlockPos(x,y,z);
        if (!this.brain.getAndroid().getWorld().isInBuildLimit(pos))
            return MethodResult.of("Block position must be in world build limit");

        BlockPos closest = PathReachChecker.getClosestPosition(this.brain.getAndroid().getBlockPos(), pos, (ServerWorld) this.brain.getAndroid().getWorld());
        if (closest != null) {
            if (!closest.isWithinDistance(pos, 3))
                return MethodResult.of("Could not find path to a position within 3 blocks of target");
            pos = closest;
        }

        this.brain.getTargeting().setBlockTarget(pos);
        this.brain.setState("movingToBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult mineBlock(int x, int y, int z) {
        BlockPos pos = new BlockPos(x,y,z);
        if (!this.brain.getAndroid().getWorld().isInBuildLimit(pos))
            return MethodResult.of("Block position must be in world build limit");

//        BlockPos closest = PathReachChecker.getClosestPosition(this.brain.getAndroid().getBlockPos(), pos, (ServerWorld) this.brain.getAndroid().getWorld());
//        if (closest != null) {
//            if (!closest.isWithinDistance(pos, 2))
//                return MethodResult.of("Could not find path to a position within 3 blocks of target");
//        }

        this.brain.getTargeting().setBlockTarget(pos);
        this.brain.setState("miningBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult useItemOnBlock(int x, int y, int z) {
        BlockPos pos = new BlockPos(x,y,z);
        if (!this.brain.getAndroid().getWorld().isInBuildLimit(pos))
            return MethodResult.of("Block position must be in world build limit");

        BlockPos closest = PathReachChecker.getClosestPosition(this.brain.getAndroid().getBlockPos(), pos, (ServerWorld) this.brain.getAndroid().getWorld());
        if (closest != null) {
            if (!closest.isWithinDistance(pos, 2))
                return MethodResult.of("Could not find path to a position within 3 blocks of target");
        }

        this.brain.getTargeting().setBlockTarget(pos);
        this.brain.setState("usingItemOnBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult useItemOnEntity(String entityUUID) {
        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null || target.isRemoved())
            return MethodResult.of("Unknown entity or invalid UUID");

        this.brain.getTargeting().setEntityTarget(target);
        this.brain.setState("usingItemOnEntity");
        return MethodResult.of();
    }

    /*
    * Inventory
    */

    @LuaFunction(mainThread = true)
    public final MethodResult pickup(String entityUUID) {
        ServerWorld world = (ServerWorld) this.brain.getAndroid().getWorld();

        ItemEntity itemEntity = (ItemEntity) world.getEntity(UUID.fromString(entityUUID));

        if (itemEntity == null)
            return MethodResult.of("Unknown item or invalid UUID");

        if (this.brain.getAndroid().distanceTo(itemEntity) > 2)
            return MethodResult.of("Item is too far to pick up");

        if (!this.brain.getAndroid().getMainHandStack().isEmpty())
            return MethodResult.of("Cannot pickup item without an empty hand");

        return this.brain.getAndroid().pickupGroundItem(itemEntity);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult dropHeldItem() {
        return this.brain.getAndroid().dropHandItem();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult stashHeldItem(int index) {
        // Tables in lua start at index 1
        index--;

        ItemStack itemStack = this.brain.getAndroid().getMainHandStack();

        if (itemStack.isEmpty())
            return MethodResult.of("No item in hand to stash");

        MethodResult result = this.brain.getAndroid().canStash(itemStack, index);

        if (result != null)
            return result;

        itemStack = this.brain.getAndroid().stashStack(itemStack, index);
        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, itemStack);

        return MethodResult.of();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult equipFromStash(int index) {
        index--;

        ItemStack storedItemstack = this.brain.getAndroid().getStashItem(index, true);

        if (storedItemstack == null || storedItemstack.isEmpty())
            return MethodResult.of("Index of stash is empty");
        if (!this.brain.getAndroid().getMainHandStack().isEmpty())
            return MethodResult.of("Cannot equip item while holding an item");

        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, storedItemstack);
        return MethodResult.of();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItemInStash(int index) {
        index--;

        ItemStack storedStack = this.brain.getAndroid().getStashItem(index, false);

        if (storedStack.isEmpty())
            return MethodResult.of("empty");
        return MethodResult.of(storedStack.getItem().getName().getString());
    }

    /*
    * Sensory
    */

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestPlayer() {
        PlayerEntity player = this.brain.getAndroid().getWorld().getClosestPlayer(this.brain.getAndroid(), 100);

        return MethodResult.of(player == null ? null : player.getUuidAsString());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getNearbyMobs(Optional<String> type) {
        return MethodResult.of(brain.getModules().sensorModule.getMobs(type.orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestMobOfType(Optional<String> type) {
        return MethodResult.of(brain.getModules().sensorModule.getClosestMobOfType(type.orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getGroundItems(Optional<String> type, Optional<Integer> max) {
        return MethodResult.of(brain.getModules().sensorModule.getGroundItem(type.orElse(null), max.orElse(Integer.MAX_VALUE)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getBlocksOfType(String type) {
        BlockPos pos = this.brain.getAndroid().getBlockPos();
        ServerWorld world = (ServerWorld) this.brain.getAndroid().getWorld();

        return MethodResult.of(brain.getModules().sensorModule.getBlocksOfType(pos, this.brain.getAndroid().getEyePos(), world, type));
    }

    /*
    * Misc
    */

    @LuaFunction
    public final MethodResult sendChatMessage(String what) {
        this.brain.getAndroid().sendChatMessage(what);
        return MethodResult.of();
    }

    //TODO: Wish this could return NBT

    @LuaFunction(mainThread = true)
    public final MethodResult getMobInfo(String entityUUIDString) {
        ServerWorld world = (ServerWorld) brain.getAndroid().getWorld();

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
