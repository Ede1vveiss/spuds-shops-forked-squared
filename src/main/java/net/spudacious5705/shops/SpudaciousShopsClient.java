package net.spudacious5705.shops;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.entity.ModBlockEntities;
import net.spudacious5705.shops.block.entity.renderer.*;
import net.spudacious5705.shops.util.CushionModel;
import net.spudacious5705.shops.util.CushionResources;
import net.spudacious5705.shops.util.CushionTextures;
import net.spudacious5705.shops.screen.*;

public class SpudaciousShopsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        /*
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_BIRCH);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_OAK);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_WARPED);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_SPRUCE);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_CHERRY);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_MANGROVE);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_BAMBOO);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_DARK_OAK);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_CRIMSON);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_ACACIA);
        putCutout(ModBlocks.SHOP_BLOCK_ANGLED_JUNGLE);
        */

        ModBlocks.getAllShops().forEach((Block shop) -> BlockRenderLayerMap.INSTANCE.putBlock(shop, RenderLayer.getCutout()));


        EntityModelLayerRegistry.registerModelLayer(CushionModel.LAYER_LOCATION, CushionModel::getTexturedModelData);

        HandledScreens.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER, ShopScreenOwner::new);
        HandledScreens.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER, ShopScreenCustomer::new);

        CushionTextures.initialiseCushionTextures();
        CushionResources.initialise();

        BlockEntityRendererFactories.register(ModBlockEntities.ANGLED_SHOP_ENTITY, AngledShopBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.WINDOW_SHOP_ENTITY, WindowSillShopEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.HOOK_SHOP_ENTITY, HookShopEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.RUG_SHOP_ENTITY, RugShopEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.CRATE_SHOP_ENTITY, CrateShopEntityRenderer::new);

    }
}
