package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.block.BlockState;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MiningModule extends AbstractAndroidModule {
    private float breakProgress = 0.0f;

    public MiningModule(BaseAndroidEntity owner, AndroidBrain brain) {
        super(owner, brain);
    }

    public void mine(BlockPos pos) {
        BlockState state = this.android.getWorld().getBlockState(pos);

        this.breakProgress = tickBreakProgress(pos, state, this.breakProgress);

        if (this.breakProgress >= 10.0f) {
            breakBlock(pos);
            this.resetBreakProgress(pos);
        }
    }

    public void breakBlock(BlockPos pos) {
        AndroidPlayer.get(this.brain).player().interactionManager.tryBreakBlock(pos);
    }

    public boolean canMineBlock(BlockPos pos) {
        if (this.android.getWorld().isClient() || !pos.isWithinDistance(this.android.getBlockPos(), 2))
            return false;

        BlockState state = this.android.getWorld().getBlockState(pos);

        return !state.isAir() && state.getHardness(this.android.getWorld(), pos) > -1;
    }

    private float tickBreakProgress(BlockPos pos, BlockState state, float progress) {
        this.android.swingHand(Hand.MAIN_HAND);
        this.android.getWorld().setBlockBreakingInfo(this.android.getId(), pos, (int) progress);
        progress += AndroidPlayer.get(this.brain).player().getBlockBreakingSpeed(state);
        return progress;
    }

    private void resetBreakProgress(BlockPos pos) {
        this.android.getWorld().setBlockBreakingInfo(this.android.getId(), pos, 0);
        this.breakProgress = 0.0f;
    }
}
