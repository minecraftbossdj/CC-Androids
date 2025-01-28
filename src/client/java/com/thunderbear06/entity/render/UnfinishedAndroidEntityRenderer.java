package com.thunderbear06.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.RogueDroidEntity;
import com.thunderbear06.entity.android.UnfinishedAndroidEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class UnfinishedAndroidEntityRenderer extends BipedEntityRenderer<UnfinishedAndroidEntity, UnfinishedAndroidEntityModel> {
    private final Identifier androidUnfinished = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_unfinished.png");

    public UnfinishedAndroidEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new UnfinishedAndroidEntityModel(ctx.getPart(EntityModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(UnfinishedAndroidEntity entity) {
        return androidUnfinished;
    }
}
