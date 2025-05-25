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
        ModBlocks.registerModBlocks();
        ModBlocks.getAllShops().forEach(builder::add);
    }
}
