package net.spudacious5705.shops.lootcondition;

import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.spudacious5705.shops.SpudaciousShops;

public class ModLootConditions {
    public static final LootConditionType MATCHES_ENUM = registerCondition(new MatchingCushionColourCondition.Serializer());

    private static LootConditionType registerCondition(JsonSerializer<MatchingCushionColourCondition> serializer) {
        LootConditionType conditionType = new LootConditionType(serializer);

        Registry.register(Registries.LOOT_CONDITION_TYPE, SpudaciousShops.id("matches_colour"), conditionType);
        return conditionType;
    }

    public static void registerLootConditions() {
        SpudaciousShops.LOGGER.info("Registering mod loot table conditions for " + SpudaciousShops.MOD_ID);
    }
}

