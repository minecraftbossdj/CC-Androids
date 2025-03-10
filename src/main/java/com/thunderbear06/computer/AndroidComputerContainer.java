package com.thunderbear06.computer;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.computer.peripherals.DummyPocket;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.menu.AndroidMenu;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class AndroidComputerContainer {
    private final BaseAndroidEntity android;

    @Nullable
    public String label;
    public boolean isOn = false;
    public boolean fresh = false;

    public boolean on = false;
    public boolean locked = false;

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
        if (this.locked && !player.getGameProfile().equals(this.android.brain.getOwningPlayerProfile())) {
            player.getWorld().playSoundFromEntity(null, this.android, SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            return;
        }

        ServerComputer computer = getOrCreateServerComputer();

        if (!this.on) {
            turnOn(computer);
        }

        Text text = Text.literal("CCAndroid");


        PlatformHelper.get().openMenu(
                player,
                text,
                (syncId, playerInventory, player1) -> AndroidMenu.ofBrain(syncId, playerInventory, getBrain()),
                new ComputerContainerData(computer, ItemStack.EMPTY)
        );

        CCAndroids.LOGGER.info(String.valueOf(text));
    }

    protected void updateOwnerLabel(ServerComputer computer) {
        if (!Objects.equals(this.label, computer.getLabel())) {
            this.label = computer.getLabel();

            if (this.label == null || this.label.isEmpty()){
                this.android.setCustomName(Text.empty());
                this.android.setCustomNameVisible(false);
            } else {
                this.android.setCustomName(Text.literal(this.label));
                this.android.setCustomNameVisible(true);
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
            CCAndroids.LOGGER.error("Failed to set peripheral of type {} on side {} of computer container owned by {}. Reason: Failed to get ServerComputer (It was null)", peripheral.getType(), side.getName(), this.android.getName());
            return;
        }

        computer.setPeripheral(side, peripheral);
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
        return !this.android.getWorld().isClient && this.android.getWorld().getServer() != null ? ServerContext.get(this.android.getWorld().getServer()).registry().get(this.instanceID) : null;
    }

    public void writeNbt(NbtCompound computerCompound) {
        computerCompound.putInt("ComputerID", this.getComputerID());
    }

    public void readNbt(NbtCompound computerCompound) {
        this.setComputerID(computerCompound.getInt("ComputerID"));
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