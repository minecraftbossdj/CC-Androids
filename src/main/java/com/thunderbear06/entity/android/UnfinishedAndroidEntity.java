package com.thunderbear06.entity.android;

import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UnfinishedAndroidEntity extends MobEntity {
    private byte components_needed;
    private boolean has_core;

    public UnfinishedAndroidEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);

        this.components_needed = 16;
        this.has_core = false;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack handStack = hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();

        if (handStack.isOf(ItemRegistry.COMPONENTS) && this.components_needed > 0) {
            this.components_needed--;

            World world = player.getWorld();

            if (this.components_needed == 0) {
                world.playSoundFromEntity(null, this, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            } else
                world.playSoundFromEntity(null, this, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        } else if (handStack.isOf(ItemRegistry.REDSTONE_REACTOR) && !this.has_core) {
            this.has_core = true;

            World world = player.getWorld();

            world.playSoundFromEntity(null, this, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        } else if (handStack.isOf(ItemRegistry.ANDROID_CPU) && this.isReadyForCPU()) {
            insertCPU(handStack);
        } else {
            return ActionResult.FAIL;
        }

        handStack.decrement(1);
        player.setStackInHand(hand, handStack);
        return ActionResult.SUCCESS;
    }

    private boolean isReadyForCPU() {
        return this.components_needed == 0 && this.has_core;
    }

    private void insertCPU(ItemStack cpu) {
        AndroidEntity android = this.convertTo(EntityRegistry.ANDROID_ENTITY, false);
        if (android != null) {
            int computer_id = -1;
            ComputerFamily family = ComputerFamily.NORMAL;

            if (cpu.hasNbt() && cpu.getNbt().contains("Computer")) {
                NbtCompound compound = cpu.getNbt().getCompound("Computer");

                computer_id = compound.getInt("ComputerID");
                family = ComputerFamily.valueOf(compound.getString("ComputerFamily"));
            }

            android.computerContainer.setComputerID(computer_id);
            android.computerContainer.setFamily(family);

            android.getWorld().playSoundFromEntity(null, android, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putByte("ComponentsNeeded", this.components_needed);
        nbt.putBoolean("HasCore", this.has_core);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("ComponentsNeeded")) {
            this.components_needed = nbt.getByte("ComponentsNeeded");
            this.has_core = nbt.getBoolean("HasCore");
        }
        super.readNbt(nbt);
    }
}
