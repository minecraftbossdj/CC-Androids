package com.thunderbear06.computer;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.util.ComponentMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class AndroidComputerContainer implements NamedScreenHandlerFactory {
    private final BaseAndroidEntity android;

    @Nullable
    public String label;
    public boolean on = false;

    @Nullable
    private UUID instanceID = null;
    private int computerID = -1;
    private boolean startOn = false;
    private ComputerFamily family;

    public AndroidComputerContainer(BaseAndroidEntity android) {
        this.android = android;
    }

    public void onTick() {
        if (this.startOn) {
            this.turnOn(getOrCreateServerComputer());
            this.startOn = false;
        }

        if (this.on && this.computerID >= 0) {
            ServerComputer computer = getServerComputer();

            if (computer == null) {
                CCAndroids.LOGGER.error("Automaton is on but has no ServerComputer");
                return;
            }

            updateOwnerLabel(computer);

            computer.keepAlive();
        }
    }

    public void turnOn(ServerComputer computer) {
        if (!computer.isOn()) {
            computer.turnOn();
            this.computerID = computer.getID();
            this.on = true;
        }
    }

    public void openComputer(ServerPlayerEntity player) {
        ServerComputer computer = getOrCreateServerComputer();

        if (!this.on) {
            turnOn(computer);
        }

        (new ComputerContainerData(computer, ItemStack.EMPTY)).open(player, this);
    }

    protected void updateOwnerLabel(ServerComputer computer) {
        if (!Objects.equals(this.label, computer.getLabel())) {
            this.label = computer.getLabel();

            if (this.label == null || this.label.isBlank()){
                this.android.setCustomName(Text.empty());
                this.android.setCustomNameVisible(false);
            } else {
                this.android.setCustomName(Text.literal(this.label));
                this.android.setCustomNameVisible(true);
            }
        }
    }

    public final ServerComputer getOrCreateServerComputer() {
        MinecraftServer server = this.android.getWorld().getServer();
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
            }

            return computer;
        }
    }

    public ComputerFamily getFamily() {
        return this.family;
    }

    public int getComputerID() {
        return this.computerID;
    }

    public void setFamily(ComputerFamily family) {
        this.family = family;
    }

    public void setComputerID (int id) {
        this.computerID = id;
    }

    protected EntityComputer createComputer(int id) {
        return new EntityComputer((ServerWorld)this.android.getWorld(), this.android, id, this.label, this.getFamily(), Config.computerTermWidth, Config.computerTermHeight, ComponentMap.builder().add(ComputerComponents.ANDROID_COMPUTER, this.android.brain).build());
    }

    @Override
    public @Nullable ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ComputerMenuWithoutInventory(ModRegistry.Menus.COMPUTER.get(), id, inventory, player1 -> true, this.getOrCreateServerComputer());
    }

    @Nullable
    public ServerComputer getServerComputer() {
        return !this.android.getWorld().isClient && this.android.getWorld().getServer() != null ? ServerContext.get(this.android.getWorld().getServer()).registry().get(this.instanceID) : null;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Android terminal");
    }

    public void writeNbt(NbtCompound computerCompound) {
        computerCompound.putInt("ComputerID", this.getComputerID());
        computerCompound.putString("ComputerFamily", this.getFamily().toString());
    }

    public void readNbt(NbtCompound computerCompound) {
        this.computerID = computerCompound.getInt("ComputerID");
        this.setFamily(ComputerFamily.valueOf(computerCompound.getString("ComputerFamily")));
    }
}
