package net.lucab.shops;

import static net.lucab.shops.SpudaciousShops.MOD_ID;

import net.lucab.shops.block.ModBlockEntities;
import net.lucab.shops.block.entity.renderer.*;
import net.lucab.shops.screen.ModScreenHandlers;
import net.lucab.shops.screen.ShopScreenCustomer;
import net.lucab.shops.screen.ShopScreenOwner;
import net.lucab.shops.util.CushionModel;
import net.lucab.shops.util.CushionResources;
import net.lucab.shops.util.CushionTextures;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SpudaciousShopsClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            /*
             * ModBlocks.getAllShops().forEach((DeferredHolder<Block, ? extends
             * AbstractShopBlock> shop) -> {
             * ItemBlockRenderTypes.setRenderLayer(shop.get(), RenderType.cutout());
             * });
             */
        });

        CushionTextures.initialiseCushionTextures();
        CushionResources.initialise();

    }

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER.get(), ShopScreenOwner::new);
        event.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER.get(), ShopScreenCustomer::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ANGLED_SHOP_ENTITY.get(), AngledShopBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WINDOW_SHOP_ENTITY.get(), WindowSillShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HOOK_SHOP_ENTITY.get(), HookShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUG_SHOP_ENTITY.get(), RugShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRATE_SHOP_ENTITY.get(), CrateShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF_SHOP_ENTITY.get(), ShelfShopEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CushionModel.LAYER_LOCATION, CushionModel::getTexturedModelData);
    }
}
