package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryWrapper;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.ModBlocks.*;
import net.spudacious5705.shops.block.custom.ShopBlock;
import net.spudacious5705.shops.item.ModItems;

import java.util.concurrent.CompletableFuture;


public class ModLootTableProvider extends FabricBlockLootTableProvider {


    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        //addDrop(ModBlocks.SHOP_BLOCK_JUNGLE, block -> );
        /*addDrop(ModBlocks.SHOP_BLOCK_OAK, LootTable.builder().pool(addSurvivesExplosionCondition(Items.OAK_LOG, LootPool.builder()
                .rolls(new UniformLootNumberProvider(new ConstantLootNumberProvider(7), new ConstantLootNumberProvider(9)))
                .with(ItemEntry.builder(Items.OAK_LOG))));*/

        //BlockLootTableGenerator.drops()

    }

}
