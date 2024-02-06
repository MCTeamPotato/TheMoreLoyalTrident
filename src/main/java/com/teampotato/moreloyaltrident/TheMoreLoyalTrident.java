package com.teampotato.moreloyaltrident;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(TheMoreLoyalTrident.MOD_ID)
public class TheMoreLoyalTrident {
    public static final String MOD_ID = "moreloyaltrident";

    private static final ForgeConfigSpec CONFIG;
    public static final ForgeConfigSpec.IntValue X, Y, Z, LEVEL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("TheMoreLoyalTrident");
        builder.push("DetectionRange").comment("The loot detection processes centered on the dead, and here is its expanding range");
        X = builder.defineInRange("X", 5, 0, Integer.MAX_VALUE);
        Y = builder.defineInRange("Y", 5, 0, Integer.MAX_VALUE);
        Z = builder.defineInRange("Z", 5, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("EnchantmentAdaption");
        LEVEL = builder.defineInRange("RequiredLoyaltyLevel", 1, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.pop();
        CONFIG = builder.build();
    }

    public TheMoreLoyalTrident() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG);
    }
}
