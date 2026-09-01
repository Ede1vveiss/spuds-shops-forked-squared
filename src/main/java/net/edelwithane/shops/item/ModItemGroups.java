package net.edelwithane.shops.item;

import net.edelwithane.shops.SpudaciousShops;
import net.edelwithane.shops.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModItemGroups {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, SpudaciousShops.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHOPS_TAB = CREATIVE_MODE_TABS.register(
            "shops_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.SHOP_BLOCK_ANGLED_OAK.get().getDefaultColouredShopItem()))
                    .title(Component.translatable("itemGroup.spudaciousshops.shop_item_group"))
                    .displayItems(
                            (params, entries) -> {
                                entries.accept(ModItems.CONTRACT_SCROLL.get());
                                ModBlocks.ALL_SHOPS.forEach(shop -> entries.accept(shop.get()));
                            })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
