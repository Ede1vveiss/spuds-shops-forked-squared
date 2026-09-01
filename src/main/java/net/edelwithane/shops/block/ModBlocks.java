package net.edelwithane.shops.block;

import net.edelwithane.shops.SpudaciousShops;
import net.edelwithane.shops.block.custom.*;
import net.edelwithane.shops.block.custom.*;
import net.edelwithane.shops.item.custom.ShopItem;
import net.edelwithane.shops.properties.Colour;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import static net.edelwithane.shops.SpudaciousShops.MOD_ID;
import static net.edelwithane.shops.block.VariantResources.*;
import static net.edelwithane.shops.block.VariantResources.wood_variant.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {

    public static final List<Runnable> postRegistryTasks = new ArrayList<>();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MOD_ID);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);

    private static final BlockBehaviour.Properties settingsChain = shopSettings(Blocks.CHAIN);

    private static final BlockBehaviour.Properties settingsWood = shopSettings(Blocks.OAK_PLANKS);

    private static final BlockBehaviour.Properties settingsStone = shopSettings(Blocks.STONE);

    public static final BlockBehaviour.Properties settingsCarpet = shopSettings(Blocks.RED_CARPET);

    private static BlockBehaviour.Properties shopSettings(Block example) {
        return BlockBehaviour.Properties.ofFullCopy(example)
                .noOcclusion()
                .strength(1f, Float.MAX_VALUE);
    }

    public static final List<DeferredHolder<Block, ? extends AbstractShopBlock>> ALL_SHOPS = new ArrayList<>();
    public static final List<DeferredHolder<Block, ? extends AbstractShopBlock>> BASIC_SHOPS = new ArrayList<>();

    public static final List<DeferredHolder<Block, ShelfShopBlock>> ALL_SHELF_SHOPS = new ArrayList<>(11);

    public static final List<DeferredHolder<Block, WindowSillShopBlock>> ALL_WINDOW_SHOPS = new ArrayList<>(10);

    public static final List<DeferredHolder<Block, RugShopBlock>> ALL_RUG_SHOPS = new ArrayList<>(11);

    // region ANGLED
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_ACACIA = registerAngledShopBlock(
            ACACIA, () -> Items.ACACIA_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_BAMBOO = registerAngledShopBlock(
            BAMBOO, () -> Items.BAMBOO_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_BIRCH = registerAngledShopBlock(BIRCH,
            () -> Items.BIRCH_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_CHERRY = registerAngledShopBlock(
            CHERRY, () -> Items.CHERRY_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_CRIMSON = registerAngledShopBlock(
            CRIMSON, () -> Items.CRIMSON_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_DARK_OAK = registerAngledShopBlock(
            DARK_OAK, () -> Items.DARK_OAK_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_MANGROVE = registerAngledShopBlock(
            MANGROVE, () -> Items.MANGROVE_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_OAK = registerAngledShopBlock(OAK,
            () -> Items.OAK_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_SPRUCE = registerAngledShopBlock(
            SPRUCE, () -> Items.SPRUCE_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_WARPED = registerAngledShopBlock(
            WARPED, () -> Items.WARPED_PLANKS);
    public static final DeferredHolder<Block, AngledShopBlock> SHOP_BLOCK_ANGLED_JUNGLE = registerAngledShopBlock(
            JUNGLE, () -> Items.JUNGLE_PLANKS);

    private static DeferredHolder<Block, AngledShopBlock> registerAngledShopBlock(VariantResources.wood_variant variant,
            Supplier<Item> woodType) {
        String name = "shop_" + variant.name;

        DeferredHolder<Block, AngledShopBlock> block = BLOCKS.register(
                name,
                () -> new AngledShopBlock(settingsWood, woodType.get(), variant));
        VariantResources.putItem(ANGLED, new PostRegAssigner<>(woodType), block::get);

        for (Colour colour : Colour.values()) {

            DeferredHolder<Item, ShopItem> item = registerShopBlockItem(name, block, colour);

            postRegistryTasks.add(() -> {
                block.get().addDropItem(item.get(), colour);
            });
        }

        ITEMS.register(name,
                () -> new BlockItem(block.get(), new Item.Properties()));

        return addToAllShops(
                block);
    }
    // endregion

    // region WINDOW SILL
    public static final DeferredHolder<Block, WindowSillShopBlock> SHOP_BLOCK_WINDOW_CALCITE = registerWindowShopBlock(
            "calcite", () -> Items.CALCITE);
    public static final DeferredHolder<Block, WindowSillShopBlock> SHOP_BLOCK_WINDOW_ANDESITE = registerWindowShopBlock(
            "andesite", () -> Items.ANDESITE);
    // endregion

    public static final DeferredHolder<Block, HookShopBlock> SHOP_BLOCK_HOOK = registerBasic("hook_shop",
            () -> new HookShopBlock(settingsChain));

    // region RUG
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_RED = registerRugLegacy();
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_WHITE = registerRug("white",
            () -> Items.WHITE_CARPET, () -> Items.WHITE_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_ORANGE = registerRug("orange",
            () -> Items.ORANGE_CARPET, () -> Items.ORANGE_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_MAGENTA = registerRug("magenta",
            () -> Items.MAGENTA_CARPET, () -> Items.MAGENTA_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_LIGHT_BLUE = registerRug("light_blue",
            () -> Items.LIGHT_BLUE_CARPET, () -> Items.LIGHT_BLUE_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_YELLOW = registerRug("yellow",
            () -> Items.YELLOW_CARPET, () -> Items.YELLOW_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_LIME = registerRug("lime",
            () -> Items.LIME_CARPET, () -> Items.LIME_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_PINK = registerRug("pink",
            () -> Items.PINK_CARPET, () -> Items.PINK_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_GRAY = registerRug("gray",
            () -> Items.GRAY_CARPET, () -> Items.GRAY_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_LIGHT_GRAY = registerRug("light_gray",
            () -> Items.LIGHT_GRAY_CARPET, () -> Items.LIGHT_GRAY_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_CYAN = registerRug("cyan",
            () -> Items.CYAN_CARPET, () -> Items.CYAN_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_PURPLE = registerRug("purple",
            () -> Items.PURPLE_CARPET, () -> Items.PURPLE_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_BLUE = registerRug("blue",
            () -> Items.BLUE_CARPET, () -> Items.BLUE_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_BROWN = registerRug("brown",
            () -> Items.BROWN_CARPET, () -> Items.BROWN_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_GREEN = registerRug("green",
            () -> Items.GREEN_CARPET, () -> Items.GREEN_DYE);
    public static final DeferredHolder<Block, RugShopBlock> SHOP_BLOCK_RUG_BLACK = registerRug("black",
            () -> Items.BLACK_CARPET, () -> Items.BLACK_DYE);

    private static DeferredHolder<Block, RugShopBlock> registerRug(String colour, Supplier<Item> carpet,
            Supplier<Item> dye) {

        DeferredHolder<Block, RugShopBlock> rug = registerBasic("rug_shop_" + colour,
                () -> new RugShopBlock(settingsCarpet, carpet.get(), colour));
        ALL_RUG_SHOPS.add(rug);

        VariantResources.putItem(RUGS_CARPET, new PostRegAssigner<>(carpet), rug);
        VariantResources.putItem(RUGS_DYE, new PostRegAssigner<>(dye), rug);

        return rug;
    }

    private static DeferredHolder<Block, RugShopBlock> registerRugLegacy() {
        String name = "rug_shop";

        DeferredHolder<Block, RugShopBlock> rug = registerBasic(name,
                () -> new RugShopBlock(settingsCarpet, Items.RED_CARPET, "red"));
        ALL_RUG_SHOPS.add(rug);

        VariantResources.putItem(RUGS_CARPET, new PostRegAssigner<>(() -> Items.RED_CARPET), rug::get);
        VariantResources.putItem(RUGS_DYE, new PostRegAssigner<>(() -> Items.RED_DYE), rug::get);

        return rug;
    }
    // endregion

    public static final DeferredHolder<Block, CrateShopBlock> SHOP_BLOCK_CRATE = registerBasic("crate_shop",
            () -> new CrateShopBlock(settingsWood));

    // region SHELF
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_ACACIA = registerShelf(ACACIA,
            () -> Blocks.ACACIA_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_BAMBOO = registerShelf(BAMBOO,
            () -> Blocks.BAMBOO_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_BIRCH = registerShelf(BIRCH,
            () -> Blocks.BIRCH_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_CHERRY = registerShelf(CHERRY,
            () -> Blocks.CHERRY_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_CRIMSON = registerShelf(CRIMSON,
            () -> Blocks.CRIMSON_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_DARK_OAK = registerShelf(DARK_OAK,
            () -> Blocks.DARK_OAK_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_MANGROVE = registerShelf(MANGROVE,
            () -> Blocks.MANGROVE_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_OAK = registerShelf(OAK,
            () -> Blocks.OAK_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_SPRUCE = registerShelf(SPRUCE,
            () -> Blocks.SPRUCE_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_WARPED = registerShelf(WARPED,
            () -> Blocks.WARPED_SLAB);
    public static final DeferredHolder<Block, ShelfShopBlock> SHOP_BLOCK_SHELF_JUNGLE = registerShelf(JUNGLE,
            () -> Blocks.JUNGLE_SLAB);

    private static DeferredHolder<Block, ShelfShopBlock> registerShelf(VariantResources.wood_variant variant,
            Supplier<Block> slab) {

        String name = "shelf_shop_" + variant.name;

        DeferredHolder<Block, ShelfShopBlock> shop = BLOCKS.register(name,
                () -> new ShelfShopBlock(settingsWood, slab.get(), variant));

        ITEMS.register(name, () -> new BlockItem(shop.get(), new Item.Properties()));

        addToAllShops(
                shop);

        ALL_SHELF_SHOPS.add(shop);
        VariantResources.putBlock(SHELF, new PostRegAssigner<>(slab), shop::get);
        return shop;
    }
    // endregion

    private static <S extends AbstractShopBlock> DeferredHolder<Block, S> registerBasic(String name, Supplier<S> shop) {

        DeferredHolder<Block, S> block = addToBasicShops(
                addToAllShops(
                        BLOCKS.register(name, shop)));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));

        return block;
    }

    private static DeferredHolder<Block, WindowSillShopBlock> registerWindowShopBlock(String name,
            Supplier<Item> stoneType) {
        DeferredHolder<Block, WindowSillShopBlock> shop = registerBasic("shop_window_" + name,
                () -> new WindowSillShopBlock(settingsStone, stoneType.get()));
        ALL_WINDOW_SHOPS.add(shop);
        VariantResources.putItem(WINDOW_SILL, new PostRegAssigner<>(stoneType), shop::get);
        return shop;
    }

    private static <S extends AbstractShopBlock> DeferredHolder<Block, S> addToAllShops(
            DeferredHolder<Block, S> register) {
        ALL_SHOPS.add(register);
        return register;
    }

    private static <S extends AbstractShopBlock> DeferredHolder<Block, S> addToBasicShops(
            DeferredHolder<Block, S> register) {
        BASIC_SHOPS.add(register);
        return register;
    }

    private static DeferredHolder<Item, ShopItem> registerShopBlockItem(String name,
            DeferredHolder<Block, AngledShopBlock> block, Colour colour) {
        name = name + "_" + colour.asString();
        return ITEMS.register(name, () -> new ShopItem(block.get(), new Item.Properties(), colour));

    }

    public static void registerModBlocks(IEventBus modEventBus) {
        SpudaciousShops.LOGGER.info("Registering mod blocks for " + MOD_ID);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        // postRegistryTasks is called in SpudaciousShops.commonSetup()
    }

    public static List<DeferredHolder<Block, ? extends AbstractShopBlock>> getAllShops() {
        return ALL_SHOPS;
    }

}
