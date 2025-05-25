package net.spudacious5705.shops.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class RugShopEntity extends AbstractShopEntity{

    public RugShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUG_SHOP_ENTITY, pos, state, -0.3f);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public Direction getFacingDirection() {
        return Direction.NORTH;
    }
}
