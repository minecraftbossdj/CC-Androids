package com.thunderbear06.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.AndroidEntity;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class AndroidEntityRenderer extends BipedEntityRenderer<AndroidEntity, PlayerEntityModel<AndroidEntity>> {
    private final Identifier androidNormal = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_normal.png");
    private final Identifier androidAdvanced = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_advanced.png");
    private final Identifier androidCommand = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_command.png");

    public AndroidEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public Identifier getTexture(AndroidEntity entity) {
        if (entity.computerContainer.getFamily() == ComputerFamily.ADVANCED)
            return androidAdvanced;
        if (entity.computerContainer.getFamily() == ComputerFamily.COMMAND)
            return androidCommand;
        return androidNormal;
    }
}
