package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.custom.AngledShopBlock;
import net.spudacious5705.shops.lootcondition.MatchingCushionColourCondition;
import net.spudacious5705.shops.properties.Colour;
import net.spudacious5705.shops.util.CushionResources;

import java.util.List;

import static net.spudacious5705.shops.block.ModBlocks.getAllShops;
import static net.spudacious5705.shops.block.ModBlocks.registerModBlocks;


public class ModLootTableProvider extends FabricBlockLootTableProvider {


    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {


        registerModBlocks();


        List<AngledShopBlock> allShops = getAllShops();
        for(AngledShopBlock shop : allShops){

            LootTable.Builder lootTable = LootTable.builder()
                    .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)));

            for(Colour colour: Colour.values()){

                Item itemDrop = shop.getColouredShopItem(colour);

                lootTable.pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .with(ItemEntry.builder(itemDrop))
                                .conditionally(new MatchingCushionColourCondition(colour.asString()))
                );
            }

            addDrop(shop,lootTable);
        }

    }

}
