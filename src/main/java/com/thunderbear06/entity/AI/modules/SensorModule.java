package com.thunderbear06.entity.AI.modules;

import com.thunderbear06.entity.AI.AndroidBrain;
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

public class SensorModule extends AndroidModule{

    private final double searchRadius;

    public SensorModule(BaseAndroidEntity android, AndroidBrain brain, double searchRadius) {
        super(android, brain);
        this.searchRadius = searchRadius;
    }


    public PlayerEntity getClosestPlayer() {
        BlockPos pos = this.owner.getBlockPos();

        return this.owner.getWorld().getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), this.searchRadius, entity -> !entity.isSpectator());
    }

    public List<String> getMobs(@Nullable String type) throws LuaException {
        List<String> list = new ArrayList<>();

        this.owner.getWorld().getEntitiesByClass(LivingEntity.class, this.owner.getBoundingBox().expand(this.searchRadius), getTypePredicate(type)).forEach(entity -> {
            list.add(entity.getUuidAsString());
        });

        return list;
    }

    public LivingEntity getClosestMobOfType(@Nullable String type) throws LuaException {
        BlockPos pos = this.owner.getBlockPos();

        return this.owner.getWorld().getClosestEntity(
                LivingEntity.class,
                TargetPredicate.DEFAULT.setPredicate(getTypePredicate(type)),
                this.owner,
                pos.getX(),
                pos.getY(),
                pos.getX(),
                this.owner.getBoundingBox().expand(this.searchRadius)
        );
    }

    public List<String> getGroundItems(@Nullable String itemName, int max) {
        List<ItemEntity> list = this.owner.getWorld().getNonSpectatingEntities(ItemEntity.class, this.owner.getBoundingBox().expand(1));

        List<String> UUIDS = new ArrayList<>();

        for (ItemEntity entity : list) {
            if (UUIDS.size() >= max)
                return UUIDS;
            if (itemName == null || entity.getName().getString().equals(itemName))
                UUIDS.add(entity.getUuidAsString());
        }

        return UUIDS;
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
