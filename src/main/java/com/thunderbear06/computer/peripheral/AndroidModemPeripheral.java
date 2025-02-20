package com.thunderbear06.computer.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.modem.ModemState;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemPeripheral;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AndroidModemPeripheral extends WirelessModemPeripheral {
    public AndroidModemPeripheral(ModemState state, boolean advanced) {
        super(state, advanced);
    }

    @Override
    public World getLevel() {
        return null;
    }

    @Override
    public Vec3d getPosition() {
        return null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return false;
    }
}
