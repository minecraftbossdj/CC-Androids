package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

    public void useHandItemOnEntity(Hand hand, LivingEntity entity) {
        ServerPlayerEntity player = AndroidPlayer.get(this.brain).player();

        ItemStack handStack = hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();

        if (entity instanceof MobEntity mob) {
            if (handStack.isOf(Items.LEAD) && mob.getHoldingEntity() == null) {
                mob.attachLeash(this.owner, true);
                handStack.decrement(1);
                this.owner.setStackInHand(hand, handStack);
                return;
            } else if (handStack.isEmpty() && mob.getHoldingEntity() != null && mob.getHoldingEntity().equals(this.owner)) {
                mob.detachLeash(true, true);
                return;
            }
        }

        entity.interact(player, hand);
    }
}
