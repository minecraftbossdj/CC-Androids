package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.NewAndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class SensorModule extends AndroidModule{

    private final double entitySearchRadius;
    private final int blockSearchRadius;

    public SensorModule(BaseAndroidEntity android, NewAndroidBrain brain, double searchRadius, int blockSearchRadius) {
        super(android, brain);
        this.entitySearchRadius = searchRadius;
        this.blockSearchRadius = blockSearchRadius;
    }

    public List<HashMap<String, Object>> getMobs(@Nullable String type) {
        List<HashMap<String, Object>> result = new ArrayList<>();

        this.android.getWorld().getEntitiesByClass(LivingEntity.class, this.android.getBoundingBox().expand(this.entitySearchRadius), getTypePredicate(type)).forEach(entity -> {
            result.add(collectEntityInfo(entity));
        });

        return result;
    }

    public HashMap<String, Object> getClosestMobOfType(@Nullable String type) {
        BlockPos pos = this.android.getBlockPos();

        Entity entity = this.android.getWorld().getClosestEntity(
                LivingEntity.class,
                TargetPredicate.DEFAULT.setPredicate(getTypePredicate(type)),
                this.android,
                pos.getX(),
                pos.getY(),
                pos.getX(),
                this.android.getBoundingBox().expand(this.entitySearchRadius)
        );

        if (entity == null || entity instanceof LivingEntity livingEntity && livingEntity.isDead())
            return new HashMap<>();

        HashMap<String, Object> info = new HashMap<>();
        info.put("UUID", entity.getUuidAsString());
        info.putAll(collectEntityInfo(entity));
        return info;
    }

    public HashMap<String, Object> getClosestPlayer() {
        ServerPlayerEntity player = (ServerPlayerEntity) this.android.getWorld().getClosestPlayer(this.android, 100);

        if (player == null)
            return new HashMap<>();

        return collectEntityInfo(player);
    }

    public List<HashMap<String, Object>> getGroundItem(@Nullable String type, int max) {
        List<ItemEntity> list = this.android.getWorld().getNonSpectatingEntities(ItemEntity.class, this.android.getBoundingBox().expand(5));
        List<HashMap<String, Object>> results = new ArrayList<>();

        for (ItemEntity entity : list) {
            if (results.size() >= max)
                break;
            if (type == null || entity.getStack().getItem().getName().getString().equals(type)) {
                results.add(collectEntityInfo(entity));
            }
        }

        return results;
    }

    public List<HashMap<String, Integer>> getBlocksOfType(BlockPos origin, Vec3d eyePos, World world, String type) {

        List<HashMap<String, Integer>> blocks = new ArrayList<>();

        for (BlockPos pos : BlockPos.iterateOutwards(origin, this.blockSearchRadius, this.blockSearchRadius, this.blockSearchRadius)) {
            if (!Registries.BLOCK.getId(world.getBlockState(pos).getBlock()).toString().contains(type))
                continue;

            for (Direction direction : Direction.stream().toList()) {
                if (world.getBlockState(pos.offset(direction)).isSolidBlock(world, pos.offset(direction)))
                    continue;

                RaycastContext context = new RaycastContext(eyePos, pos.offset(direction).toCenterPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, this.android);
                if (!world.raycast(context).getBlockPos().equals(pos.offset(direction)))
                    continue;

                blocks.add(new HashMap<>() {{put("x", pos.getX()); put("y", pos.getY()); put("z", pos.getZ());}} );
                break;
            }
        }

        return blocks;
    }

    private HashMap<String, Object> collectEntityInfo(Entity entity) {
        HashMap<String, Object> infoMap = new HashMap<>();

        infoMap.put("uuid", entity.getUuidAsString());
        infoMap.put("name", entity.getName().getString());
        infoMap.put("posX", entity.getX());
        infoMap.put("posY", entity.getY());
        infoMap.put("posZ", entity.getZ());
        if (entity instanceof LivingEntity livingEntity) {
            infoMap.put("health", livingEntity.getHealth());
        }

        return infoMap;
    }

    private Predicate<LivingEntity> getTypePredicate(@Nullable String type) {
        if (type == null) {
            return (entity -> entity != this.android && this.android.canSee(entity));
        } else {
            return (entity -> {
                return EntityType.getId(entity.getType()).toString().contains(type)
                        && entity != this.android
                        && !entity.isSpectator()
                        && entity.isAlive()
                        && this.android.canSee(entity);
            });
        }
    }
}
