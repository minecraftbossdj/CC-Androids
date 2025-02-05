package com.thunderbear06.entity.android;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class AdvancedAndroidEntity extends AndroidEntity{
    public AdvancedAndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.computerContainer.setFamily(ComputerFamily.ADVANCED);
    }

    public static DefaultAttributeContainer.Builder createAndroidAttributes() {
        return AndroidEntity.createAndroidAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.9);
    }

    @Override
    public double getEntitySearchRadius() {
        return super.getEntitySearchRadius() * 3;
    }

    @Override
    public int getBlockSearchRadius() {
        return super.getBlockSearchRadius() * 3;
    }
}
