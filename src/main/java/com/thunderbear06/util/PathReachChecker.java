package com.thunderbear06.util;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class PathReachChecker {

    public static BlockPos getClosestPosition(BlockPos origin, BlockPos target, ServerWorld world) {
        BlockPos current = origin;

        int lives = 10;

        while (lives > 0) {
            List<BlockPos> check = new ArrayList<>();

            for (Direction direction : Direction.stream().toList()) {
                check.add(current.offset(direction));
                check.add(current.offset(direction).up());
                check.add(current.offset(direction).down());
            }

            BlockPos next = null;

            for (BlockPos checkBlock : check) {
                if (checkBlock.getSquaredDistance(target) < current.getSquaredDistance(target) && isTraversable(checkBlock, world))
                    next = checkBlock;
            }

            if (next == null)
                lives--;
            else
                current = next;
        }

        return current;
    }

    private static boolean isTraversable(BlockPos pos, ServerWorld world) {
        if (world.getBlockState(pos).isSolidBlock(world, pos))
            return false;
        if (!world.getBlockState(pos.down()).isSolidBlock(world, pos.down()))
            return false;
        return !world.getBlockState(pos.up()).isSolidBlock(world, pos.up());
    }
}
