package com.thunderbear06;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.computer.api.AndroidAPI;
import com.thunderbear06.entity.EntityRegistry;
import com.thunderbear06.item.ItemRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCAndroids implements ModInitializer {
	public static final String MOD_ID = "cc-androids";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ComputerCraftAPI.registerAPIFactory(computer -> {
			NewAndroidBrain brain = computer.getComponent(ComputerComponents.ANDROID_COMPUTER);
			return brain == null ? null : new AndroidAPI(brain);
		});

		ItemRegistry.register();
		EntityRegistry.register();
	}
}