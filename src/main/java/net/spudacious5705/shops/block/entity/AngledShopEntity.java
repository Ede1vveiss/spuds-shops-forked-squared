package net.spudacious5705.shops.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.spudacious5705.shops.model.CushionTextures;
import net.spudacious5705.shops.properties.Colour;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.screen.ShopScreenHandlerCustomer;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;
import org.jetbrains.annotations.Nullable;

public class AngledShopEntity extends AbstractShopEntity implements ExtendedScreenHandlerFactory{


    public AngledShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOP_ENTITY, pos, state);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        //runs serverside

        int result = player.getUuid().compareTo(ownerID);

        if(result==0) return new ShopScreenHandlerOwner(syncId, playerInventory, this, this.propertyDelegate);

        if(!isShopFunctional()) {return null;}

        return new ShopScreenHandlerCustomer(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public BlockState getCachedState() {
        return super.getCachedState();
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public Direction getCachedFacingDirection(){
        Direction direction = this.getCachedState().get(Properties.HORIZONTAL_FACING);
        if(direction == null) return Direction.NORTH;
        return direction;
    }

    public Colour getCachedCushionColour(){
        Colour colour = this.getCachedState().get(ModProperties.CUSHION_COLOUR);
        if(colour == null) return Colour.RED;
        return colour;
    }

    public Identifier getCushionTextureID() {
        Colour colour = getCachedCushionColour();
        return CushionTextures.TEXTURE_MAP.get(colour);
    }

    //Only call from the CLIENT
    @Environment(EnvType.CLIENT)
    public void forceUpdateRenderData() {
        rendererData.update();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
}
