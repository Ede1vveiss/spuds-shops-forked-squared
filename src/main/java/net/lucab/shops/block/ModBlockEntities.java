package net.lucab.shops.block;

import net.lucab.shops.SpudaciousShops;
import net.lucab.shops.block.custom.RugShopBlock;
import net.lucab.shops.block.custom.ShelfShopBlock;
import net.lucab.shops.block.entity.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SpudaciousShops.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AngledShopEntity>> ANGLED_SHOP_ENTITY = BLOCK_ENTITIES
            .register("shop_b_e",
                    () -> BlockEntityType.Builder.of(AngledShopEntity::new,
                            ModBlocks.SHOP_BLOCK_ANGLED_OAK.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_BAMBOO.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_BIRCH.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_ACACIA.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_CRIMSON.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_CHERRY.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_DARK_OAK.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_MANGROVE.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_SPRUCE.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_WARPED.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_JUNGLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindowSillShopEntity>> WINDOW_SHOP_ENTITY = BLOCK_ENTITIES
            .register("shop_b_e_window_sill",
                    () -> BlockEntityType.Builder.of(WindowSillShopEntity::new,
                            ModBlocks.SHOP_BLOCK_WINDOW_CALCITE.get(),
                            ModBlocks.SHOP_BLOCK_WINDOW_ANDESITE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HookShopEntity>> HOOK_SHOP_ENTITY = BLOCK_ENTITIES
            .register("shop_b_e_hook",
                    () -> BlockEntityType.Builder.of(HookShopEntity::new,
                            ModBlocks.SHOP_BLOCK_HOOK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RugShopEntity>> RUG_SHOP_ENTITY = BLOCK_ENTITIES
            .register("shop_b_e_rug",
                    () -> BlockEntityType.Builder.of(RugShopEntity::new,
                            ModBlocks.ALL_RUG_SHOPS.stream().map(DeferredHolder::get).toArray(RugShopBlock[]::new))
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrateShopEntity>> CRATE_SHOP_ENTITY = BLOCK_ENTITIES
            .register("shop_b_e_crate",
                    () -> BlockEntityType.Builder.of(CrateShopEntity::new,
                            ModBlocks.SHOP_BLOCK_CRATE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShelfShopEntity>> SHELF_SHOP_ENTITY = BLOCK_ENTITIES
            .register("shop_b_e_shelf",
                    () -> BlockEntityType.Builder.of(ShelfShopEntity::new,
                            ModBlocks.ALL_SHELF_SHOPS.stream().map(DeferredHolder::get).toArray(ShelfShopBlock[]::new))
                            .build(null));

    public static void registerBlockEntities(IEventBus modEventBus) {
        SpudaciousShops.LOGGER.info("Registering block entities for" + SpudaciousShops.MOD_ID);
        BLOCK_ENTITIES.register(modEventBus);
        AbstractShopEntity.initialiseStaticMethods();
    }
}