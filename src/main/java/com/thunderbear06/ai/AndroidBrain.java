package com.thunderbear06.ai;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.computer.IAndroidAccess;
import com.thunderbear06.ai.modules.InteractionModule;
import com.thunderbear06.ai.modules.MiningModule;
import com.thunderbear06.ai.modules.SensorModule;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import com.thunderbear06.util.PathReachChecker;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class AndroidBrain implements IAndroidAccess {
    private final BaseAndroidEntity owner;
    private final SensorModule sensor;
    private final MiningModule miningModule;
    private final InteractionModule interactionModule;

    @Nullable
    private GameProfile owningPlayer;


    @Nullable
    private LivingEntity targetEntity;
    @Nullable
    private BlockPos targetBlock;

    private String state = "idle";
    private long lastChatMessageTime = 0;

    @Deprecated
    public AndroidPlayer fakePlayer;

    public AndroidBrain(BaseAndroidEntity android) {
        this.owner = android;
        this.sensor = new SensorModule(this.owner, this, 10, 6);
        this.miningModule = new MiningModule(this.owner, this);
        this.interactionModule = new InteractionModule(this.owner, this);
        if (android.getWorld() instanceof ServerWorld)
            this.fakePlayer = AndroidPlayer.get(this);
        else {
            this.fakePlayer = null;
        }
    }

    @Override
    public World getWorld() {
        return this.owner.getWorld();
    }

    @Override
    public BaseAndroidEntity getOwner() {
        return this.owner;
    }

    public @Nullable GameProfile getOwningPlayer() {
        return this.owningPlayer;
    }

    public void setOwningPlayer(@NotNull GameProfile player) {
        this.owningPlayer = player;
    }

    public SensorModule getSensorModule() {
        return this.sensor;
    }

    public MiningModule getMiningModule() {
        return this.miningModule;
    }

    public InteractionModule getInteractionModule() {
        return this.interactionModule;
    }

    @Override
    public void sendChatMessage(String rawMessage) {
        ServerWorld world = (ServerWorld) this.owner.getWorld();

        long time = this.owner.getWorld().getTime();

        if (time - this.lastChatMessageTime < 20)
            return;

        this.lastChatMessageTime = this.owner.getWorld().getTime();

        world.getPlayers().forEach(serverPlayerEntity -> {
            if (this.owner.getPos().distanceTo(serverPlayerEntity.getPos()) < 50)
                serverPlayerEntity.sendChatMessage(SentMessage.of(SignedMessage.ofUnsigned(rawMessage)), false, MessageType.params(MessageType.CHAT, this.owner));
        });
    }

    @Override
    public boolean setTargetBlock(BlockPos pos) {
        if (pos == null) {
            this.targetBlock = null;
            return true;
        }

        if (!this.owner.getWorld().isInBuildLimit(pos))
            return false;

        if (this.owner.getBlockPos().getSquaredDistance(pos) > 100) {
            this.targetBlock = pos;
            return true;
        }

        BlockPos closest = PathReachChecker.getClosestPosition(this.owner.getBlockPos(), pos, (ServerWorld) this.owner.getWorld());

        if (!closest.isWithinDistance(pos, 3))
            return false;

        this.targetBlock = closest;
        return true;
    }

    @Override
    public boolean setTargetEntity(UUID entityUUID) {
        if (entityUUID == null) {
            this.targetEntity = null;
            return true;
        }

        ServerWorld world = (ServerWorld) this.owner.getWorld();

        this.targetEntity = (LivingEntity) world.getEntity(entityUUID);

        return this.targetEntity != null && !this.targetEntity.isDead();
    }

    @Override
    public boolean hasTargetEntity() {
        return this.targetEntity != null && this.targetEntity.isAlive();
    }

    @Override
    public boolean hasTargetBlock() {
        return this.targetBlock != null;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public @Nullable LivingEntity getTargetEntity() {
        return this.targetEntity;
    }

    @Override
    public @Nullable BlockPos getTargetBlock() {
        return this.targetBlock;
    }

    @Override
    public PlayerEntity getClosestPlayer() {
        return this.sensor.getClosestPlayer();
    }

    @Override
    public String getState() {
        return this.state;
    }

    @Override
    public BlockPos getPosition() {
        return this.owner.getBlockPos();
    }

    public void writeNbt(NbtCompound nbt) {
        if (this.getOwningPlayer() != null) {
            NbtCompound owner = new NbtCompound();
            owner.putString("Name", this.getOwningPlayer().getName());
            owner.putUuid("UUID", this.getOwningPlayer().getId());
            nbt.put("OwningPlayer", owner);
        }
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("OwningPlayer")) {
            NbtCompound owner = nbt.getCompound("OwningPlayer");
            this.owningPlayer = new GameProfile(owner.getUuid("UUID"), owner.getString("Name"));
        }
    }
}
