package com.teampotato.moreloyaltrident.mixin;

import com.teampotato.moreloyaltrident.TheMoreLoyalTrident;
import com.teampotato.moreloyaltrident.api.LootCarrier;
import com.teampotato.moreloyaltrident.api.LoyalChecker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private final ThreadLocal<Entity> moreLoyalTrident$tridentOnDeath = new ThreadLocal<>();

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void checkTrident(DamageSource damageSource, CallbackInfo ci) {
        Entity entity = moreLoyalTrident$tridentOnDeath(damageSource.getDirectEntity());
        if (entity == null) entity = moreLoyalTrident$tridentOnDeath(damageSource.getEntity());
        this.moreLoyalTrident$tridentOnDeath.set(entity);
    }

    @Unique
    private static @Nullable Entity moreLoyalTrident$tridentOnDeath(Entity entity) {
        if (entity instanceof ThrownTrident && ((LoyalChecker) entity).moreLoyalTrident$canCatchLoot()) {
            return entity;
        }
        return null;
    }

    @Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
    private void removeThreadLocal(DamageSource damageSource, CallbackInfo ci) {
        final ThrownTrident trident = (ThrownTrident) this.moreLoyalTrident$tridentOnDeath.get();
        if (trident != null) this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().expandTowards(TheMoreLoyalTrident.X.get(), TheMoreLoyalTrident.Y.get(), TheMoreLoyalTrident.Z.get()))
                .forEach(e -> {
                    if (e instanceof ItemEntity || e instanceof ExperienceOrb) {
                        ((LootCarrier) trident).moreLoyalTrident$carryLoot(e);
                    }
                });
        this.moreLoyalTrident$tridentOnDeath.remove();
    }
}
