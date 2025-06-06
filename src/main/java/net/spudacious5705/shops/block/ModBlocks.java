package net.spudacious5705.shops.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
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
import java.util.IdentityHashMap;
import java.util.List;

public abstract class ModBlocks{


    private static final AbstractBlock.Settings settingsWood = shopSettings(AbstractShopBlock.Settings.copy(Blocks.OAK_PLANKS));

    private static final AbstractBlock.Settings settingsStone = shopSettings(AbstractBlock.Settings.copy(Blocks.STONE));

    private static AbstractBlock.Settings shopSettings(AbstractBlock.Settings settingsExample){
        return settingsExample
                .nonOpaque()
                .hardness(-1f)
                .resistance(Float.MAX_VALUE);
    }

    public static final List<AbstractShopBlock> ALL_SHOPS = new ArrayList<>();
    public static final List<AbstractShopBlock> BASIC_SHOPS = new ArrayList<>();

    public static final List<ShelfShopBlock> ALL_SHELF_SHOPS = new ArrayList<>(11);

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

    public static final HookShopBlock SHOP_BLOCK_HOOK = registerBasic("hook_shop",new HookShopBlock(shopSettings(AbstractBlock.Settings.copy(Blocks.CHAIN))));

    public static final RugShopBlock SHOP_BLOCK_RUG = registerBasic("rug_shop",new RugShopBlock(shopSettings(AbstractBlock.Settings.copy(Blocks.RED_CARPET))));

    public static final CrateShopBlock SHOP_BLOCK_CRATE = registerBasic("crate_shop",new CrateShopBlock(settingsWood));

    public static final ShelfShopBlock SHOP_BLOCK_SHELF_ACACIA = registerShelf("acacia",Blocks.ACACIA_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_BAMBOO = registerShelf("bamboo",Blocks.BAMBOO_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_BIRCH = registerShelf("birch",Blocks.BIRCH_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_CHERRY = registerShelf("cherry",Blocks.CHERRY_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_CRIMSON = registerShelf("crimson",Blocks.CRIMSON_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_DARK_OAK = registerShelf("dark_oak",Blocks.DARK_OAK_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_MANGROVE = registerShelf("mangrove",Blocks.MANGROVE_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_OAK = registerShelf("oak",Blocks.OAK_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_SPRUCE = registerShelf("spruce",Blocks.SPRUCE_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_WARPED = registerShelf("warped",Blocks.WARPED_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_JUNGLE = registerShelf("jungle",Blocks.JUNGLE_SLAB);


    private static <S extends AbstractShopBlock> S registerBasic(String name, S shop){
        Identifier id = SpudaciousShops.id(name);
        Registry.register(
                Registries.ITEM,
                id,
                new BlockItem(shop, new FabricItemSettings())
        );

        return addToBasicShops(
                addToAllShops(
                Registry.register(
                        Registries.BLOCK,
                        id,
                        shop
                )
                )
        );
    }

    private static ShelfShopBlock registerShelf(String name, Block slab){

        ShelfShopBlock shop = new ShelfShopBlock(settingsWood,slab,name);

        name = "shelf_shop_"+name;
        Identifier id = SpudaciousShops.id(name);

        Registry.register(
                Registries.ITEM,
                id,
                new BlockItem(shop, new FabricItemSettings())
        );

        shop = addToAllShops(
                Registry.register(
                        Registries.BLOCK,
                        id,
                        shop
                )
        );

        ALL_SHELF_SHOPS.add(shop);
        return shop;
    }

    private static WindowSillShopBlock registerWindowShopBlock(String name, Item stoneType) {
        return registerBasic("shop_window_"+name,new WindowSillShopBlock(settingsStone, stoneType));
    }

    private static AngledShopBlock registerAngledShopBlock(String name, Item woodType) {
        name = "shop_"+name;
        Identifier id = SpudaciousShops.id(name);

        AngledShopBlock block = new AngledShopBlock(settingsWood, woodType);
        for(Colour colour : Colour.values()) {
            block.addDropItem(registerShopBlockItem(name, block, colour), colour);
        }

        return addToAllShops(
                Registry.register(
                        Registries.BLOCK,
                        id,
                        block
                )
        );
    }

    private static <S extends AbstractShopBlock> S addToAllShops(S register) {
        ALL_SHOPS.add(register);
        return register;
    }

    private static <S extends AbstractShopBlock> S addToBasicShops(S register) {
        BASIC_SHOPS.add(register);
        return register;
    }

    private static ShopItem registerShopBlockItem(String name, AngledShopBlock block, Colour colour) {
        Identifier id = SpudaciousShops.id(name + "_" + colour.asString());
        return Registry.register(Registries.ITEM, id,
                new ShopItem(block, new FabricItemSettings(), colour));

    }

    public static void registerModBlocks() {
        SpudaciousShops.LOGGER.info("Registering mod blocks for " + SpudaciousShops.MOD_ID);
    }

    public static List<AbstractShopBlock> getAllShops(){
        return ALL_SHOPS;
    }
}
