//package com.thunderbear06.entity.AI.goals;
//
//import com.thunderbear06.entity.AI.AndroidBrain;
//import com.thunderbear06.entity.AndroidEntity;
//import net.minecraft.util.math.BlockPos;
//
//public class MoveToTargetPosGoal extends BaseAndroidGoal {
//
//    public MoveToTargetPosGoal(AndroidEntity android, AndroidBrain brain) {
//        super(android, brain);
//    }
//
//    @Override
//    public boolean canStart() {
//        return this.brain.state == AndroidBrain.AndroidBrainState.MOVING && this.brain.targetPosition != null;
//    }
//
//    @Override
//    public void start() {
//        BlockPos pos = this.brain.targetPosition;
//
//        this.android.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
//    }
//
//    @Override
//    public boolean shouldContinue() {
//        return super.shouldContinue() && !this.android.getNavigation().isIdle();
//    }
//
//    @Override
//    public void stop() {
//        this.android.getNavigation().stop();
//        this.brain.ClearPositionTarget();
//    }
//}
