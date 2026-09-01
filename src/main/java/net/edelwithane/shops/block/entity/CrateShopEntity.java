package net.edelwithane.shops.block.entity;

import net.edelwithane.shops.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrateShopEntity extends AbstractShopEntity {

    public CrateShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRATE_SHOP_ENTITY.get(), pos, state, 0f);
    }

    @Override
    public int getTextureId() {
        return 0;
    }

}
