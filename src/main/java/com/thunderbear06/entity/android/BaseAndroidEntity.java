package com.thunderbear06.entity.android;

import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.computer.AndroidComputerContainer;
import com.thunderbear06.computer.EntityComputer;
import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.util.ComponentMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class BaseAndroidEntity extends PathAwareEntity {
    public final AndroidBrain brain;
    public final AndroidComputerContainer computerContainer;
    public final DefaultedList<ItemStack> internalStorage;

    protected BaseAndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);

        this.brain = new AndroidBrain(this);
        this.internalStorage = DefaultedList.ofSize(9, ItemStack.EMPTY);
        computerContainer = new AndroidComputerContainer(this);
    }

    public static DefaultAttributeContainer.Builder createAndroidAttributes() {
        return createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0);
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

        if (!this.getWorld().isClient()) {
            this.computerContainer.onTick();
        }
    }

    // Action

    private void handleDoor(PathNode node, boolean open) {
        BlockPos pos = new BlockPos(node.x, node.y, node.z);
        BlockState state = this.getWorld().getBlockState(pos);

        if (state.isIn(BlockTags.WOODEN_DOORS)) {
            DoorBlock door = (DoorBlock) state.getBlock();
            door.setOpen(this, this.getWorld(), state, pos, open);
        }
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

    public void jumpAccess() {
        this.jump();
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
        if (this.computerContainer.getComputerID() >= 0)
            this.dropCPU();

        for (ItemStack stack : this.internalStorage) {
            this.dropStack(stack);
        }
    }

    private void dropCPU() {
        ItemStack stack = new ItemStack(ItemRegistry.ANDROID_CPU, 1);

        NbtCompound compound = new NbtCompound();
        NbtCompound computerCompound = new NbtCompound();

        computerCompound.putInt("ComputerID", this.computerContainer.getComputerID());
        computerCompound.putString("ComputerFamily", this.computerContainer.getFamily().toString());

        compound.put("Computer", computerCompound);

        stack.setNbt(compound);

        this.dropStack(stack);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
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
        ItemStack storedStack = this.internalStorage.get(index);

        if (storedStack.isEmpty()) {
            this.internalStorage.set(index, stack);
            return ItemStack.EMPTY;
        } else if (storedStack.isOf(stack.getItem())) {
            int space = storedStack.getMaxCount() - storedStack.getCount();
            int transfer = Math.min(stack.getCount(), space);

            this.internalStorage.set(index, stack.copyWithCount(transfer));

            stack.setCount(stack.getCount() - transfer);
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }
    public ItemStack getStashItem(int index, boolean remove) {
        if (index < 0 || index >= this.internalStorage.size())
            return null;
        ItemStack storedStack = this.internalStorage.get(index);

        if (remove)
            this.internalStorage.set(index, ItemStack.EMPTY);

        return storedStack;
    }

    public @Nullable MethodResult canStash(ItemStack itemStack, int index) {
        if (index < 0 || index >= this.internalStorage.size())
            return MethodResult.of(String.format("Index out of bounds! Must be between 1 and %d", index));

        ItemStack storedStack = this.internalStorage.get(index);

        if (!storedStack.isEmpty() && !ItemStack.canCombine(storedStack, itemStack))
            return MethodResult.of("Index is occupied by another item stack!");

        return null;
    }

    // Misc

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.internalStorage);

        NbtCompound computerCompound = new NbtCompound();

        this.computerContainer.writeNbt(computerCompound);
        nbt.put("ComputerContainer", computerCompound);

        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.internalStorage);

        if (nbt.contains("ComputerEntity")) {
            NbtCompound computerCompound = nbt.getCompound("ComputerContainer");

            this.computerContainer.readNbt(computerCompound);
            this.brain.readNbt(computerCompound);
        }

        super.readCustomDataFromNbt(nbt);
    }

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
}
