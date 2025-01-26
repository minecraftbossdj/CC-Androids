package com.thunderbear06.entity.render;

import com.thunderbear06.entity.AndroidEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class RobotEntityModel extends PlayerEntityModel<AndroidEntity> {
    public RobotEntityModel(EntityRendererFactory.Context context) {
        super(context.getPart(EntityModelLayers.PLAYER), false);
    }

    @Override
    protected void animateArms(AndroidEntity entity, float animationProgress) {
        super.animateArms(entity, animationProgress);
    }
}
