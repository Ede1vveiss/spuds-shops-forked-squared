package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;


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
