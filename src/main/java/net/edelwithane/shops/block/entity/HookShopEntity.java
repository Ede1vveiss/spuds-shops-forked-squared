package net.edelwithane.shops.block.entity;


import net.edelwithane.shops.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class HookShopEntity extends AbstractShopEntity{

    public HookShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HOOK_SHOP_ENTITY.get(), pos, state, -2.1f);
    }

    @Override
    public int getTextureId() {
        return 0;
    }


}
