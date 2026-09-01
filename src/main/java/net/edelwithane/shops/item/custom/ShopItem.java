package net.edelwithane.shops.item.custom;

import net.edelwithane.shops.block.custom.AngledShopBlock;
import net.edelwithane.shops.properties.Colour;
import net.minecraft.world.item.BlockItem;

public class ShopItem extends BlockItem {

    public final Colour colour;


    public ShopItem(AngledShopBlock block, Properties properties, Colour colour) {
        super(block, properties);
        this.colour = colour;
    }

}
