package net.spudacious5705.shops.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;

public class ModBlockEntities {

    public static final BlockEntityType<AngledShopEntity> ANGLED_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SpudaciousShops.MOD_ID, "shop_b_e_angled"),
                    FabricBlockEntityTypeBuilder.create(AngledShopEntity::new,
                            ModBlocks.SHOP_BLOCK_ANGLED_OAK,
                            ModBlocks.SHOP_BLOCK_ANGLED_BAMBOO,
                            ModBlocks.SHOP_BLOCK_ANGLED_BIRCH,
                            ModBlocks.SHOP_BLOCK_ANGLED_ACACIA,
                            ModBlocks.SHOP_BLOCK_ANGLED_CRIMSON,
                            ModBlocks.SHOP_BLOCK_ANGLED_CHERRY,
                            ModBlocks.SHOP_BLOCK_ANGLED_DARK_OAK,
                            ModBlocks.SHOP_BLOCK_ANGLED_MANGROVE,
                            ModBlocks.SHOP_BLOCK_ANGLED_SPRUCE,
                            ModBlocks.SHOP_BLOCK_ANGLED_WARPED,
                            ModBlocks.SHOP_BLOCK_ANGLED_JUNGLE
                    ).build());


    public static final BlockEntityType<WindowSillShopEntity> WINDOW_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SpudaciousShops.MOD_ID, "shop_b_e_window_sill"),
                    FabricBlockEntityTypeBuilder.create(WindowSillShopEntity::new,
                            ModBlocks.SHOP_BLOCK_WINDOW_CALCITE,
                            ModBlocks.SHOP_BLOCK_WINDOW_ANDESITE
                    ).build());

    public static final BlockEntityType<HookShopEntity> HOOK_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SpudaciousShops.MOD_ID, "shop_b_e_hook"),
                    FabricBlockEntityTypeBuilder.create(HookShopEntity::new,
                            ModBlocks.SHOP_BLOCK_HOOK
                    ).build());

    public static final BlockEntityType<RugShopEntity> RUG_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SpudaciousShops.MOD_ID, "shop_b_e_rug"),
                    FabricBlockEntityTypeBuilder.create(RugShopEntity::new,
                            ModBlocks.SHOP_BLOCK_RUG
                    ).build());

    public static final BlockEntityType<CrateShopEntity> CRATE_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SpudaciousShops.MOD_ID, "shop_b_e_crate"),
                    FabricBlockEntityTypeBuilder.create(CrateShopEntity::new,
                            ModBlocks.SHOP_BLOCK_CRATE
                    ).build());


    public static void registerBlockEntities() {
        SpudaciousShops.LOGGER.info("Registering block entities for" + SpudaciousShops.MOD_ID);
    }
}