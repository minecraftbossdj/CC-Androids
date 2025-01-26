package com.thunderbear06;

import com.thunderbear06.entity.render.RobotEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class CCAndroidsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(CCAndroids.ROBOT_ENTITY, (RobotEntityRenderer::new));
	}
}