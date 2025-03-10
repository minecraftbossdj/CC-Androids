package com.thunderbear06.menu;

import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.platform.RegistrationHelper;
import dan200.computercraft.shared.platform.RegistryEntry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;

public class MenuRegistryAndroid {
    private static final RegistrationHelper<ScreenHandlerType<?>> REGISTRY = PlatformHelper.get().createRegistrationHelper(RegistryKeys.SCREEN_HANDLER);

    public static final RegistryEntry<ScreenHandlerType<AndroidMenu>> ANDROID = REGISTRY.register("android",
            () -> ContainerData.toType(ComputerContainerData::new, AndroidMenu::ofData));

    public static void register() {
        REGISTRY.register();
    }
}
