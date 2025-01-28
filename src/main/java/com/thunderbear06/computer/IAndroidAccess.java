package com.thunderbear06.computer;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@ApiStatus.NonExtendable
public interface IAndroidAccess {

    World getWorld();
    BaseAndroidEntity getOwner();

    @Nullable GameProfile getOwningPlayer();

    void sendChatMessage(String rawMessage);

    boolean setTargetBlock(BlockPos pos);

    boolean setTargetEntity(UUID entityUUID);

    boolean hasTargetEntity();

    boolean hasTargetBlock();

    void setState(String state);

    @Nullable LivingEntity getTargetEntity();

    @Nullable BlockPos getTargetBlock();

    PlayerEntity getClosestPlayer();

    String getState();

    BlockPos getPosition();
}
