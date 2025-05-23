package net.spudacious5705.shops.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.block.custom.AngledShopBlock;
import net.spudacious5705.shops.properties.Colour;

public class ShopItem extends BlockItem {

    public final Colour colour;


    public ShopItem(AngledShopBlock block, Settings settings, Colour colour) {
        super(block, settings);
        this.colour = colour;
    }

    /*@Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        return super.place(context, state);
    }*/

}
