package com.thunderbear06.entity.AI.tasks;

import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.BaseAndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public abstract class TargetedAndroidTask extends AndroidTask{
    protected LivingEntity targetEntity;

    public TargetedAndroidTask(BaseAndroidEntity androidEntity, AndroidBrain brain, UUID entityUUID, String statusName) {
        super(androidEntity, brain, statusName);

        ServerWorld world = (ServerWorld) this.android.getWorld();
        this.targetEntity = (LivingEntity) world.getEntity(entityUUID);
    }

    public boolean targetValid() {
        return this.targetEntity != null && this.targetEntity.isAlive();
    }
}
