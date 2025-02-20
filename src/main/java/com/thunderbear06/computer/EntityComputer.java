package com.thunderbear06.computer;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.util.ComponentMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EntityComputer extends ServerComputer {
    private final BaseAndroidEntity entity;

    public EntityComputer(ServerWorld level, BaseAndroidEntity entity, int computerID, @Nullable String label, ComputerFamily family, int terminalWidth, int terminalHeight, ComponentMap baseComponents) {
        super(level, entity.getBlockPos(), computerID, label, family, terminalWidth, terminalHeight, baseComponents);
        this.entity = entity;
    }

    @Override
    public BlockPos getPosition() {
        return entity.getBlockPos();
    }
}
