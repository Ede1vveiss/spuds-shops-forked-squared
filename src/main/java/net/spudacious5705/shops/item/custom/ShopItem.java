package net.spudacious5705.shops.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.spudacious5705.shops.block.custom.ShopBlock;
import net.spudacious5705.shops.properties.Colour;

public class ShopItem extends BlockItem {

    public final Colour colour;

    public ShopItem(ShopBlock block, Settings settings, Colour colour) {
        super(block, settings);
        this.colour = colour;
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        state.with(ShopBlock.CUSHION_COLOUR, colour);
        return super.place(context, state);
    }

}
