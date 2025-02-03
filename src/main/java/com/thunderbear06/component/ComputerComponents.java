package com.thunderbear06.component;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.computer.IAndroidAccess;
import dan200.computercraft.api.component.ComputerComponent;

public class ComputerComponents {
    public static final ComputerComponent<IAndroidAccess> ANDROID_COMPUTER = ComputerComponent.create(CCAndroids.MOD_ID, "android_computer");
}
