//package com.thunderbear06.entity.AI.goals;
//
//import com.thunderbear06.entity.AI.AndroidBrain;
//import com.thunderbear06.entity.AndroidEntity;
//
//public class FollowTargetGoal extends BaseAndroidGoal {
//    public FollowTargetGoal(AndroidEntity android, AndroidBrain brain) {
//        super(android, brain);
//    }
//
//    @Override
//    public boolean canStart() {
//        return this.brain.state == AndroidBrain.AndroidBrainState.FOLLOWING && this.brain.targetEntity != null;
//    }
//
//    @Override
//    public void tick() {
//        this.android.getNavigation().startMovingTo(this.brain.targetEntity, 0.5);
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
//        this.brain.state = AndroidBrain.AndroidBrainState.IDLE;
//    }
//}
