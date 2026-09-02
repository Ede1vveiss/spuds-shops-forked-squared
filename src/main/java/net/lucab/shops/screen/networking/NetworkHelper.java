package net.lucab.shops.screen.networking;

import net.lucab.shops.SpudaciousShops;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SpudaciousShops.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHelper {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                ShopTabSyncPkt.TYPE,
                ShopTabSyncPkt.STREAM_CODEC,
                ShopTabSyncPkt::handle);

        registrar.playBidirectional(
                ShopSelfDemotePkt.TYPE,
                ShopSelfDemotePkt.STREAM_CODEC,
                ShopSelfDemotePkt::handle);

        registrar.playBidirectional(
                ToggleSyncPkt.TYPE,
                ToggleSyncPkt.STREAM_CODEC,
                ToggleSyncPkt::handle);

        registrar.playBidirectional(
                ToggleSyncResponsePkt.TYPE,
                ToggleSyncResponsePkt.STREAM_CODEC,
                ToggleSyncResponsePkt::handle);

        registrar.playBidirectional(
                ShopTabSyncResponsePkt.TYPE,
                ShopTabSyncResponsePkt.STREAM_CODEC,
                ShopTabSyncResponsePkt::handle);
    }
}
