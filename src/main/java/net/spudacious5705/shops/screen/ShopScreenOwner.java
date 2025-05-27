package net.spudacious5705.shops.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;

public class ShopScreenOwner extends HandledScreen<ShopScreenHandlerOwner> {
    private static final String[] TEXTURE_BANK = {
            "textures/gui/shop_seller.png",
    };
    private final Identifier TEXTURE;

    public ShopScreenOwner(ShopScreenHandlerOwner handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 228;
        this.backgroundHeight = 254;
        this.TEXTURE = SpudaciousShops.id(TEXTURE_BANK[handler.textureId()]);
    }

    @Override
    protected void init() {
        super.init();
        playerInventoryTitleX = 1000;
        titleX = 1000;

        tab1Button = addDrawableChild(
                ButtonWidget.builder(Text.of("Inventory"),button -> handler.updateTabSelection(0))
                .dimensions(170,200,20,20)
                .build()
        );

        tab2Button = addDrawableChild(
                ButtonWidget.builder(Text.of("Settings"),button -> handler.updateTabSelection(1))
                        .dimensions(200,200,20,20)
                        .build()
        );

    }

    ButtonWidget tab1Button;
    ButtonWidget tab2Button;

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        x = (width - backgroundWidth)/2;
        y = (height - backgroundHeight)/2;
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE, x , y, 0, 0, backgroundWidth, backgroundHeight);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
