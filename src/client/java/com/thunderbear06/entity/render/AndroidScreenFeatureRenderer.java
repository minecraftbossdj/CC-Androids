package com.thunderbear06.entity.render;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AndroidScreenFeatureRenderer extends FeatureRenderer<AndroidEntity, PlayerEntityModel<AndroidEntity>> {

    public AndroidScreenFeatureRenderer(FeatureRendererContext<AndroidEntity, PlayerEntityModel<AndroidEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AndroidEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.getEyesTexture(entity));
        this.getContextModel().render(matrices, vertexConsumer, 1, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public abstract RenderLayer getEyesTexture(AndroidEntity entity);
}
