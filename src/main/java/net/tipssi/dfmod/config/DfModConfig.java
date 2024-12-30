package net.tipssi.dfmod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class DfModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG;

    public static final ForgeConfigSpec.IntValue STAMINA_CONSUMPTION_ATTACK;
    public static final ForgeConfigSpec.IntValue STAMINA_CONSUMPTION_SWING;
    public static final ForgeConfigSpec.IntValue STAMINA_CONSUMPTION_BLOCK;
    public static final ForgeConfigSpec.IntValue STAMINA_CONSUMPTION_HAND; // Added this
    public static final ForgeConfigSpec.DoubleValue STAMINA_MODIFIER_LOW_ATTACK_SPEED;
    public static final ForgeConfigSpec.DoubleValue STAMINA_MODIFIER_NORMAL_ATTACK_SPEED;
    public static final ForgeConfigSpec.DoubleValue STAMINA_MODIFIER_HIGH_ATTACK_SPEED;
    public static final ForgeConfigSpec.DoubleValue EXHAUSTED_DAMAGE_REDUCTION;

    static {
        // Stamina consumption settings
        BUILDER.comment("Stamina Consumption Settings").push("stamina");

        STAMINA_CONSUMPTION_ATTACK = BUILDER
                .comment("Stamina consumed per attack")
                .defineInRange("staminaConsumptionAttack", 275, 0, 10000);

        STAMINA_CONSUMPTION_SWING = BUILDER
                .comment("Stamina consumed per swing")
                .defineInRange("staminaConsumptionSwing", 275, 0, 10000);

        STAMINA_CONSUMPTION_BLOCK = BUILDER
                .comment("Stamina consumed per block break")
                .defineInRange("staminaConsumptionBlock", 3, 0, 10000);

        STAMINA_CONSUMPTION_HAND = BUILDER
                .comment("Stamina consumed for hand or non-weapon items") // New configuration
                .defineInRange("staminaConsumptionHand", 10, 0, 100);

        BUILDER.pop();

        // Stamina modifier settings
        BUILDER.comment("Stamina Modifier Settings").push("modifiers");

        STAMINA_MODIFIER_LOW_ATTACK_SPEED = BUILDER
                .comment("Stamina multiplier for attack speed below 1.5")
                .defineInRange("staminaModifierLowAttackSpeed", 1.5, 0.0, 10.0);

        STAMINA_MODIFIER_NORMAL_ATTACK_SPEED = BUILDER
                .comment("Stamina multiplier for attack speed between 1.5 and 1.9")
                .defineInRange("staminaModifierNormalAttackSpeed", 1.0, 0.0, 10.0);

        STAMINA_MODIFIER_HIGH_ATTACK_SPEED = BUILDER
                .comment("Stamina multiplier for attack speed above 1.9")
                .defineInRange("staminaModifierHighAttackSpeed", 0.5, 0.0, 10.0);

        BUILDER.pop();

        // Damage reduction settings
        BUILDER.comment("Damage Reduction Settings").push("damage");

        EXHAUSTED_DAMAGE_REDUCTION = BUILDER
                .comment("Damage multiplier when stamina is exhausted (e.g., 0.25 for 75% reduction)")
                .defineInRange("exhaustedDamageReduction", 0.35, 0.0, 1.0);

        BUILDER.pop();

        CONFIG = BUILDER.build();
    }

    public static void registerConfig() {
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                CONFIG,
                "AdventurersAwaken/AdventurersAwaken.toml"
        );
    }
}
