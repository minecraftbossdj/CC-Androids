package com.thunderbear06.ai.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.LivingEntity;

public class LookAtEntityTask extends EntityBasedTask{
    private final float chance;
    
    public LookAtEntityTask(AndroidEntity android, float chance) {
        super(android);
        this.chance = chance;
    }

    @Override
    public void firstTick() {}

    @Override
    public void tick() {
        if (isInRange(10) && this.android.getRandom().nextFloat() < this.chance)
            this.android.lookAtEntity(getTarget(), 0.5f, 0.5f);
    }

    @Override
    public void lastTick() {}
}
