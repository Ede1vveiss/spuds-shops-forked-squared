package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.spudacious5705.shops.block.ModBlockTags;
import net.spudacious5705.shops.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        addShops(getOrCreateTagBuilder(BlockTags.AXE_MINEABLE));
        addShops(getOrCreateTagBuilder(ModBlockTags.SPUDS_SHOPS));
    }

    private void addShops(FabricTagBuilder builder) {
        builder
                .add(ModBlocks.SHOP_BLOCK_ANGLED_ACACIA)
                .add(ModBlocks.SHOP_BLOCK_ANGLED_BAMBOO)
                .add(ModBlocks.SHOP_BLOCK_ANGLED_BIRCH)
                .add(ModBlocks.SHOP_BLOCK_ANGLED_CHERRY)
                .add(ModBlocks.SHOP_BLOCK_ANGLED_CRIMSON)
                .add(ModBlocks.SHOP_BLOCK_ANGLED_DARK_OAK)
                .add(ModBlocks.SHOP_BLOCK_ANGLED_MANGROVE)
                .add(ModBlocks.SHOP_BLOCK_ANGLED_OAK)
                .add(ModBlocks.SHOP_BLOCK_ANGLED_SPRUCE)
                .add(ModBlocks.SHOP_BLOCK_ANGLED_WARPED);
    }
}
