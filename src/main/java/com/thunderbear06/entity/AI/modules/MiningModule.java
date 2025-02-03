package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MiningModule extends AndroidModule{
    private float breakProgress = 0.0f;

    public MiningModule(BaseAndroidEntity owner, AndroidBrain brain) {
        super(owner, brain);
    }

    public void mineWith(BlockPos pos, ItemStack itemStack) {
        BlockState state = this.owner.getWorld().getBlockState(pos);

        this.breakProgress = tickBreakProgress(pos, state, itemStack, this.breakProgress);

        if (this.breakProgress >= 10.0f) {
            breakBlock(pos);
            this.resetBreakProgress();
        }
    }

    public void breakBlock(BlockPos pos) {
        AndroidPlayer.get(this.brain).player().interactionManager.tryBreakBlock(pos);
    }

    public boolean canMineBlock(BlockPos pos) {
        if (!this.owner.getWorld().isClient() && AndroidPlayer.get(this.brain).isBlockProtected((ServerWorld) this.owner.getWorld(), pos))
            return false;

        BlockState state = this.owner.getWorld().getBlockState(pos);

        return !state.isAir() && state.getHardness(this.owner.getWorld(), pos) > -1;
    }

    private float tickBreakProgress(BlockPos pos, BlockState state, ItemStack itemStack, float progress) {
        this.owner.swingHand(Hand.MAIN_HAND);
        this.owner.getWorld().setBlockBreakingInfo(this.owner.getId(), pos, (int) progress);
        progress += AndroidPlayer.get(this.brain).player().getBlockBreakingSpeed(state);
        return progress;
    }

    private void resetBreakProgress() {
        this.breakProgress = 0.0f;
    }
}
