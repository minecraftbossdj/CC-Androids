package com.thunderbear06.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class AndroidInventory extends SimpleInventory {
    public AndroidInventory(int size) {
        super(size);
    }

    @Override
    public NbtList toNbtList() {
        NbtList list = new NbtList();

        for (int i = 0; i < this.size(); i++) {
            list.add(getStack(i).writeNbt(new NbtCompound()));
        }

        return list;
    }

    @Override
    public void readNbtList(NbtList nbtList) {
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound compound = nbtList.getCompound(i);

            this.addStack(ItemStack.fromNbt(compound));
        }
    }
}
