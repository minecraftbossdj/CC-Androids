package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.AI.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class SensorModule extends AndroidModule{

    private final double entitySearchRadius;
    private final int blockSearchRadius;

    public SensorModule(BaseAndroidEntity android, AndroidBrain brain, double searchRadius, int blockSearchRadius) {
        super(android, brain);
        this.entitySearchRadius = searchRadius;
        this.blockSearchRadius = blockSearchRadius;
    }


    public PlayerEntity getClosestPlayer() {
        BlockPos pos = this.owner.getBlockPos();

        return this.owner.getWorld().getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), this.entitySearchRadius, entity -> !entity.isSpectator());
    }

    public List<HashMap<String, Object>> getMobs(@Nullable String type) throws LuaException {
        List<HashMap<String, Object>> result = new ArrayList<>();

        this.owner.getWorld().getEntitiesByClass(LivingEntity.class, this.owner.getBoundingBox().expand(this.entitySearchRadius), getTypePredicate(type)).forEach(entity -> {
            result.add(collectEntityInfo(entity));
        });

        return result;
    }

    public HashMap<String, Object> getClosestMobOfType(@Nullable String type) throws LuaException {
        BlockPos pos = this.owner.getBlockPos();

        Entity entity = this.owner.getWorld().getClosestEntity(
                LivingEntity.class,
                TargetPredicate.DEFAULT.setPredicate(getTypePredicate(type)),
                this.owner,
                pos.getX(),
                pos.getY(),
                pos.getX(),
                this.owner.getBoundingBox().expand(this.entitySearchRadius)
        );

        if (entity == null || entity instanceof LivingEntity livingEntity && livingEntity.isDead())
            return new HashMap<>();

        HashMap<String, Object> info = new HashMap<>();
        info.put("UUID", entity.getUuidAsString());
        info.putAll(collectEntityInfo(entity));
        return info;
    }

    public List<HashMap<String, Object>> getGroundItems(@Nullable String type, int max) {
        List<ItemEntity> list = this.owner.getWorld().getNonSpectatingEntities(ItemEntity.class, this.owner.getBoundingBox().expand(5));

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
            if (!Registries.BLOCK.getId(world.getBlockState(pos).getBlock()).toString().equals(type))
                continue;
            RaycastContext context = new RaycastContext(eyePos, pos.toCenterPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, this.owner);
            if (!world.raycast(context).getBlockPos().equals(pos))
                continue;
            blocks.add(new HashMap<>() {{put("x", pos.getX()); put("y", pos.getY()); put("z", pos.getZ());}} );
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

    private Predicate<LivingEntity> getTypePredicate(@Nullable String type) throws LuaException {
        if (type == null) {
            return (entity -> entity != this.owner && this.owner.canSee(entity));
        } else {
            Optional<EntityType<?>> targetType = EntityType.get(type);

            if (targetType.isEmpty()) {
                throw new LuaException("Unknown EntityType: "+type);
            }

            return (entity -> entity != this.owner && !entity.isSpectator() && this.owner.canSee(entity) && entity.getType().equals(targetType.get()));
        }
    }
}
