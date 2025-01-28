package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UnfinishedAndroidEntity extends MobEntity {
    public UnfinishedAndroidEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        // TODO: This won't use redstone blocks
        if (player.getMainHandStack().isOf(Items.REDSTONE_BLOCK)) {
            AndroidEntity android = this.convertTo(CCAndroids.ANDROID_ENTITY, false);
            if (android != null)
                android.setFamily(ComputerFamily.NORMAL);
        }

        return super.interactMob(player, hand);
    }
}
