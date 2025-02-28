package com.thunderbear06.entity.render;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.AndroidEntity;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class AndroidEntityRenderer extends BipedEntityRenderer<AndroidEntity, PlayerEntityModel<AndroidEntity>> {
    private final Identifier androidNormal = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_normal.png");
    private final Identifier androidAdvanced = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_advanced.png");
    private final Identifier androidCommand = new Identifier(CCAndroids.MOD_ID, "textures/entity/android_command.png");
    private final Identifier androidNormalE = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/android_normal_e.png");
    private final Identifier androidAdvancedE = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/android_advanced_e.png");
    private final Identifier androidCommandE = new Identifier(CCAndroids.MOD_ID, "textures/entity/emissive/android_command_e.png");

    public AndroidEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false), 0.5f);
        this.addFeature(new AndroidScreenFeatureRenderer(this) {
            @Override
            public RenderLayer getEyesTexture(AndroidEntity entity) {
                if (entity.hasVariant())
                    return RenderLayer.getEyes(getVariantTexture(entity.getVariant(), true));

                ComputerFamily family = entity.getComputer().getFamily();

                return RenderLayer.getEyes(switch (family) {
                    case NORMAL -> androidNormalE;
                    case ADVANCED -> androidAdvancedE;
                    case COMMAND -> androidCommandE;
                });
            }
        });
    }

    @Override
    public Identifier getTexture(AndroidEntity entity) {
        if (entity.hasVariant())
            return getVariantTexture(entity.getVariant(), false);

        ComputerFamily family = entity.getComputer().family;
        if (family == ComputerFamily.ADVANCED)
            return androidAdvanced;
        if (family == ComputerFamily.COMMAND)
            return androidCommand;
        return androidNormal;
    }

    private Identifier getVariantTexture(byte b, boolean emissive) {
        String path = emissive ? "textures/entity/emissive/variant/" : "textures/entity/variant/";

        String name = switch (b) {
            case 1 -> "android_kaylon.png";
            case 2 -> "android_pinky.png";
            default -> throw new IllegalArgumentException();
        };

        return new Identifier(CCAndroids.MOD_ID,path+name);
    }
}
