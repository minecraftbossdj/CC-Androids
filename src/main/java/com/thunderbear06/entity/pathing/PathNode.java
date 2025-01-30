package com.thunderbear06.entity.pathing;

import net.minecraft.util.math.BlockPos;

public class PathNode {
    public PathNode previous;
    public int cost = Integer.MAX_VALUE;
    public BlockPos blockPos;


    public PathNode(BlockPos pos) {
        this.blockPos = pos;
    }
}
