package com.thunderbear06.entity.android;

import com.thunderbear06.entity.AI.goals.*;
import com.thunderbear06.entity.pathing.PathFinder;
import com.thunderbear06.entity.pathing.PathNode;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class AndroidEntity extends BaseAndroidEntity {

    public AndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setFamily(ComputerFamily.NORMAL);

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
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!getWorld().isClient()) {

            if (this.brain.getOwningPlayer() == null)
                this.brain.setOwningPlayer(player.getGameProfile());

            ServerComputer serverComputer = createServerComputer();

            serverComputer.turnOn();

            serverComputer.keepAlive();

            (new ComputerContainerData(serverComputer, ItemStack.EMPTY)).open(player, this);
        }

        return ActionResult.CONSUME;
    }
}
