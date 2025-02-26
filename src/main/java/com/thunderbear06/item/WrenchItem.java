package com.thunderbear06.item;

import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.AndroidFrame;
import com.thunderbear06.entity.android.CommandAndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class WrenchItem extends Item {
    public WrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        stack.setDamage(stack.getDamage()+1);
        if (stack.getDamage() >= stack.getMaxDamage())
            return ItemStack.EMPTY;
        return stack.copyWithCount(1);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof CommandAndroidEntity)
            return ActionResult.FAIL;

        if (entity instanceof AndroidEntity android && !android.isLocked()) {
            android.deconstruct();
            if (!user.getWorld().isClient())
                stack.damage(1, user.getRandom(), (ServerPlayerEntity) user);
            return ActionResult.SUCCESS;
        }

        if (entity instanceof AndroidFrame androidFrame) {
            androidFrame.onBreak();
            if (!user.getWorld().isClient())
                stack.damage(1, user.getRandom(), (ServerPlayerEntity) user);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
