package com.thunderbear06.entity.pathing;

import com.thunderbear06.CCAndroids;
import net.minecraft.block.*;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathFinder {
    private ServerWorld world;

    private final List<PathNode> open = new ArrayList<>();
    private final List<PathNode> closed = new ArrayList<>();

    private PathNode current;

    public List<PathNode> findPathFromTo(BlockPos start, BlockPos end, ServerWorld world) {
        this.world = world;
        this.open.clear();
        this.closed.clear();

        PathNode node = new PathNode(start);
        node.cost = getDistanceCost(start, node.blockPos, end);

        this.open.add(node);

        CCAndroids.LOGGER.info("Calculating path from {} to {}", start.toString(), end.toString());

        if (isImpassable(end)) {
            BlockPos adjusted = adjustTarget(end);
            if (end.equals(adjusted)) {
                CCAndroids.LOGGER.warn("Pathfinder failed to adjust end target {} to suitable location", end);
                return new ArrayList<>();
            }
            end = adjusted;
        }

        int maxIterations = 100;

        while (!this.open.isEmpty() && maxIterations-- > 0) {

            this.current = getLowestCostNode();

            if (this.current.blockPos.equals(end)) {
                // Found path
                CCAndroids.LOGGER.info("Found path");
                return getPathToNode(this.current);
            }

            this.open.remove(this.current);
            this.closed.add(this.current);

            getNextNode(start, end);
        }

        CCAndroids.LOGGER.warn("Pathfinder failed to find path to end target {}, returning path to closest node", end);

        return getPathToNode(getClosestNode(end));
    }

    private void getNextNode(BlockPos start, BlockPos end) {
        List<Direction> directions = Direction.stream().toList();

        for (Direction direction : directions) {
            BlockPos neighborPos = this.current.blockPos.offset(direction);

            PathNode neighbor = new PathNode(neighborPos);

            if (isIn(neighborPos, this.closed) || isImpassable(neighborPos))
                continue;
            if (!isIn(neighborPos, this.open))
                this.open.add(neighbor);

            int cost = getDistanceCost(start, neighborPos, end);
            neighbor.cost = cost;
            neighbor.previous = this.current;

            if (cost < this.current.cost)
                this.current = neighbor;
        }
    }

    private BlockPos adjustTarget(BlockPos target) {
        while (isImpassable(target)) {
            List<Direction> directions = Direction.stream().toList();
            for (Direction direction : directions) {
                if (this.world.getBlockState(target.offset(direction)).isAir())
                    return target.offset(direction);
            }
            target = target.up();
        }
        return target;
    }

    private boolean isImpassable(BlockPos pos) {
        return this.world.getBlockState(pos).getHardness(this.world, pos) == -1;
    }

    private boolean isIn(BlockPos pos, List<PathNode> list) {
        for (PathNode node : list) {
            if (node.blockPos.equals(pos))
                return true;
        }
        return false;
    }

    private List<PathNode> getPathToNode(PathNode node) {
        List<PathNode> path = new ArrayList<>();
        do{
            path.add(node);
            node = node.previous;
        } while (node.previous != null);
        Collections.reverse(path);
        return path;
    }

    private PathNode getLowestCostNode() {
        PathNode lowestNode = this.open.get(0);

        for (PathNode node : this.open) {
            if (node.cost < lowestNode.cost)
                lowestNode = node;
        }

        return lowestNode;
    }

    private PathNode getClosestNode(BlockPos target) {
        PathNode closest = this.current;
        double closestDist = this.current.blockPos.getSquaredDistance(target);

        for (PathNode node : this.closed) {
            double dist = node.blockPos.getSquaredDistance(target);
            if (dist < closestDist) {
                closest = node;
                closestDist = dist;
            }
        }
        return closest;
    }

    private int getDistanceCost(BlockPos start, BlockPos current, BlockPos end) {
        int gCost = (int) current.getSquaredDistance(start);
        int hCost = (int) current.getSquaredDistance(end);
        int bCost = getBlockCost(current);

        return (gCost + hCost) * bCost;
    }

    private int getBlockCost(BlockPos pos) {
        BlockState state = this.world.getBlockState(pos);
        BlockState belowState = this.world.getBlockState(pos.down());

        if (state.isAir()) {

            if (state.getFluidState().isIn(FluidTags.WATER) || belowState.getFluidState().isIn(FluidTags.WATER))
                return PathCosts.WATER.cost;
            if (isHarmfulFall(pos))
                return PathCosts.DANGER.cost;
            if (belowState.isAir())
                return PathCosts.NO_SUPPORT.cost;
            return PathCosts.OPEN.cost;
        }

        if (state.getFluidState().isIn(FluidTags.LAVA))
            return PathCosts.DANGER.cost;

        Block block = state.getBlock();
        if (block instanceof MagmaBlock || block instanceof SweetBerryBushBlock || block instanceof CactusBlock)
            return PathCosts.DANGER.cost;

        return (int) state.getHardness(this.world, pos);
    }

    private boolean isHarmfulFall(BlockPos pos) {
        for (int i = 1; i < 4; i++) {
            if (!this.world.getBlockState(pos.down(i)).isAir())
                return false;
        }
        return true;
    }
}
