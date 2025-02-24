package com.thunderbear06.entity.model;

import com.thunderbear06.entity.android.AndroidFrame;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class AndroidFrameEntityModel extends PlayerEntityModel<AndroidFrame> {
    public AndroidFrameEntityModel(ModelPart root) {
        super(root, false);
    }

    @Override
    public void animateModel(AndroidFrame livingEntity, float f, float g, float h) {
        int comps = livingEntity.getComponentsNeeded();
        int ingots = livingEntity.getIngotsNeeded();

        this.leftArm.visible = comps < 8;
        this.rightArm.visible = comps < 6;
        this.hat.visible = comps < 4;
        this.jacket.visible = ingots < 10;
        this.leftSleeve.visible = ingots < 8;
        this.rightSleeve.visible = ingots < 6;
        this.leftPants.visible = ingots < 4;
        this.rightPants.visible = ingots < 2;

        super.animateModel(livingEntity, f, g, h);
    }

    @Override
    public void setAngles(AndroidFrame livingEntity, float f, float g, float h, float i, float j) {}
}
