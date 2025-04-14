package net.spudacious5705.shops.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.*;
import net.spudacious5705.shops.item.custom.ShopItem;
import net.spudacious5705.shops.properties.Colour;

public class ModBlocks {

    private static final AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .nonOpaque()
            .resistance(Float.MAX_VALUE);

    public static final ShopBlock SHOP_BLOCK_ACACIA = registerShopBlock("shop_acacia",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_BAMBOO = registerShopBlock("shop_bamboo",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_BIRCH = registerShopBlock("shop_birch",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_CHERRY = registerShopBlock("shop_cherry",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_CRIMSON = registerShopBlock("shop_crimson",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_DARK_OAK = registerShopBlock("shop_dark_oak",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_MANGROVE = registerShopBlock("shop_mangrove",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_OAK = registerShopBlock("shop_oak",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_SPRUCE = registerShopBlock("shop_spruce",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_WARPED = registerShopBlock("shop_warped",
            new ShopBlock(settings));
    public static final ShopBlock SHOP_BLOCK_JUNGLE = registerShopBlock("shop_jungle",
            new ShopBlock(settings));

    private static <T extends ShopBlock> T registerShopBlock(String name, T block) {
        /*for(Colour colour : Colour.values()) {
            registerShopBlockItem(name+"_"+colour.name(), block, colour);
        }*/
        registerBlockItem(name,block);
        return Registry.register(Registries.BLOCK, Identifier.of(SpudaciousShops.MOD_ID, name), block);
    }

    private static void registerShopBlockItem(String name, ShopBlock block, Colour colour) {
        Registry.register(Registries.ITEM, Identifier.of(SpudaciousShops.MOD_ID, name),
                new ShopItem(block, new Item.Settings(), colour));
    }

    private static void registerBlockItem(String name, ShopBlock block) {
        Registry.register(Registries.ITEM, Identifier.of(SpudaciousShops.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        SpudaciousShops.LOGGER.info("Registering mod blocks for " + SpudaciousShops.MOD_ID);
    }
}
