package com.teampotato.moreloyaltrident.api;

import net.minecraft.world.entity.Entity;

import java.util.Set;

public interface LootCarrier {
    void moreLoyalTrident$carryLoot(Entity lootEntity);
    Set<Entity> moreLoyalTrident$getCarriedLoots();
}
