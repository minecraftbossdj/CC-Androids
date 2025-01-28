package com.thunderbear06.entity.render;

import com.thunderbear06.entity.android.UnfinishedAndroidEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class UnfinishedAndroidEntityModel extends BipedEntityModel<UnfinishedAndroidEntity> {
    public UnfinishedAndroidEntityModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setAngles(UnfinishedAndroidEntity livingEntity, float f, float g, float h, float i, float j) {}
}
