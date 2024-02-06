package com.teampotato.moreloyaltrident.mixin;

import com.teampotato.moreloyaltrident.api.LootCarrier;
import com.teampotato.moreloyaltrident.api.LoyalChecker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique private final ThreadLocal<UUID> moreLoyalTrident$tridentOnDeath = new ThreadLocal<>();

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void moreLoyalTrident$checkTrident(DamageSource damageSource, CallbackInfo ci) {
        if (!(this.level instanceof ServerLevel)) return;
        UUID entityUUID = moreLoyalTrident$getDeathTrident(damageSource.getDirectEntity());
        if (entityUUID == null) entityUUID = moreLoyalTrident$getDeathTrident(damageSource.getEntity());
        this.moreLoyalTrident$tridentOnDeath.set(entityUUID);
    }

    @Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
    private void moreLoyalTrident$invalidateTrident(DamageSource damageSource, CallbackInfo ci) {
        this.moreLoyalTrident$tridentOnDeath.remove();
    }

    @Unique
    private static UUID moreLoyalTrident$getDeathTrident(Entity entity) {
        if (entity instanceof ThrownTrident && ((LoyalChecker) entity).moreLoyalTrident$canCatchLoot()) return entity.getUUID();
        return null;
    }

    @Dynamic
    @ModifyArg(method = {"lambda$dropAllDeathLoot$5", "method_16080", "func_213345_d"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private Entity moreLoyalTrident$carryItem(Entity item) {
        return moreLoyalTrident$carryLoot(item);
    }

    @ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private Entity moreLoyalTrident$carryExpOrb(Entity expOrb) {
        return moreLoyalTrident$carryLoot(expOrb);
    }

    @Unique
    private Entity moreLoyalTrident$carryLoot(Entity entity) {
        final UUID tridentUUID = this.moreLoyalTrident$tridentOnDeath.get();
        if (tridentUUID != null) {
            final LootCarrier lootCarrier = (LootCarrier) ((ServerLevel) this.level).getEntity(tridentUUID);
            if (lootCarrier != null) lootCarrier.moreLoyalTrident$carryLoot(entity.getUUID());
        }
        return entity;
    }
}
