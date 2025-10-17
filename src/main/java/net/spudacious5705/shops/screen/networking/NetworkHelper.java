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
import net.spudacious5705.shops.screen.ToggleButtonID;

public class NetworkHelper {
    public static final Identifier SHOP_TAB_SYNC_ID = SpudaciousShops.id("shop_tab_sync");
    public static final Identifier SHOP_TAB_SYNC_RESPONSE_ID = SpudaciousShops.id("shop_tab_sync");
    public static final Identifier SHOP_SELF_DEMOTE = SpudaciousShops.id("shop_self_demote");
    public static final Identifier TOGGLE_SYNC = SpudaciousShops.id("toggle_sync");
    public static final Identifier TOGGLE_SYNC_RESPONSE = SpudaciousShops.id("toggle_sync_response");


    public static void initialise(){
        ServerPlayNetworking.registerGlobalReceiver(SHOP_TAB_SYNC_ID,
                (server, player, handler, buf, responseSender) -> {
                    int tab = buf.readInt();
                    server.execute(() -> {
                        if (player.currentScreenHandler instanceof ShopScreenHandlerOwner screenHandler) {
                            screenHandler.updateTabSelectionServerside(tab); // Sync the tab on the server

                            //update client
                            PacketByteBuf responseBuf = PacketByteBufs.create();
                            responseBuf.writeInt(tab);
                            responseSender.sendPacket(SHOP_TAB_SYNC_RESPONSE_ID, responseBuf);
                        }
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(SHOP_SELF_DEMOTE,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        if (player.currentScreenHandler instanceof ShopScreenHandlerOwner screenHandler) {
                            screenHandler.selfDemotePlayer(player);
                            player.closeHandledScreen();
                        }
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_SYNC,
                (server, player, handler, buf, responseSender) -> {
            String tabID = buf.readString();
            boolean state = buf.readBoolean();
            server.execute(() -> {
                        if(player.currentScreenHandler instanceof ShopScreenHandlerOwner screenHandler) {

                            ToggleButtonID button;

                            try {
                                button = ToggleButtonID.fromString(tabID);
                            } catch (Exception e) {
                                return;
                            }

                            boolean response = screenHandler.toggleButtonServersideUpdate(button, state); // Sync the tab on the server

                            //update client
                            PacketByteBuf responseBuf = PacketByteBufs.create();
                            responseBuf.writeString(tabID);
                            responseBuf.writeBoolean(response);

                            responseSender.sendPacket(TOGGLE_SYNC_RESPONSE, responseBuf);
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

        ClientPlayNetworking.registerGlobalReceiver(TOGGLE_SYNC_RESPONSE,
                (client, handler, buf, responseSender) -> {
            String tabID = buf.readString();
            boolean state = buf.readBoolean();
            client.execute(() -> {
                if (client.currentScreen instanceof ShopScreenOwner screen) {
                   ToggleButtonID button;
                   try {
                       button = ToggleButtonID.fromString(tabID);
                        //TODO FIX ISSUE WITH THE SETTINGS TEXTURE.
                        // THE SLOT FOR THE CREATIVE BUTTON SHOWS WHEN IN SURVIVAL
                        // WHILST THE CREATIVE BUTTON DOES NOT
                   } catch (Exception e) {
                       return;
                   }
                   screen.getScreenHandler().updateToggleButtonFromPacket(button, state); // Update UI on client
                }
            });
        });
    }


}
