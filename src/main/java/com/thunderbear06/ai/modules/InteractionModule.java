package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import com.thunderbear06.item.ItemRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class InteractionModule extends AbstractAndroidModule {
    public InteractionModule(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    public void interactWithBlock(Hand hand, BlockPos pos) {
        ServerPlayerEntity player = AndroidPlayer.get(this.brain).player();

        this.android.swingHand(hand);

        player.interactionManager.interactBlock(player, this.android.getWorld(), player.getStackInHand(hand), hand, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, true));
    }

    public void interactWithEntity(Hand hand, LivingEntity entity) {
        ServerPlayerEntity player = AndroidPlayer.get(this.brain).player();

        ItemStack handStack = player.getStackInHand(hand);

        this.android.swingHand(hand);

        if (entity instanceof AndroidEntity android && handStack.isOf(ItemRegistry.COMPONENTS)) {
            if (android.repair()) {
                handStack.decrement(1);
                return;
            }
        }

        if (entity instanceof MobEntity mob) {
            if (handStack.isOf(Items.LEAD) && mob.getHoldingEntity() == null) {
                mob.attachLeash(this.android, true);
                handStack.decrement(1);
                this.android.setStackInHand(hand, handStack);
                return;
            } else if (handStack.isEmpty() && mob.getHoldingEntity() != null && mob.getHoldingEntity().equals(this.android)) {
                mob.detachLeash(true, true);
                return;
            }
        }

        entity.interact(player, hand);
    }
}
