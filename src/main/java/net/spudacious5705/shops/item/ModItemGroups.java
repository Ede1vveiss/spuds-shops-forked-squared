package net.spudacious5705.shops.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;


public final class ModItemGroups {
    public static final ItemGroup SHOP_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.SHOP_BLOCK_ANGLED_OAK.getDefaultColouredShopItem()))
            .entries((displayContext, entries) -> {

                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_ACACIA.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_BAMBOO.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_BIRCH.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_CHERRY.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_CRIMSON.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_OAK.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_MANGROVE.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_DARK_OAK.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_SPRUCE.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_WARPED.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_ANGLED_JUNGLE.getDefaultColouredShopItem());
                        entries.add(ModBlocks.SHOP_BLOCK_WINDOW_CALCITE.asItem());
                        entries.add(ModBlocks.SHOP_BLOCK_WINDOW_ANDESITE.asItem());

                    })
            .displayName(Text.translatable("itemGroup.spudaciousshops.shop_item_group"))
            .build();

    public static void initialise() {
        Registry.register(Registries.ITEM_GROUP,Identifier.of(SpudaciousShops.MOD_ID, "shop_item_group"), SHOP_ITEM_GROUP);
    }
}
