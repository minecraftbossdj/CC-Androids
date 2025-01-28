package com.thunderbear06.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.RogueDroidEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class RogueAndroidEntityRenderer extends BipedEntityRenderer<RogueDroidEntity, PlayerEntityModel<RogueDroidEntity>> {
    private final Identifier androidRogue = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_rogue.png");

    public RogueAndroidEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public Identifier getTexture(RogueDroidEntity entity) {
        return androidRogue;
    }
}
