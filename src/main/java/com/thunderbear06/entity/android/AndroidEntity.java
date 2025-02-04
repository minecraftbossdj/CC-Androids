package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.goals.*;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;


public class AndroidEntity extends BaseAndroidEntity {

    public AndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.computerContainer.setFamily(ComputerFamily.NORMAL);

        initAndroidGoals();
    }

    protected void initAndroidGoals() {
        goalSelector.add(0, new AndroidMoveToBlockGoal(this, this.brain));
        goalSelector.add(0, new AndroidMineBlockGoal(this, this.brain));
        goalSelector.add(0, new AndroidUseItemOnBlockGoal(this, this.brain));
        goalSelector.add(0, new AndroidUseItemOnEntityGoal(this, this.brain));
        goalSelector.add(0, new AndroidFollowTargetGoal(this, this.brain));
        goalSelector.add(0, new AndroidMeleeAttackGoal(this));
        goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 10));
    }

    @Override
    public void tick() {
        if (this.getWorld().isClient() && this.age < 1)
            CCAndroids.LOGGER.info(this.computerContainer.getFamily().toString());
        super.tick();
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!getWorld().isClient()) {

            if (this.brain.getOwningPlayer() == null)
                this.brain.setOwningPlayer(player.getGameProfile());

            this.computerContainer.openComputer((ServerPlayerEntity) player);
        }

        return ActionResult.CONSUME;
    }
}
