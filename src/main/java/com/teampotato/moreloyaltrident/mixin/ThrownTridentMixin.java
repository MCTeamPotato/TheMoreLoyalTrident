package com.teampotato.moreloyaltrident.mixin;

import com.teampotato.moreloyaltrident.TheMoreLoyalTrident;
import com.teampotato.moreloyaltrident.api.LootCarrier;
import com.teampotato.moreloyaltrident.api.LoyalChecker;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin extends AbstractArrow implements LoyalChecker, LootCarrier {
    @Shadow @Final private static EntityDataAccessor<Byte> ID_LOYALTY;

    @Unique private final ObjectSet<Entity> moreLoyalTrident$carriedItems = new ObjectOpenHashSet<>();
    @Unique private int moreLoyalTrident$tickLoyal;

    protected ThrownTridentMixin(EntityType<? extends AbstractArrow> arg, Level arg2) {
        super(arg, arg2);
    }

    @Override
    public boolean moreLoyalTrident$canCatchLoot() {
        return this.entityData.get(ID_LOYALTY) >= TheMoreLoyalTrident.LEVEL.get().byteValue();
    }

    @Override
    public void moreLoyalTrident$carryLoot(Entity itemEntity) {
        synchronized (this.moreLoyalTrident$carriedItems) {
            this.moreLoyalTrident$carriedItems.add(itemEntity);
        }
    }

    @Override
    public Set<Entity> moreLoyalTrident$getCarriedLoots() {
        return this.moreLoyalTrident$carriedItems;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void carryItems(CallbackInfo ci) {
        if (this.moreLoyalTrident$tickLoyal % 5 == 0) {
            this.moreLoyalTrident$tickLoyal = 0;
            this.moreLoyalTrident$getCarriedLoots().forEach(itemEntity -> itemEntity.teleportTo(this.getX(), this.getY(), this.getZ()));
        }
        this.moreLoyalTrident$tickLoyal = this.moreLoyalTrident$tickLoyal + 1;
    }

    @Inject(method = "playerTouch", at = @At("TAIL"))
    private void carryItems(Player player, CallbackInfo ci) {
        this.moreLoyalTrident$getCarriedLoots().forEach(itemEntity -> itemEntity.teleportTo(this.getX(), this.getY(), this.getZ()));
        this.moreLoyalTrident$getCarriedLoots().clear();
    }
}
