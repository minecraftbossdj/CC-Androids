package com.thunderbear06.entity.android;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.ai.task.TaskManager;
import com.thunderbear06.ai.task.tasks.*;
import com.thunderbear06.item.ItemRegistry;
import com.thunderbear06.sounds.SoundRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class AndroidEntity extends BaseAndroidEntity {
    protected final TaskManager taskManager;
    private boolean isLocked = false;

    public AndroidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.brain = new AndroidBrain(this);
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
        this.taskManager.addTask(new BreakBlockTask(this, 0.5f));
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

        if (hasFuel())
            this.taskManager.tick();
        else if (!this.getNavigation().isIdle())
            this.getNavigation().stop();
    }

    @Override
    protected boolean isIdle() {
        return !this.taskManager.hasTask();
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.isLocked && !this.brain.isOwningPlayer(player)) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            player.sendMessage(Text.translatable("entity.cc-androids.android.locked"), true);
            return ActionResult.FAIL;
        }

        if (player.isSneaking()) {
            player.setStackInHand(Hand.MAIN_HAND, swapHandStack(player.getStackInHand(hand)));
            return ActionResult.SUCCESS;
        }

        if (player.getStackInHand(hand).isOf(Items.TRIPWIRE_HOOK) && this.brain.isOwningPlayer(player)) {
            this.isLocked = !this.isLocked;
            return ActionResult.SUCCESS;
        }

        if (player.getStackInHand(hand).isOf(ItemRegistry.COMPONENTS) && this.repair()) {
            player.getStackInHand(hand).decrement(1);
            return ActionResult.SUCCESS;
        }

        if (!getWorld().isClient()) {
            if (this.brain.getOwningPlayerProfile() == null)
                this.brain.setOwningPlayer(player.getGameProfile());

            this.getComputer().openComputer((ServerPlayerEntity) player);
        }

        return ActionResult.CONSUME;
    }

    protected ItemStack swapHandStack(ItemStack stack) {
        ItemStack heldStack = this.getMainHandStack();

        if (stack.isIn(ItemTags.FLOWERS))
            spawnHearts();

        this.setStackInHand(Hand.MAIN_HAND, stack);
        return heldStack;
    }

    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    public boolean repair() {
        if (this.getHealth() < this.getMaxHealth()) {
            this.getWorld().playSoundFromEntity(null, this, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            this.heal(5);
            return true;
        }
        return false;
    }

    private void spawnHearts() {
        double d = this.random.nextGaussian() * 0.02;
        double e = this.random.nextGaussian() * 0.02;
        double f = this.random.nextGaussian() * 0.02;
        this.getWorld().addParticle(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundRegistry.ANDROID_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.ANDROID_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundRegistry.ANDROID_DEATH;
    }
}
