package com.thunderbear06;

import com.thunderbear06.entity.render.AndroidEntityRenderer;
import com.thunderbear06.entity.render.RogueAndroidEntityRenderer;
import com.thunderbear06.entity.render.UnfinishedAndroidEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class CCAndroidsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(CCAndroids.ANDROID_ENTITY, (AndroidEntityRenderer::new));
		EntityRendererRegistry.register(CCAndroids.ROGUE_ANDROID_ENTITY, (RogueAndroidEntityRenderer::new));
		EntityRendererRegistry.register(CCAndroids.UNFINISHED_ANDROID_ENTITY, (UnfinishedAndroidEntityRenderer::new));
	}
}