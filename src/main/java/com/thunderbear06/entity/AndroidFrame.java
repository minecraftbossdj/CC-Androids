package com.thunderbear06.entity;

import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class AndroidFrame extends MobEntity {
    private final byte max_components_needed = 8;
    private final byte max_ingots_needed = 10;

    public byte components_needed;
    public boolean has_core;
    public byte ingots_needed;

    private boolean isAdvanced = false;

    private long lastHitTime = 0;

    public AndroidFrame(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);

        this.components_needed = this.max_components_needed;
        this.ingots_needed = this.max_ingots_needed;
        this.has_core = false;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack handStack = hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();

        World world = player.getWorld();

        if (handStack.isOf(ItemRegistry.COMPONENTS)) {
            if (addComponents(world)){
                onSuccess(handStack, player, hand);
                return ActionResult.SUCCESS;
            }
        }

        if (handStack.isOf(Items.IRON_INGOT) || handStack.isOf(Items.GOLD_INGOT)) {
            if (addPlates(world, handStack.isOf(Items.GOLD_INGOT))) {
                onSuccess(handStack, player, hand);
                return ActionResult.SUCCESS;
            }
        }

        if (handStack.isOf(ItemRegistry.REDSTONE_REACTOR)) {
            if (insertCore(world)) {
                onSuccess(handStack, player, hand);
                return ActionResult.SUCCESS;
            }
        }

        if (handStack.isOf(ItemRegistry.ANDROID_CPU) || handStack.isOf(Items.COMMAND_BLOCK) && this.isReadyForCPU()) {
            insertCPU(handStack);
            onSuccess(handStack, player, hand);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    private void onSuccess(ItemStack stack, PlayerEntity player, Hand hand) {
        stack.decrement(1);
        player.setStackInHand(hand, stack);
    }

    private boolean addComponents(World world) {
        if (this.components_needed <= 0)
            return false;

        if (--this.components_needed == 0)
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        else
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.NEUTRAL, 1.0f, 1.0f);

        return true;
    }

    private boolean addPlates(World world, boolean isGold) {
        if (this.components_needed > 0)
            return false;

        if (this.ingots_needed <= 0)
            return false;

        if (isGold && !this.isAdvanced) {
            if (this.ingots_needed < this.max_ingots_needed)
                return false;
            this.isAdvanced = true;
        }

        if (!isGold && this.isAdvanced)
            return false;

        if (--this.ingots_needed == 0)
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        else
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.NEUTRAL, 1.0f, 1.0f);

        return true;
    }

    private boolean insertCore(World world) {
        if (this.has_core)
            return false;
        this.has_core = true;

        world.playSoundFromEntity(null, this, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);

        return true;
    }

    private boolean isReadyForCPU() {
        return this.components_needed == 0 && this.has_core && this.ingots_needed == 0;
    }

    private void insertCPU(ItemStack cpu) {
        int computer_id = -1;

        if (cpu.hasNbt() && cpu.getNbt().contains("ComputerID")) {
            computer_id = cpu.getNbt().getInt("ComputerID");
        }

        ComputerFamily family;

        if (cpu.isOf(Items.COMMAND_BLOCK))
            family = ComputerFamily.COMMAND;
        else {
            family = this.isAdvanced ? ComputerFamily.ADVANCED : ComputerFamily.NORMAL;
        }

        this.finish(computer_id, family);
    }

    private void finish(int computerID, ComputerFamily family) {
        AndroidEntity android = EntityRegistry.ANDROID_ENTITY.create(this.getWorld());

        assert android != null;

        android.copyPositionAndRotation(this);

        this.getWorld().spawnEntity(android);

        android.computerContainer.setComputerID(computerID);
        android.computerContainer.setFamily(family);

        this.discard();

        android.getWorld().playSoundFromEntity(null, android, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.NEUTRAL, 1.0f, 1.0f);

    }

    @Override
    protected void pushAway(Entity entity) {}

    @Override
    public void pushAwayFrom(Entity entity) {}

    @Override
    public void takeKnockback(double strength, double x, double z) {}

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient() || this.isRemoved())
            return false;
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill();
            return false;
        }
        if (this.isInvulnerableTo(source))
            return false;
        if (source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            this.onBreak();
            this.kill();
            return false;
        }
        if (!(source.getAttacker() instanceof PlayerEntity))
            return false;
        if (!((PlayerEntity)source.getAttacker()).getAbilities().allowModifyWorld)
            return false;

        if (source.isSourceCreativePlayer()) {
            this.playHurtSound(source);
            this.kill();
        } else {
            long l = this.getWorld().getTime();

            if (l - this.lastHitTime > 5L) {
                this.getWorld().sendEntityStatus(this, (byte)32);
                this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
                this.lastHitTime = l;
            } else {
                this.onBreak();
                this.kill();
            }
        }

        return true;
    }

    private void onBreak() {
        this.dropInventory();
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
        this.emitGameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_ANVIL_FALL;
    }

    @Override
    protected void dropInventory() {
        int components_dropped = this.max_components_needed - this.components_needed;

        for (int i = 0; i < components_dropped; i++) {
            this.dropStack(new ItemStack(ItemRegistry.COMPONENTS));
        }

        if (!this.has_core)
            return;

        this.dropStack(new ItemStack(ItemRegistry.REDSTONE_REACTOR));
        this.dropStack(new ItemStack(ItemRegistry.ANDROID_FRAME));
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
