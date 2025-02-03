package com.thunderbear06.entity.player;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.computer.IAndroidAccess;
import com.thunderbear06.ai.AndroidBrain;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.UUID;

public class AndroidPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidPlayer.class);
    private static final GameProfile DEFAULT_PROFILE = new GameProfile(UUID.fromString("0d0c4ca0-4ff1-11e4-916c-0800200c9a66"), "[ComputerCraft]");
    private final ServerPlayerEntity player;

    public AndroidPlayer(ServerPlayerEntity player) {
        this.player = player;
    }

    private static AndroidPlayer create(IAndroidAccess android) {
        ServerWorld world = (ServerWorld)android.getWorld();
        GameProfile profile = android.getOwningPlayer();
        AndroidPlayer player = new AndroidPlayer(PlatformHelper.get().createFakePlayer(world, getProfile(profile != null ? profile : DEFAULT_PROFILE)));
        player.setState(android);
        return player;
    }

    public static AndroidPlayer get(IAndroidAccess access) {
        if (!(access instanceof AndroidBrain brain)) {
            throw new IllegalStateException("IAndroidAccess is not a brain");
        } else {
            AndroidPlayer player = brain.fakePlayer;
            if (player != null && player.player.getGameProfile() == getProfile(access.getOwningPlayer()) && player.player.getWorld() == access.getWorld()) {
                player.setState(access);
            } else {
                player = brain.fakePlayer = create(brain);
            }

            return player;
        }
    }

    public ServerPlayerEntity player() {
        return this.player;
    }

    private void setState(IAndroidAccess android) {
        if (this.player.currentScreenHandler != this.player.playerScreenHandler) {
            LOGGER.warn("Android has open container ({})", this.player.currentScreenHandler);
            this.player.onHandledScreenClosed();
        }

        setPosition(android);
        loadHand(android.getOwner().getMainHandStack(), Hand.MAIN_HAND);
        loadHand(android.getOwner().getOffHandStack(), Hand.OFF_HAND);
    }

    private void setRotation(Vec3d rotation) {
        this.player.setYaw((float) rotation.y);
        this.player.setPitch((float) rotation.x);
    }

    public void setPosition(IAndroidAccess android) {
        this.setRotation(android.getOwner().getRotationVector());

        Vec3d pos = android.getOwner().getPos();

        this.player.setPosition(pos);

        this.player.prevX = pos.getX();
        this.player.prevY = pos.getY();
        this.player.prevZ = pos.getZ();

        this.player.prevPitch = this.player.getPitch();
        this.player.prevHeadYaw = this.player.headYaw = this.player.prevYaw = this.player.getYaw();
    }

    private static GameProfile getProfile(@Nullable GameProfile profile) {
        return profile != null && profile.isComplete() ? profile : DEFAULT_PROFILE;
    }

    public void loadHand(ItemStack stack, Hand hand) {
        this.player.setStackInHand(hand, stack);
    }

    public boolean isBlockProtected(ServerWorld level, BlockPos pos) {
        return level.getServer().isSpawnProtected(level, pos, this.player);
    }
}
