package com.thunderbear06;

import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.entity.render.AndroidEntityRenderer;
import com.thunderbear06.entity.render.AndroidFrameEntityRenderer;
import com.thunderbear06.entity.render.RogueAndroidEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class CCAndroidsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(EntityRegistry.ANDROID_ENTITY, (AndroidEntityRenderer::new));
		EntityRendererRegistry.register(EntityRegistry.ADVANCED_ANDROID_ENTITY, (AndroidEntityRenderer::new));
		EntityRendererRegistry.register(EntityRegistry.COMMAND_ANDROID_ENTITY, (AndroidEntityRenderer::new));
		EntityRendererRegistry.register(EntityRegistry.ROGUE_ANDROID_ENTITY, (RogueAndroidEntityRenderer::new));
		EntityRendererRegistry.register(EntityRegistry.UNFINISHED_ANDROID_ENTITY, (AndroidFrameEntityRenderer::new));
	}
}