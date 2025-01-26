//package com.thunderbear06.entity.AI.goals;
//
//import com.thunderbear06.entity.AI.AndroidBrain;
//import com.thunderbear06.entity.BaseAndroidEntity;
//import net.minecraft.entity.ai.goal.MeleeAttackGoal;
//
//public class AndroidMeleeAttackGoal extends MeleeAttackGoal {
//    private final AndroidBrain brain;
//
//    public AndroidMeleeAttackGoal(BaseAndroidEntity mob, double speed, boolean pauseWhenMobIdle) {
//        super(mob, speed, pauseWhenMobIdle);
//        this.brain = mob.brain;
//    }
//
//    @Override
//    public void start() {
//        super.start();
//        brain.state = AndroidBrain.AndroidBrainState.ATTACKING;
//    }
//
//    @Override
//    public void stop() {
//        super.stop();
//        brain.state = AndroidBrain.AndroidBrainState.IDLE;
//    }
//}
