package net.spudacious5705.shops.lootcondition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonSerializer;
import net.spudacious5705.shops.block.entity.AngledShopEntity;
import org.spongepowered.include.com.google.common.collect.ImmutableSet;

import java.util.Set;

public class MatchingCushionColourCondition implements LootCondition {
    private final String expectedColourName;

    public MatchingCushionColourCondition(String expectedColourName) {
        this.expectedColourName = expectedColourName;
    }

    @Override
    public LootConditionType getType() {
        return ModLootConditions.MATCHES_ENUM;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.BLOCK_ENTITY);
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockEntity blockEntity = lootContext.get(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof AngledShopEntity shop) {
            boolean v = shop.getCushionColour().matchesString(expectedColourName);
            return v;
        }
        return false;
    }

    public static LootCondition.Builder builder(String colourName) {
        return () -> new MatchingCushionColourCondition(colourName);
    }

    public static class Serializer implements JsonSerializer<MatchingCushionColourCondition> {
        @Override
        public void toJson(JsonObject json, MatchingCushionColourCondition condition, JsonSerializationContext context) {
            json.addProperty("expected_colour", condition.expectedColourName);
        }

        @Override
        public MatchingCushionColourCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            String colourName = json.get("expected_colour").getAsString();
            return new MatchingCushionColourCondition(colourName);
        }
    }
}
