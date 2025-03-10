package com.thunderbear06.computer;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.computer.peripherals.DummyPocket;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.menu.AndroidMenu;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class AndroidComputerContainer {
    private final BaseAndroidEntity android;

    public String label = "";
    public boolean isOn = false;
    public boolean fresh = false;

    @Nullable
    private UUID instanceID = null;
    private int computerID = -1;
    private boolean startOn = false;
    public ComputerFamily family;

    private UpgradeData<IPocketUpgrade> leftUpgrade;
    private UpgradeData<IPocketUpgrade> rightUpgrade;
    private final DummyPocket dummyPocket;

    public AndroidComputerContainer(BaseAndroidEntity android) {
        this.android = android;
        dummyPocket = new DummyPocket(android);
    }

    public void onTick() {
        if (computerID < 0 && !startOn)
            return;

        EntityComputer computer = getOrCreateServerComputer();

        if (startOn || (fresh && isOn)) {
            turnOn(computer);
            startOn = false;
        }

        fresh = false;
        computerID = computer.getID();
        isOn = computer.isOn();

        updateOwnerLabel(computer);

        tickPeripherals();

        computer.keepAlive();

        if (!isOn && android.isOn) {
            android.shutdown();
        }
    }

    public void turnOn(ServerComputer computer) {
        computer.turnOn();

        computerID = computer.getID();
        android.isOn = true;

        onHandItemChanged(Hand.MAIN_HAND);
        onHandItemChanged(Hand.OFF_HAND);

        getUpgradePeripherals();
    }

    public void openComputer(ServerPlayerEntity player) {
        ServerComputer computer = getOrCreateServerComputer();

        if (!isOn)
            turnOn(computer);

        PlatformHelper.get().openMenu(
                player,
                Text.literal(label),
                (syncId, playerInventory, player1) -> AndroidMenu.ofBrain(syncId, playerInventory, getBrain()),
                new ComputerContainerData(computer, ItemStack.EMPTY)
        );
    }

    protected void updateOwnerLabel(ServerComputer computer) {
        if (!Objects.equals(label, computer.getLabel())) {
            label = computer.getLabel();

            if (label == null || label.isEmpty()){
                android.setCustomName(Text.empty());
                android.setCustomNameVisible(false);
            } else {
                android.setCustomName(Text.literal(label));
                android.setCustomNameVisible(true);
            }
        }
    }

    public final EntityComputer getOrCreateServerComputer() {
        MinecraftServer server = android.getWorld().getServer();
        if (server == null) {
            throw new IllegalStateException("Cannot access server computer on the client.");
        }

        EntityComputer computer = (EntityComputer) ServerContext.get(server).registry().get(instanceID);
        if (computer == null) {
            if (computerID < 0) {
                computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(server, "computer");
            }

            computer = createComputer(computerID);
            instanceID = computer.register();
            fresh = true;
        }

        return computer;
    }

    public void setPeripheral(ComputerSide side, IPeripheral peripheral) {
        ServerComputer computer = getServerComputer();

        if (computer == null) {
            CCAndroids.LOGGER.error("Failed to set peripheral of type {} on side {} of computer container owned by {}. Reason: Failed to get ServerComputer (It was null)", peripheral.getType(), side.getName(), android.getName());
            return;
        }

        computer.setPeripheral(side, peripheral);
    }

    public ComputerFamily getFamily() {
        return family;
    }

    public int getComputerID() {
        return computerID;
    }

    public void setFamily(ComputerFamily family) {
        this.family = family;
    }

    public void setComputerID (int id) {
        computerID = id;
    }

    public AndroidBrain getBrain() {
        return android.brain;
    }

    protected EntityComputer createComputer(int id) {
        ServerComputer.Properties properties = ServerComputer.properties(id, getFamily())
                .addComponent(ComputerComponents.ANDROID_COMPUTER, android.brain)
                .label(label)
                .terminalSize(Config.TURTLE_TERM_WIDTH, Config.TURTLE_TERM_HEIGHT);

        return new EntityComputer((ServerWorld)android.getWorld(), android, properties);
    }

    @Nullable
    public ServerComputer getServerComputer() {
        return !android.getWorld().isClient && android.getWorld().getServer() != null ? ServerContext.get(android.getWorld().getServer()).registry().get(instanceID) : null;
    }

    public void writeNbt(NbtCompound computerCompound) {
        computerCompound.putInt("ComputerID", getComputerID());
    }

    public void readNbt(NbtCompound computerCompound) {
        setComputerID(computerCompound.getInt("ComputerID"));
    }

    public void getUpgradePeripherals() {
        setPeripheral(ComputerSide.LEFT, leftUpgrade == null ? null : leftUpgrade.upgrade().createPeripheral(dummyPocket));
        setPeripheral(ComputerSide.RIGHT, rightUpgrade == null ? null : rightUpgrade.upgrade().createPeripheral(dummyPocket));
    }

    private void tickPeripherals() {
        ServerComputer computer = getServerComputer();

        assert computer != null;

        if (leftUpgrade != null) leftUpgrade.upgrade().update(dummyPocket, computer.getPeripheral(ComputerSide.LEFT));
        if (rightUpgrade != null) rightUpgrade.upgrade().update(dummyPocket, computer.getPeripheral(ComputerSide.RIGHT));
    }

    public boolean hasUpgrade(ComputerSide side) {
        return (side == ComputerSide.LEFT && leftUpgrade != null) || (side == ComputerSide.RIGHT && rightUpgrade != null);
    }

    public void onHandItemChanged(Hand hand) {
        ItemStack handItem = android.getStackInHand(hand);

        if (hand == Hand.OFF_HAND) {
            leftUpgrade = dummyPocket.createUpgrade(handItem);
            return;
        }

        rightUpgrade = dummyPocket.createUpgrade(handItem);
    }
}