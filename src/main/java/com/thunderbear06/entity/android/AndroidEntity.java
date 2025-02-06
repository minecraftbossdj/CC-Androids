package com.thunderbear06.entity.android;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.ai.TaskManager;
import com.thunderbear06.ai.tasks.*;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;


public class AndroidEntity extends BaseAndroidEntity {
    protected final TaskManager taskManager;

    public AndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.brain = new NewAndroidBrain(this);
        this.taskManager = new TaskManager();
        this.computerContainer.setFamily(ComputerFamily.NORMAL);

        addAndroidTasks();
        initAndroidGoals();
    }

    public static DefaultAttributeContainer.Builder createAndroidAttributes() {
        return createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0);
    }

    protected void addAndroidTasks() {
        this.taskManager.addTask(new AttackEntityTask(this, 0.5f));
        this.taskManager.addTask(new MineBlockTask(this, 0.5f));
        this.taskManager.addTask(new InteractBlockTask(this, 0.5f));
        this.taskManager.addTask(new InteractEntityTask(this, 0.5f));
        this.taskManager.addTask(new MoveToBlockTask(this, 0.5f));
        this.taskManager.addTask(new MoveToEntityTask(this, 0.5f));
    }

    protected void initAndroidGoals() {
        this.goalSelector.add(0, new LookAtEntityGoal(this, PlayerEntity.class, 10));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        this.taskManager.tick();
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        ItemStack stack1 = this.getStackInHand(hand);

        if (!stack.isEmpty() && stack1.isEmpty()) {
            if (stack.isIn(ItemTags.FLOWERS)) {
                spawnHearts();
            }

            this.setStackInHand(hand, stack.copyWithCount(1));

            player.getStackInHand(hand).decrement(1);
            player.setStackInHand(hand, stack);

            return ActionResult.SUCCESS;
        }


        if (player.isSneaking() && stack.isEmpty() && !stack1.isEmpty()) {
            player.setStackInHand(hand, stack1);
            this.setStackInHand(hand, ItemStack.EMPTY);

            return ActionResult.SUCCESS;
        }

        if (!getWorld().isClient()) {
            if (this.brain.getOwningPlayerProfile() == null)
                this.brain.setOwningPlayer(player.getGameProfile());

            this.getComputer().openComputer((ServerPlayerEntity) player);
        }

        return ActionResult.CONSUME;
    }

    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    private void spawnHearts() {
        double d = this.random.nextGaussian() * 0.02;
        double e = this.random.nextGaussian() * 0.02;
        double f = this.random.nextGaussian() * 0.02;
        this.getWorld().addParticle(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
    }
}
