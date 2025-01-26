package com.thunderbear06.entity;

import com.thunderbear06.computer.ComputerComponents;
import com.thunderbear06.computer.EntityComputer;
import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.AI.modules.TaskManagerModule;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.util.ComponentMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class BaseAndroidEntity extends PathAwareEntity implements NamedScreenHandlerFactory {

    @Nullable
    private UUID instanceID = null;
    private int computerID = -1;
    @Nullable String label;
    private boolean on = false;
    boolean startOn = false;
    private boolean fresh = false;

    private final ComputerFamily family;
    public final AndroidBrain brain;

    protected BaseAndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.family = ComputerFamily.NORMAL;
        this.brain = new AndroidBrain(this);
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

                TaskManagerModule taskManager = this.brain.GetTaskModule();

                if (computer.isOn()) {
                    if (taskManager.tasksComplete()) {
                        computer.queueEvent("tasksComplete");
                    }
                } else if (!taskManager.tasksComplete()) {
                    this.brain.GetTaskModule().clearTasks();
                }
            }
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.getWorld().isClient())
            return;

        this.brain.tickTasks();
    }

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

    public boolean isUsable(PlayerEntity player) {
        return true;
    }

    @Override
    public @org.jetbrains.annotations.Nullable ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ComputerMenuWithoutInventory(ModRegistry.Menus.COMPUTER.get(), id, inventory, this::isUsable, this.createServerComputer());
    }

    @Nullable
    public ServerComputer getServerComputer() {
        return !this.getWorld().isClient && this.getWorld().getServer() != null ? ServerContext.get(this.getWorld().getServer()).registry().get(this.instanceID) : null;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);

        ServerComputer computer = getServerComputer();

        if (computer != null)
            computer.close();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("computer_id", this.computerID);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("computer_id"))
            this.computerID = nbt.getInt("computer_id");
        super.readCustomDataFromNbt(nbt);
    }
}
