package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.entity.BaseAndroidEntity;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class SensorModule {

    private final double searchRadius;

    public SensorModule(double searchRadius) {
        this.searchRadius = searchRadius;
    }


    public PlayerEntity getClosestPlayer(BaseAndroidEntity android) {
        BlockPos pos = android.getBlockPos();

        return android.getWorld().getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), this.searchRadius, entity -> !entity.isSpectator());
    }

    public List<String> getMobs(@Nullable String type, BaseAndroidEntity android) throws LuaException {
        List<String> list = new ArrayList<>();

        android.getWorld().getEntitiesByClass(LivingEntity.class, android.getBoundingBox().expand(this.searchRadius), getTypePredicate(android, type)).forEach(entity -> {
            list.add(entity.getUuidAsString());
        });

        return list;
    }

    public LivingEntity getClosestMobOfType(@Nullable String type, BaseAndroidEntity android) throws LuaException {
        BlockPos pos = android.getBlockPos();

        return android.getWorld().getClosestEntity(
                LivingEntity.class,
                TargetPredicate.DEFAULT.setPredicate(getTypePredicate(android, type)),
                android,
                pos.getX(),
                pos.getY(),
                pos.getX(),
                android.getBoundingBox().expand(this.searchRadius)
        );
    }

    public List<String> getGroundItems(@Nullable String itemName, int max, BaseAndroidEntity android) {
        List<ItemEntity> list = android.getWorld().getNonSpectatingEntities(ItemEntity.class, android.getBoundingBox().expand(1));

        List<String> UUIDS = new ArrayList<>();

        for (ItemEntity entity : list) {
            if (UUIDS.size() >= max)
                return UUIDS;
            if (itemName == null || entity.getName().getString().equals(itemName))
                UUIDS.add(entity.getUuidAsString());
        }

        return UUIDS;
    }

    private Predicate<LivingEntity> getTypePredicate(BaseAndroidEntity android, @Nullable String type) throws LuaException {
        if (type == null) {
            return (entity -> entity != android && android.canSee(entity));
        } else {
            Optional<EntityType<?>> targetType = EntityType.get(type);

            if (targetType.isEmpty()) {
                throw new LuaException("Unknown EntityType: "+type);
            }

            return (entity -> entity != android && !entity.isSpectator() && android.canSee(entity) && entity.getType().equals(targetType.get()));
        }
    }
}
