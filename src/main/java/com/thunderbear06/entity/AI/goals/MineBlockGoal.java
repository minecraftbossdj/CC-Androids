package com.thunderbear06.entity.AI.goals;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MineBlockGoal extends BaseAndroidGoal{
    private final float maxProgress = 10.0f;
    private float breakProgress = 0.0f;

    public MineBlockGoal(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    @Override
    public boolean canStart() {
        return this.brain.state.equals("miningBlock") && this.brain.targetBlock != null && canMineBlock();
    }

    @Override
    public void start() {
        this.breakProgress = 0.0f;
    }

    @Override
    public void tick() {
        BlockPos pos = this.brain.targetBlock;

        if (pos == null || !canMineBlock())
            return;

        this.android.getLookControl().lookAt(pos.toCenterPos());

        BlockState state = this.android.getWorld().getBlockState(pos);
        ItemStack stack = this.android.getMainHandStack();

        if (this.android.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) > 2) {
            this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 0.5);
        } else {
            this.breakProgress += getBreakSpeed(state, pos);
            this.android.swingHand(Hand.MAIN_HAND);
            this.android.getWorld().setBlockBreakingInfo(this.android.getId(), pos, (int) this.breakProgress);
            this.breakProgress += 1.0f / state.getHardness(this.android.getWorld(), pos);
        }
        if (this.breakProgress >= this.maxProgress) {
            this.android.getWorld().setBlockBreakingInfo(this.android.getId(), pos, 0);
            this.android.getWorld().breakBlock(pos, !state.isToolRequired() || stack.isSuitableFor(state), this.android);
        }
    }

    private boolean canMineBlock() {
        BlockState state = this.android.getWorld().getBlockState(this.brain.targetBlock);

        return !state.isAir() && state.getHardness(this.android.getWorld(), this.brain.targetBlock) != -1;
    }

    private float getBreakSpeed(BlockState state, BlockPos pos) {
        float multiplier = this.android.getMainHandStack().getMiningSpeedMultiplier(state);
        if (multiplier > 1.0F) {
            int i = EnchantmentHelper.getEfficiency(this.android);
            ItemStack itemStack = this.android.getMainHandStack();
            if (i > 0 && !itemStack.isEmpty()) {
                multiplier += (float)(i * i + 1);
            }
        }

        return 1.0f / state.getHardness(this.android.getWorld(), pos);
    }
}
