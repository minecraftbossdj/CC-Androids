package com.thunderbear06.entity.android;

import com.thunderbear06.entity.EntityRegistry;
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
    private final byte maxComponentsNeeded = 8;
    private final byte maxIngotsNeeded = 10;

    public byte componentsNeeded;
    public boolean hasCore;
    public byte ingotsNeeded;

    private boolean isAdvanced = false;

    private long lastHitTime = 0;

    public AndroidFrame(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);

        this.componentsNeeded = this.maxComponentsNeeded;
        this.ingotsNeeded = this.maxIngotsNeeded;
        this.hasCore = false;
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

        if ((handStack.isOf(ItemRegistry.ANDROID_CPU) || handStack.isOf(Items.COMMAND_BLOCK)) && this.isReadyForCPU()) {
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
        if (this.componentsNeeded <= 0)
            return false;

        if (--this.componentsNeeded == 0)
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        else
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.NEUTRAL, 1.0f, 1.0f);

        return true;
    }

    private boolean addPlates(World world, boolean isGold) {
        if (this.componentsNeeded > 0)
            return false;

        if (this.ingotsNeeded <= 0)
            return false;

        if (isGold && !this.isAdvanced) {
            if (this.ingotsNeeded < this.maxIngotsNeeded)
                return false;
            this.isAdvanced = true;
        }

        if (!isGold && this.isAdvanced)
            return false;

        if (--this.ingotsNeeded == 0)
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        else
            world.playSoundFromEntity(null, this, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.NEUTRAL, 1.0f, 1.0f);

        return true;
    }

    private boolean insertCore(World world) {
        if (this.hasCore)
            return false;
        this.hasCore = true;

        world.playSoundFromEntity(null, this, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);

        return true;
    }

    private boolean isReadyForCPU() {
        return this.componentsNeeded == 0 && this.hasCore && this.ingotsNeeded == 0;
    }

    private void insertCPU(ItemStack cpu) {
        ComputerFamily family;
        int computerID = -1;

        if (cpu.isOf(Items.COMMAND_BLOCK))
            family = ComputerFamily.COMMAND;
        else
            family = this.isAdvanced ? ComputerFamily.ADVANCED : ComputerFamily.NORMAL;

        if (cpu.hasNbt() && cpu.getNbt().contains("ComputerID"))
            computerID = cpu.getNbt().getInt("ComputerID");

        finish(family, computerID);
    }

    private void finish(ComputerFamily family, int computerID) {
        BaseAndroidEntity android;

        switch (family) {
            case NORMAL -> android = EntityRegistry.ANDROID_ENTITY.create(getWorld());
            case ADVANCED -> android = EntityRegistry.ADVANCED_ANDROID_ENTITY.create(getWorld());
            case COMMAND -> android = EntityRegistry.COMMAND_ANDROID_ENTITY.create(getWorld());
            default -> throw new IllegalArgumentException("Unknown ComputerFamily " + family);
        }

        assert android != null;

        android.copyPositionAndRotation(this);
        android.getComputer().setComputerID(computerID);

        this.discard();
        this.getWorld().spawnEntity(android);

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
        int components_dropped = this.maxComponentsNeeded - this.componentsNeeded;

        for (int i = 0; i < components_dropped; i++) {
            this.dropStack(new ItemStack(ItemRegistry.COMPONENTS));
        }

        for (int j = 0; j < ingotsNeeded; j++) {
            this.dropStack(new ItemStack(this.isAdvanced ? Items.GOLD_INGOT : Items.IRON_INGOT));
        }

        if (this.hasCore)
            this.dropStack(new ItemStack(ItemRegistry.REDSTONE_REACTOR));

        this.dropStack(new ItemStack(ItemRegistry.ANDROID_FRAME));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putByte("ComponentsNeeded", this.componentsNeeded);
        nbt.putByte("IngotsNeeded", this.ingotsNeeded);
        nbt.putBoolean("IsAdvanced", this.isAdvanced);
        nbt.putBoolean("HasCore", this.hasCore);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("ComponentsNeeded")) {
            this.componentsNeeded = nbt.getByte("ComponentsNeeded");
            this.ingotsNeeded = nbt.getByte("IngotsNeeded");
            this.isAdvanced = nbt.getBoolean("IsAdvanced");
            this.hasCore = nbt.getBoolean("HasCore");
        }
        super.readNbt(nbt);
    }
}
