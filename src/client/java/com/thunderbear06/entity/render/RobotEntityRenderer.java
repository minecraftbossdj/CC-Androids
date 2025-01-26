package com.thunderbear06.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.AndroidEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class RobotEntityRenderer extends BipedEntityRenderer<AndroidEntity, RobotEntityModel> {
    public RobotEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new RobotEntityModel(context), 0.5f);
    }

    @Override
    public Identifier getTexture(AndroidEntity entity) {
        return new Identifier(CCAndroids.MOD_ID, "textures/entity/robot.png");
    }
}
