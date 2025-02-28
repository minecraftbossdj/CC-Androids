package com.thunderbear06.menu;

import com.thunderbear06.ai.AndroidBrain;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AndroidMenu extends AbstractComputerMenu {
    public AndroidMenu(int id, Predicate<PlayerEntity> canUse, ComputerFamily family, @Nullable ServerComputer computer, @Nullable ComputerContainerData containerData, PlayerInventory playerInventory, SimpleInventory inventory) {
        super(MenuRegistry.ANDROID.get(), id, canUse, family, computer, containerData);
    }

    public static AndroidMenu ofBrain(int id, PlayerInventory inventory, AndroidBrain brain) {
        return new AndroidMenu(
                id, player -> true, brain.getAndroid().getComputer().getFamily(), brain.getAndroid().getComputer().getOrCreateServerComputer(), null, inventory, brain.getAndroid().inventory
        );
    }

    public static AndroidMenu ofData(int id, PlayerInventory inv, ComputerContainerData data) {
        return new AndroidMenu(id, player -> true, data.family(), null, data, inv, new SimpleInventory(11));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }
}
