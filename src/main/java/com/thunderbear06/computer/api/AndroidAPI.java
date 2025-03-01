package com.thunderbear06.computer.api;

import com.thunderbear06.ai.AndroidBrain;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class AndroidAPI implements ILuaAPI {
    private final AndroidBrain brain;

    public AndroidAPI(AndroidBrain android) {
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


    public static MethodResult Result(boolean failure, String reason) {
        return MethodResult.of(failure, reason);
    }

    private boolean checkFuel() {
        return this.brain.getAndroid().hasFuel();
    }

    /*
    * Information
    */

    @LuaFunction
    public final MethodResult getState() {
        return MethodResult.of(this.brain.getAndroid().getTaskManager().getCurrentTaskName());
    }

    @LuaFunction
    public final MethodResult getSelf() {
        return MethodResult.of(this.brain.getModules().sensorModule.collectEntityInfo(this.brain.getAndroid()));
    }

    /*
    * Action
    */

    @LuaFunction
    public final MethodResult attack(String entityUUID) {
        if (!checkFuel())
            return Result(true, "No fuel!");

        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null)
            return Result(true, "Unknown entity or invalid UUID");

        this.brain.getTargeting().setEntityTarget(target);
        this.brain.setTask("attacking");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult goTo(String entityUUID) {
        if (!checkFuel())
            return Result(true, "No fuel!");

        return this.brain.getModules().navigationModule.MoveToEntity(entityUUID);
    }

    @LuaFunction
    public final MethodResult moveTo(int x, int y, int z) {
        if (!checkFuel())
            return Result(true, "No fuel!");

        return this.brain.getModules().navigationModule.MoveToBlock(new BlockPos(x,y,z));
    }

    @LuaFunction
    public final MethodResult breakBlock(int x, int y, int z) {
        if (!checkFuel())
            return Result(true, "No fuel!");

        BlockPos pos = new BlockPos(x,y,z);

        if (!pos.isWithinDistance(this.brain.getAndroid().getBlockPos(), 100))
            return Result(true, "Block position must be within a 100 block radius");

        if (!this.brain.getAndroid().getWorld().isInBuildLimit(pos))
            return Result(true, "Block position must be in world build limit");

        this.brain.getTargeting().setBlockTarget(pos);
        this.brain.setTask("breakingBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult useBlock(int x, int y, int z) {
        if (!checkFuel())
            return Result(true, "No fuel!");

        BlockPos pos = new BlockPos(x,y,z);
        if (!this.brain.getAndroid().getWorld().isInBuildLimit(pos))
            return Result(true, "Block position must be in world build limit");

        this.brain.getTargeting().setBlockTarget(pos);
        this.brain.setTask("usingBlock");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult useEntity(String entityUUID) {
        if (!checkFuel())
            return Result(true, "No fuel!");

        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null || target.isRemoved())
            return Result(true, "Unknown entity or invalid UUID");

        this.brain.getTargeting().setEntityTarget(target);
        this.brain.setTask("usingEntity");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult startUsingItem(String handName) {
        if (handName.equals("right") || handName.equals("main"))
            this.brain.getAndroid().setCurrentHand(Hand.MAIN_HAND);
        else if (handName.equals("left") || handName.equals("off"))
            this.brain.getAndroid().setCurrentHand(Hand.OFF_HAND);
        else
            return Result(true, "Invalid hand name");
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult stopUsingItem() {
        this.brain.getAndroid().clearActiveItem();
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
            return Result(true, "Unknown item or invalid UUID");

        if (this.brain.getAndroid().distanceTo(itemEntity) > 2)
            return Result(true, "Item is too far to pick up");

        if (!this.brain.getAndroid().getMainHandStack().isEmpty())
            return Result(true, "Cannot pickup item without an empty hand");

        return this.brain.getAndroid().pickupGroundItem(itemEntity);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult dropHeldItem() {
        return this.brain.getAndroid().dropHandItem();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult stashHeldItem(int index) {
        ItemStack itemStack = this.brain.getAndroid().getMainHandStack();

        if (itemStack.isEmpty())
            return Result(true, "No item in hand to stash");

        MethodResult result = this.brain.getAndroid().canStash(itemStack, index);

        if (result != null)
            return result;

        itemStack = this.brain.getAndroid().stashStack(itemStack, index);
        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, itemStack);

        return Result(false, "Stashed held item at index "+index);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult equipFromStash(int index) {
        ItemStack storedItemstack = this.brain.getAndroid().getStashItem(index, true);

        if (storedItemstack == null || storedItemstack.isEmpty())
            return MethodResult.of("Index of stash is empty");
        if (!this.brain.getAndroid().getMainHandStack().isEmpty())
            return MethodResult.of("Cannot equip item while holding an item");

        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, storedItemstack);
        return Result(false, "Equipped stack from index "+index);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult swapHandItems() {
        this.brain.getAndroid().swapOffHandStack();
        return MethodResult.of();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItemInStash(int index) {
        ItemStack storedStack = this.brain.getAndroid().getStashItem(index, false);

        if (storedStack == null || storedStack.isEmpty())
            return MethodResult.of("empty");
        return Result(false, storedStack.getItem().getName().getString());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult refuel(Optional<Integer> amt) {
        ItemStack heldStack = this.brain.getAndroid().getMainHandStack();

        if (heldStack.isEmpty())
            return Result(true, "Hand it empty");

        if (!this.brain.getAndroid().addFuel(amt.orElse(heldStack.getCount()), heldStack))
            return Result(true, "Held item stack cannot be used for fuel");

        this.brain.getAndroid().setStackInHand(Hand.MAIN_HAND, heldStack);

        return Result(false, "Fuel level increased to "+ brain.getAndroid().getFuel());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getFuelLevel() {
        return MethodResult.of(this.brain.getAndroid().getFuel());
    }

    /*
    * Sensory
    */

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestPlayer() {
        return MethodResult.of(this.brain.getModules().sensorModule.getClosestPlayer());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getNearbyMobs(Optional<String> type) {
        return MethodResult.of(brain.getModules().sensorModule.getMobs(type.orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestMob(Optional<String> type) {
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
}
