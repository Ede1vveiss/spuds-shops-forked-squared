package net.spudacious5705.shops.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
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

public abstract class ModBlocks{

    public static final int SHOP_COUNT = 11;

    public static final int COLOUR_COUNT = 16;

    private static final AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .nonOpaque()
            .hardness(-1f)
            .resistance(Float.MAX_VALUE);

    private static final List<AngledShopBlock> ALL_SHOPS = new ArrayList<>(11);// FOR DEBUG COMMAND ONLY

    public static final AngledShopBlock SHOP_BLOCK_ANGLED_ACACIA = registerAngledShopBlock("acacia", Items.ACACIA_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_BAMBOO = registerAngledShopBlock("bamboo", Items.BAMBOO_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_BIRCH = registerAngledShopBlock("birch", Items.BIRCH_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_CHERRY = registerAngledShopBlock("cherry", Items.CHERRY_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_CRIMSON = registerAngledShopBlock("crimson", Items.CRIMSON_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_DARK_OAK = registerAngledShopBlock("dark_oak", Items.DARK_OAK_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_MANGROVE = registerAngledShopBlock("mangrove", Items.MANGROVE_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_OAK = registerAngledShopBlock("oak", Items.OAK_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_SPRUCE = registerAngledShopBlock("spruce", Items.SPRUCE_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_WARPED = registerAngledShopBlock("warped", Items.WARPED_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_JUNGLE = registerAngledShopBlock("jungle", Items.JUNGLE_PLANKS);

    public static final WindowSillShopBlock SHOP_BLOCK_WINDOW_CALCITE = registerWindowShopBlock("calcite", Items.CALCITE);
    public static final WindowSillShopBlock SHOP_BLOCK_WINDOW_ANDESITE = registerWindowShopBlock("andesite", Items.ANDESITE);

    private static WindowSillShopBlock registerWindowShopBlock(String name, Item stoneType) {
        name = "shop_window_"+name;
        WindowSillShopBlock block = new WindowSillShopBlock(settings, stoneType);

        Registry.register(Registries.ITEM, Identifier.of(SpudaciousShops.MOD_ID, name), new BlockItem(block, new Item.Settings()));

        return Registry.register(Registries.BLOCK, new Identifier(SpudaciousShops.MOD_ID, name), block);
    }

    private static int numbOfShopItems(){
        return SHOP_COUNT*COLOUR_COUNT;
    }

    private static AngledShopBlock registerAngledShopBlock(String name, Item woodType) {
        name = "shop_"+name;
        AngledShopBlock block = new AngledShopBlock(settings, woodType);
        for(Colour colour : Colour.values()) {
            block.addDropItem(registerShopBlockItem(name, block, colour), colour);
        }

        return AddToAllShops(Registry.register(Registries.BLOCK, new Identifier(SpudaciousShops.MOD_ID, name), block));
    }

    private static AngledShopBlock AddToAllShops(AngledShopBlock register) {
        ALL_SHOPS.add(register);
        return register;
    }

    private static ShopItem registerShopBlockItem(String name, AngledShopBlock block, Colour colour) {
        name = name + "_" + colour.asString();
        Identifier id = Identifier.of(SpudaciousShops.MOD_ID, name);
        return Registry.register(Registries.ITEM, id,
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
