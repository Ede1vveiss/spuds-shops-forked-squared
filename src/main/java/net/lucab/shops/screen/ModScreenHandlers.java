package net.lucab.shops.screen;

import net.lucab.shops.SpudaciousShops;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import static net.lucab.shops.SpudaciousShops.getResource;

import java.util.Map;

public class ModScreenHandlers {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU,
            SpudaciousShops.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ShopScreenHandlerOwner>> SHOP_SCREEN_HANDLER_OWNER = registerMenuType(
            "shop_gui_owner", ShopScreenHandlerOwner::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ShopScreenHandlerCustomer>> SHOP_SCREEN_HANDLER_CUSTOMER = registerMenuType(
            "shop_gui_customer", ShopScreenHandlerCustomer::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(
            String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static final Map<Character, ResourceLocation> CURRENCY_IMG_MAP = Map.of(
            '£', getResource("textures/gui/currency_textures/gbp.png"),
            '€', getResource("textures/gui/currency_textures/eur.png"),
            'x', getResource("textures/gui/contract_slot.png"));

    public static void registerScreenHandlers(IEventBus modEventBus) {// called by modMain
        SpudaciousShops.LOGGER.info("Registering screen handlers for " + SpudaciousShops.MOD_ID);
        MENUS.register(modEventBus);
    }
}