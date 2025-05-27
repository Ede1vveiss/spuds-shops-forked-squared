package net.spudacious5705.shops.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;

public class ModScreenHandlers {

    public  static  final ScreenHandlerType<ShopScreenHandlerOwner> SHOP_SCREEN_HANDLER_OWNER =
            Registry.register(Registries.SCREEN_HANDLER, id("shop_gui_owner"),
                    new ExtendedScreenHandlerType<>(ShopScreenHandlerOwner::new));

    public  static  final ScreenHandlerType<ShopScreenHandlerCustomer> SHOP_SCREEN_HANDLER_CUSTOMER =
            Registry.register(Registries.SCREEN_HANDLER, id("shop_gui_customer"),
                    new ExtendedScreenHandlerType<>(ShopScreenHandlerCustomer::new));


    public static Identifier id(String path) {
        return Identifier.of(SpudaciousShops.MOD_ID, path);
    }
    public static void registerScreenHandlers() {
        SpudaciousShops.LOGGER.info("Registering screen handlers for " + SpudaciousShops.MOD_ID);

    }
}