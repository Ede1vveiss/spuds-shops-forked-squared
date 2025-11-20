package net.lucab.shops.screen.networking;

import io.netty.buffer.ByteBuf;
import net.lucab.shops.SpudaciousShops;
import net.lucab.shops.screen.ShopScreenHandlerOwner;
import net.lucab.shops.screen.ToggleButtonID;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleSyncPkt(ToggleButtonID buttonID, boolean state) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ToggleSyncPkt> TYPE = new CustomPacketPayload.Type<>(
            SpudaciousShops.id("toggle_sync"));

    public static final StreamCodec<ByteBuf, ToggleSyncPkt> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.map(i -> ToggleButtonID.values()[i], ToggleButtonID::ordinal), ToggleSyncPkt::buttonID,
            ByteBufCodecs.BOOL, ToggleSyncPkt::state,
            ToggleSyncPkt::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player
                    && player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                boolean response = screenHandler.toggleButtonServersideUpdate(buttonID, state);

                // Send response back to client
                player.connection
                        .send(new ClientboundCustomPayloadPacket(new ToggleSyncResponsePkt(buttonID, response)));
            }
        });
    }
}
