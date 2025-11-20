package net.lucab.shops.screen.networking;

import io.netty.buffer.ByteBuf;
import net.lucab.shops.SpudaciousShops;
import net.lucab.shops.screen.ShopScreenOwner;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ShopTabSyncResponsePkt(int tab) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ShopTabSyncResponsePkt> TYPE = new CustomPacketPayload.Type<>(
            SpudaciousShops.id("shop_tab_sync_response"));

    public static final StreamCodec<ByteBuf, ShopTabSyncResponsePkt> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ShopTabSyncResponsePkt::tab,
            ShopTabSyncResponsePkt::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof ShopScreenOwner screen) {
                screen.updateTabSelectionResponse(tab);
            }
        });
    }
}
