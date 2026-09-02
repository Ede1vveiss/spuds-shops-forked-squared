package net.lucab.shops.item.custom;

import net.lucab.shops.block.custom.AngledShopBlock;
import net.lucab.shops.properties.Colour;
import net.minecraft.world.item.BlockItem;

public class ShopItem extends BlockItem {

    public final Colour colour;


    public ShopItem(AngledShopBlock block, Properties properties, Colour colour) {
        super(block, properties);
        this.colour = colour;
    }

}
