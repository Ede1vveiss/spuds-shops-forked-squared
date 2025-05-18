package net.spudacious5705.shops.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.*;
import net.spudacious5705.shops.item.custom.ShopItem;
import net.spudacious5705.shops.properties.Colour;

import java.util.ArrayList;
import java.util.List;

public abstract class       ModBlocks{

    public static final ArrayList<ShopItem> SHOP_ITEM_LIST = new ArrayList<>(numbOfShopItems());

    public static final int SHOP_COUNT = 11;

    public static final int COLOUR_COUNT = 16;

    private static final AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .nonOpaque()
            .hardness(-1f)
            .resistance(Float.MAX_VALUE);

    private static final List<AngledShopBlock> ALL_SHOPS = new ArrayList<>(11);

    public static final AngledShopBlock SHOP_BLOCK_ACACIA = registerShopBlock("shop_acacia", Items.ACACIA_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_BAMBOO = registerShopBlock("shop_bamboo", Items.BAMBOO_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_BIRCH = registerShopBlock("shop_birch", Items.BIRCH_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_CHERRY = registerShopBlock("shop_cherry", Items.CHERRY_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_CRIMSON = registerShopBlock("shop_crimson", Items.CRIMSON_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_DARK_OAK = registerShopBlock("shop_dark_oak", Items.DARK_OAK_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_MANGROVE = registerShopBlock("shop_mangrove", Items.MANGROVE_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_OAK = registerShopBlock("shop_oak", Items.OAK_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_SPRUCE = registerShopBlock("shop_spruce", Items.SPRUCE_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_WARPED = registerShopBlock("shop_warped", Items.WARPED_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_JUNGLE = registerShopBlock("shop_jungle", Items.JUNGLE_PLANKS);

    private static int numbOfShopItems(){
        return SHOP_COUNT*COLOUR_COUNT;
    }

    private static AngledShopBlock registerShopBlock(String name, Item woodType) {
        AngledShopBlock block = new AngledShopBlock(settings, woodType);
        for(Colour colour : Colour.values()) {
            block.addDropItem(registerShopBlockItem(name, block, colour), colour);
        }
        //registerBlockItem(name,block);
        return AddToAllShops(Registry.register(Registries.BLOCK, new Identifier(SpudaciousShops.MOD_ID, name), block));
    }

    private static AngledShopBlock AddToAllShops(AngledShopBlock register) {
        ALL_SHOPS.add(register);
        return register;
    }

    private static ShopItem registerShopBlockItem(String name, AngledShopBlock block, Colour colour) {
        name = name + "_" + colour.asString();

        return Registry.register(Registries.ITEM, new Identifier(SpudaciousShops.MOD_ID, name),
                new ShopItem(block, new FabricItemSettings(), colour));

    }

    /*private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(SpudaciousShops.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }*/

    public static void registerModBlocks() {
        SpudaciousShops.LOGGER.info("Registering mod blocks for " + SpudaciousShops.MOD_ID);
    }

    public static List<AngledShopBlock> getAllShops(){
        return ALL_SHOPS;
    }
}
