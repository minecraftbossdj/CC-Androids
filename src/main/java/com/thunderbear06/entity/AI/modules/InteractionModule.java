package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class InteractionModule extends AndroidModule{
    public InteractionModule(BaseAndroidEntity owner, AndroidBrain brain) {
        super(owner, brain);
    }

    public void useHandItemOnBlock(Hand hand, BlockPos pos) {
        ServerPlayerEntity player = AndroidPlayer.get(this.brain).player();

        ItemStack handStack = hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();

        player.interactionManager.interactBlock(player, this.owner.getWorld(), handStack, hand, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, false));
    }
}
