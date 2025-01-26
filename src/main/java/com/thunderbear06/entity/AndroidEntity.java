package com.thunderbear06.entity;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;


public class AndroidEntity extends BaseAndroidEntity {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public AndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        //initAndroidGoals();
    }

    protected void initAndroidGoals() {
//        goalSelector.add(0, new MoveToTargetPosGoal(this, this.brain));
//        goalSelector.add(0, new FollowTargetGoal(this, this.brain));
//        goalSelector.add(0, new AndroidMeleeAttackGoal(this, 0.5, false));
        goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 10));
    }

    public static DefaultAttributeContainer.Builder createAndroidAttributes() {
        return createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!getWorld().isClient() && this.isUsable(player)) {

            ServerComputer serverComputer = createServerComputer();

            serverComputer.turnOn();

            serverComputer.keepAlive();

            (new ComputerContainerData(serverComputer, ItemStack.EMPTY)).open(player, this);
        }

        return ActionResult.CONSUME;
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }
}
