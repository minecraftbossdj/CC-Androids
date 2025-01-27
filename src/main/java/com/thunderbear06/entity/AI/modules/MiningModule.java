package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MiningModule extends AndroidModule{
    private float breakProgress = 0.0f;

    public MiningModule(BaseAndroidEntity owner, AndroidBrain brain) {
        super(owner, brain);
    }

    public void mineWith(BlockPos pos, ItemStack itemStack) {
        BlockState state = this.owner.getWorld().getBlockState(pos);

        this.breakProgress = tickBreakProgress(pos, state, itemStack, this.breakProgress);

        if (this.breakProgress >= 10.0f) {
            breakBlock(pos, state, itemStack);
            this.resetBreakProgress();
        }
    }

    public void breakBlock(BlockPos pos, BlockState state, ItemStack itemStack) {
        World world = this.owner.getWorld();

        boolean doDrop = !state.isToolRequired() || itemStack.isSuitableFor(state);

        world.breakBlock(pos, false, this.owner);
        world.addBlockBreakParticles(pos, state);

        // Handled here to support fortune enchant
        if (doDrop) {
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
            Block.dropStacks(state, world, pos, blockEntity, this.owner, itemStack);
        }

        itemStack.damage(1, this.owner.getRandom(), null);
    }

    public boolean canMineBlock(BlockPos pos, ItemStack itemStack) {
        BlockState state = this.owner.getWorld().getBlockState(pos);

        return !state.isAir() && state.getHardness(this.owner.getWorld(), pos) > -1;
    }

    private float tickBreakProgress(BlockPos pos, BlockState state, ItemStack itemStack, float progress) {
        this.owner.swingHand(Hand.MAIN_HAND);
        this.owner.getWorld().setBlockBreakingInfo(this.owner.getId(), pos, (int) progress);
        progress += getBreakSpeed(state, pos, itemStack);
        return progress;
    }

    private float getBreakSpeed(BlockState state, BlockPos pos, ItemStack itemStack) {
        float multiplier = itemStack.getMiningSpeedMultiplier(state);
        if (multiplier > 1.0F) {
            int i = EnchantmentHelper.getEfficiency(this.owner);
            if (i > 0 && !itemStack.isEmpty()) {
                multiplier += (float)(i * i + 1);
            }
        }

        return (1.0f / state.getHardness(this.owner.getWorld(), pos)) * multiplier;
    }

    private void resetBreakProgress() {
        this.breakProgress = 0.0f;
    }
}
