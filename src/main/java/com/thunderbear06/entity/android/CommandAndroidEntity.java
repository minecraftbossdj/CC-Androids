package com.thunderbear06.entity.android;

import com.thunderbear06.ai.goals.*;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CommandAndroidEntity extends AndroidEntity{
    public CommandAndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.computerContainer.setFamily(ComputerFamily.COMMAND);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!(source.getAttacker() instanceof PlayerEntity player))
            return source.getTypeRegistryEntry().isIn(DamageTypeTags.BYPASSES_INVULNERABILITY);

        return player.isCreative();
    }
}
