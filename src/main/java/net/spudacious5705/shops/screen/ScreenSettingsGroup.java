package net.spudacious5705.shops.screen;

import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;

import java.util.Arrays;


public enum ScreenSettingsGroup {
    BASIC(0,
            new ScreenSettings(
                    id("owner_customer_screen"),
                    176, 165,
                    8,84,
                    0,0,
                    80,11),
            new ScreenSettings(
                    id("shop_seller"),
                    228, 254,
                    33,172,
                    60,10,
                    23,11),
            new ScreenSettings(
                    id("shop_settings"),
                    228, 254,
                    33,172,
                    60,10,
                    23,11),
            198,164,
            198,190,
            198,216
    );

    final ScreenSettings CUSTOMER;
    final ScreenSettings SELLER;
    final ScreenSettings SETTINGS;
    final int ID;

    final int tab1ButtonX; final int tab1ButtonY;
    final int tab2ButtonX; final int tab2ButtonY;
    final int tab3ButtonX; final int tab3ButtonY;


    ScreenSettingsGroup(int id, ScreenSettings CUSTOMER, ScreenSettings SELLER, ScreenSettings SETTINGS,
                        int tab1ButtonX, int tab1ButtonY,
                        int tab2ButtonX, int tab2ButtonY,
                        int tab3ButtonX, int tab3ButtonY) {
        this.ID = id;
        this.CUSTOMER = CUSTOMER;
        this.SELLER = SELLER;
        this.SETTINGS = SETTINGS;
        this.tab1ButtonX = tab1ButtonX;
        this.tab1ButtonY = tab1ButtonY;
        this.tab2ButtonX = tab2ButtonX;
        this.tab2ButtonY = tab2ButtonY;
        this.tab3ButtonX = tab3ButtonX;
        this.tab3ButtonY = tab3ButtonY;
    }

    public static ScreenSettingsGroup fromId(int id){
        for(ScreenSettingsGroup group : values()){
            if(group.ID==id){
                return group;
            }
        }
        return BASIC;
    }

    private static Identifier id(String texture){
        return SpudaciousShops.id("textures/gui/"+texture+".png");
    }


    public record ScreenSettings(
            Identifier textureID,
            int backgroundWidth, int backgroundHeight,
            int playerInvX, int playerInvY,
            int shopInvX, int shopInvY,
            int tradeInvX, int tradeInvY) {}
}
