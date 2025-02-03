package com.thunderbear06.entity.render;

import com.thunderbear06.entity.android.UnfinishedAndroidEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class UnfinishedAndroidEntityModel extends PlayerEntityModel<UnfinishedAndroidEntity> {
    public UnfinishedAndroidEntityModel(ModelPart root) {
        super(root, false);
    }

    @Override
    public void animateModel(UnfinishedAndroidEntity livingEntity, float f, float g, float h) {
        byte comps = livingEntity.components_needed;

        this.leftArm.visible = comps < 14;
        this.rightArm.visible = comps < 12;
        this.leftSleeve.visible = comps < 10;
        this.rightSleeve.visible = comps < 8;
        this.leftPants.visible = comps < 6;
        this.rightPants.visible = comps < 4;
        this.hat.visible = comps < 2;

        super.animateModel(livingEntity, f, g, h);
    }

    @Override
    public void setAngles(UnfinishedAndroidEntity livingEntity, float f, float g, float h, float i, float j) {}
}
