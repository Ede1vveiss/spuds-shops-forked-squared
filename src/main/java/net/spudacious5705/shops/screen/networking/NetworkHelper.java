package net.spudacious5705.shops.screen.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;
import net.spudacious5705.shops.screen.ShopScreenOwner;

public class NetworkHelper {
    public static final Identifier SHOP_TAB_SYNC_ID = SpudaciousShops.id("shop_tab_sync");
    public static final Identifier SHOP_TAB_SYNC_RESPONSE_ID = SpudaciousShops.id("shop_tab_sync");
    public static void initialiseSERVER(){
        ServerPlayNetworking.registerGlobalReceiver(SHOP_TAB_SYNC_ID,
                (server, player, handler, buf, responseSender) -> {
                    int tab = buf.readInt();
                    server.execute(() -> {
                        if (player.currentScreenHandler instanceof ShopScreenHandlerOwner screenHandler) {
                            screenHandler.updateTabSelectionServerside(tab); // Sync the tab on the server

                            //update client
                            PacketByteBuf responseBuf = PacketByteBufs.create();
                            responseBuf.writeInt(tab);
                            ServerPlayNetworking.send(player, SHOP_TAB_SYNC_RESPONSE_ID, responseBuf);
                        }
                    });
                });
    }

    @Environment(EnvType.CLIENT)
    public static void initialiseCLIENT(){
        ClientPlayNetworking.registerGlobalReceiver(SHOP_TAB_SYNC_RESPONSE_ID,
                (client, handler, buf, responseSender) -> {
                    int tab = buf.readInt();
                    client.execute(() -> {
                        if (client.currentScreen instanceof ShopScreenOwner screen) {
                            screen.updateTabSelectionResponse(tab); // Update UI on client
                        }
                    });
                });

    }
}
