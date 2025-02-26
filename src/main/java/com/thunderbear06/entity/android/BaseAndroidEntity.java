package com.thunderbear06.entity.android;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.computer.AndroidComputerContainer;
import com.thunderbear06.inventory.AndroidInventory;
import com.thunderbear06.item.ItemRegistry;
import com.thunderbear06.tags.TagRegistry;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BaseAndroidEntity extends PathAwareEntity {
    public AndroidBrain brain;

    public final AndroidInventory inventory;

    protected final AndroidComputerContainer computerContainer;
    protected final int maxFuel = 10000;
    protected int fuel = 0;

    public boolean isOn = false;

    protected BaseAndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);

        this.inventory = new AndroidInventory(9);

        this.computerContainer = new AndroidComputerContainer(this);

    }

    public void shutdown() {
        this.isOn = false;

        this.brain.onShutdown();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        this.openNaNoor();
    }

    @Override
    public void tick() {
        super.tick();

        tickHandSwing();

        if (this.getWorld().isClient())
            return;

        this.computerContainer.onTick();

        if (this.age % 20 > 0)
            return;

        if (isIdle())
            updatePeripherals();
        else
            consumeFuel();
    }

    protected boolean isIdle() {
        return true;
    }

    private void updatePeripherals() {
        if (this.computerContainer.getComputerID() < 0 || !this.computerContainer.on)
            return;

        for (Direction direction : Direction.stream().toList()) {
            if (direction == Direction.UP)
                continue;

            IPeripheral peripheral = PeripheralLookup.get().find(this.getWorld(), this.getBlockPos().offset(direction), direction);

            this.computerContainer.setPeripheral(ComputerSide.valueOf(direction.getId()), peripheral);
        }
    }

    protected void consumeFuel() {
        if (this.fuel > 0)
            this.fuel--;
    }

    private int getFuelMultiplier(ItemStack stack) {
        if (stack.isIn(TagRegistry.MINOR_ANDROID_FUEL))
            return 10;
        if (stack.isIn(TagRegistry.MEDIUM_ANDROID_FUEL))
            return 80;
        if (stack.isIn(TagRegistry.MAJOR_ANDROID_FUEL))
            return 800;
        return 0;
    }

    public boolean addFuel(int min, ItemStack stack) {
        int mult = getFuelMultiplier(stack);

        if (mult <= 0)
            return false;

        int fuelAvailable = Math.min(min, stack.getCount());

        int fuelNeeded = this.maxFuel - this.fuel;

        int fuelUsed = Math.min(fuelAvailable, fuelNeeded);

        setFuel(Math.min(this.fuel + (fuelUsed * mult), this.maxFuel));
        stack.decrement(fuelUsed);

        return true;
    }

    public int getFuel() {
        return this.fuel;
    }

    public void setFuel(int newFuel) {
        this.fuel = newFuel;
    }

    public boolean hasFuel() {
        return this.fuel > 0;
    }

    public AndroidComputerContainer getComputer() {
        return this.computerContainer;
    }

    // Action

    public void sendChatMessage(String msg) {
        Text message = Text.literal(this.getName().getString() + ": " + msg);

        this.getWorld().getPlayers().forEach(player -> {
            if (this.getBlockPos().isWithinDistance(player.getBlockPos(), 50))
                player.sendMessage(message);
        });
    }

    private void openNaNoor() {
        MobNavigation nav = (MobNavigation) this.getNavigation();

        if (nav.isIdle() || nav.getCurrentPath() == null)
            return;
        PathNode node = nav.getCurrentPath().getCurrentNode();
        PathNode lastNode = nav.getCurrentPath().getLastNode();

        handleDoor(node, true);
        if (lastNode != null && lastNode.previous != null)
            handleDoor(lastNode.previous, false);
    }

    private void handleDoor(PathNode node, boolean open) {
        BlockPos pos = new BlockPos(node.x, node.y, node.z);
        BlockState state = this.getWorld().getBlockState(pos);

        if (state.isIn(BlockTags.WOODEN_DOORS)) {
            DoorBlock door = (DoorBlock) state.getBlock();
            door.setOpen(this, this.getWorld(), state, pos, open);
        }
    }

    // Inventory
    public MethodResult pickupGroundItem(ItemEntity itemEntity) {
        if (itemEntity.isRemoved())
            return MethodResult.of("Item does not exist");
        if (itemEntity.getStack().isEmpty())
            return MethodResult.of("Cannot pickup item. Item is broken (Contact mod author)");
        if (itemEntity.cannotPickup())
            return MethodResult.of("Unable to pickup item");

        this.loot(itemEntity);

        return MethodResult.of();
    }

    public MethodResult dropHandItem() {
        ItemStack itemStack = this.getMainHandStack();

        if (itemStack.isEmpty())
            return MethodResult.of("Hand is empty");

        this.dropStack(itemStack);
        this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        return MethodResult.of();
    }

    @Override
    protected void dropInventory() {
        this.dropCPU();
        this.dropStack(ItemRegistry.REDSTONE_REACTOR.getDefaultStack());

        for (ItemStack stack : this.inventory.clearToList()) {
            this.dropStack(stack);
        }
    }

    private void dropCPU() {
        boolean isCommand = this.computerContainer.getFamily() == ComputerFamily.COMMAND;

        ItemStack stack = new ItemStack(isCommand ? Items.COMMAND_BLOCK : ItemRegistry.ANDROID_CPU);

        if (this.computerContainer.getComputerID() >= 0) {
            NbtCompound compound = new NbtCompound();

            compound.putInt("ComputerID", this.computerContainer.getComputerID());

            stack.setNbt(compound);
        }

        this.dropStack(stack);
    }

    @Override
    public ItemStack tryEquip(ItemStack stack) {
        EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
        ItemStack itemStack = this.getEquippedStack(equipmentSlot);

        if (this.canPickupItem(stack)) {
            if (!itemStack.isEmpty()) {
                this.dropStack(itemStack);
            }

            this.equipLootStack(equipmentSlot, stack);
            return stack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack stashStack(ItemStack stack, int index) {
        ItemStack storedStack = this.inventory.getStack(index);

        if (storedStack.isEmpty()) {
            this.inventory.setStack(index, stack);
            return ItemStack.EMPTY;
        } else if (storedStack.isOf(stack.getItem())) {
            int space = storedStack.getMaxCount() - storedStack.getCount();
            int transfer = Math.min(stack.getCount(), space);

            this.inventory.setStack(index, stack.copyWithCount(transfer));

            stack.setCount(stack.getCount() - transfer);
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    public ItemStack getStashItem(int index, boolean remove) {
        if (index < 0 || index >= this.inventory.size()-1)
            return null;
        ItemStack storedStack = this.inventory.getStack(index);

        if (remove)
            this.inventory.setStack(index, ItemStack.EMPTY);

        return storedStack;
    }

    public @Nullable MethodResult canStash(ItemStack itemStack, int index) {
        if (index < 0 || index >= this.inventory.size())
            return MethodResult.of(String.format("Index out of bounds! Must be between 1 and %d", index));

        ItemStack storedStack = this.inventory.getStack(index);

        if (!storedStack.isEmpty() && !ItemStack.canCombine(storedStack, itemStack))
            return MethodResult.of("Index is occupied by another item stack!");

        return null;
    }

    // Misc

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.put("Items", this.inventory.toNbtList());

        nbt.putInt("Fuel", this.getFuel());

        NbtCompound computerCompound = new NbtCompound();

        this.computerContainer.writeNbt(computerCompound);
        this.brain.writeNbt(computerCompound);
        nbt.put("ComputerEntity", computerCompound);

        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.inventory.readNbtList(nbt.getList("Items", NbtElement.COMPOUND_TYPE));

        if (nbt.contains("Fuel"))
            setFuel(nbt.getInt("Fuel"));

        if (nbt.contains("ComputerEntity")) {
            NbtCompound computerCompound = nbt.getCompound("ComputerEntity");

            this.computerContainer.readNbt(computerCompound);
            this.brain.readNbt(computerCompound);
        }

        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.FALL))
            return false;
        if (source.isOf(DamageTypes.MAGIC))
            return false;

        return super.damage(source, amount);
    }

    @Override
    protected void onStatusEffectApplied(StatusEffectInstance effect, @org.jetbrains.annotations.Nullable Entity source) {}

    @Override
    protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @org.jetbrains.annotations.Nullable Entity source) {}

    // Robots don't drown now, do they?
    @Override
    public int getAir() {
        return 10;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);

        ServerComputer computer = this.computerContainer.getServerComputer();

        if (computer != null)
            computer.close();
    }

    public double getEntitySearchRadius() {
        return 10.0;
    }

    public int getBlockSearchRadius() {
        return 10;
    }
}
