package com.thunderbear06.entity;

import com.thunderbear06.computer.ComputerComponents;
import com.thunderbear06.computer.EntityComputer;
import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.player.AndroidPlayer;
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
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
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

public class BaseAndroidEntity extends PathAwareEntity implements NamedScreenHandlerFactory {
    public final AndroidBrain brain;

    @Nullable
    public String label;
    public boolean on = false;
    public final DefaultedList<ItemStack> internalStorage;

    @Nullable
    private UUID instanceID = null;
    private int computerID = -1;
    private boolean startOn = false;
    private boolean fresh = false;
    private final ComputerFamily family;

    protected BaseAndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.family = ComputerFamily.NORMAL;
        this.brain = new AndroidBrain(this);
        this.internalStorage = DefaultedList.ofSize(9, ItemStack.EMPTY);
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
            if (this.computerID >= 0 || this.startOn) {
                ServerComputer computer = this.createServerComputer();

                if (this.startOn || this.fresh && this.on) {
                    computer.turnOn();
                    this.startOn = false;
                }

                computer.keepAlive();
                this.fresh = false;
                this.computerID = computer.getID();
                boolean newOn = computer.isOn();
                if (this.on != newOn) {
                    this.on = newOn;
                }

                if (!Objects.equals(this.label, computer.getLabel())) {
                    this.label = computer.getLabel();

                    this.setCustomName(Text.of(this.label));
                    this.setCustomNameVisible(this.label != null && !this.label.isEmpty());
                }
            }
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

    // Computer

    public final ServerComputer createServerComputer() {
        MinecraftServer server = this.getWorld().getServer();
        if (server == null) {
            throw new IllegalStateException("Cannot access server computer on the client.");
        } else {
            EntityComputer computer = (EntityComputer) ServerContext.get(server).registry().get(this.instanceID);
            if (computer == null) {
                if (this.computerID < 0) {
                    this.computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(server, "computer");
                }

                computer = this.createComputer(this.computerID);
                this.instanceID = computer.register();
                this.fresh = true;
            }

            return computer;
        }
    }

    public ComputerFamily getFamily() {
        return this.family;
    }

    protected EntityComputer createComputer(int id) {
        return new EntityComputer((ServerWorld)this.getWorld(), this, id, this.label, this.getFamily(), Config.computerTermWidth, Config.computerTermHeight, ComponentMap.builder().add(ComputerComponents.ANDROID_COMPUTER, this.brain).build());
    }

    @Override
    public @Nullable ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ComputerMenuWithoutInventory(ModRegistry.Menus.COMPUTER.get(), id, inventory, player1 -> true, this.createServerComputer());
    }

    @Nullable
    public ServerComputer getServerComputer() {
        return !this.getWorld().isClient && this.getWorld().getServer() != null ? ServerContext.get(this.getWorld().getServer()).registry().get(this.instanceID) : null;
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
        nbt.putInt("ComputerID", this.computerID);
        this.brain.writeNbt(nbt);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.internalStorage);
        if (nbt.contains("ComputerID"))
            this.computerID = nbt.getInt("ComputerID");
        this.brain.readNbt(nbt);
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

        ServerComputer computer = getServerComputer();

        if (computer != null)
            computer.close();
    }
}
