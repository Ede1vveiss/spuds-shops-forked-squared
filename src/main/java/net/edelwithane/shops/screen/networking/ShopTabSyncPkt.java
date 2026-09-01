package net.edelwithane.shops.screen.networking;

import io.netty.buffer.ByteBuf;
import net.edelwithane.shops.SpudaciousShops;
import net.edelwithane.shops.screen.ShopScreenHandlerOwner;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ShopTabSyncPkt(int tab) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ShopTabSyncPkt> TYPE = new CustomPacketPayload.Type<>(
            SpudaciousShops.id("shop_tab_sync"));

    public static final StreamCodec<ByteBuf, ShopTabSyncPkt> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ShopTabSyncPkt::tab,
            ShopTabSyncPkt::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player
                    && player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                screenHandler.updateTabSelectionServerside(tab);

                // Send response back to client
                player.connection.send(new ClientboundCustomPayloadPacket(new ShopTabSyncResponsePkt(tab)));
            }
        });
    }
}
