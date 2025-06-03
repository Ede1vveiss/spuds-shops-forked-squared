package net.spudacious5705.shops.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import org.intellij.lang.annotations.MagicConstant;

import java.util.List;

public class ShopScreenOwner extends HandledScreen<ShopScreenHandlerOwner> {
    private final ScreenSettingsGroup SETTINGS;

    private Identifier TEXTURE;

    private static final Identifier WARNING_TEXTURE = SpudaciousShops.id("textures/gui/warning_screen.png");;

    public ShopScreenOwner(ShopScreenHandlerOwner handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 228;
        this.backgroundHeight = 254;
        this.SETTINGS = handler.getSettings();
        this.TEXTURE = SETTINGS.SELLER.textureID();
        this.x = (width - backgroundWidth)/2;
        this.y = (height - backgroundHeight)/2;

        handler.initiateWarn(this::openWarnPopup);
    }

    private ShopScreenHandlerOwner.InteractorContractRemover remover;

    private void closeWarnPopup(){
        setStateAllButtons(true);
        switchToSettingsTab();
    }

    private void endOwnership(){
        if(remover != null)remover.remove();
        this.close();
    }

    private void setStateAllButtons(boolean state){
        SettingsTabButton.visible=state;
        SellerTabButton.visible=state;
        ShopFrontTabButton.visible=state;
        WarningCancel.visible=!state;
        WarningProceed.visible=!state;
    }

    private static final Identifier COG_ICON = SpudaciousShops.id("textures/gui/settings.png");
    private static final Identifier STORAGE_ICON = SpudaciousShops.id("textures/gui/storage.png");
    private static final Identifier SHOPFRONT_ICON = SpudaciousShops.id("textures/gui/storage.png");
    private static final Identifier TAB_SELECTED = SpudaciousShops.id("textures/gui/tab_selected.png");
    private static final Identifier TAB_DESELECTED = SpudaciousShops.id("textures/gui/tab_deselected.png");
    private static final Identifier TAB_HOVER = SpudaciousShops.id("textures/gui/tab_hover.png");

    @Override
    protected void init() {
        super.init();
        playerInventoryTitleX = 1000;
        titleX = 1000;

        int posX = SETTINGS.tab1ButtonX+x;
        int posY = SETTINGS.tab1ButtonY+y;
        SellerTabButton = addDrawableChild(new TabWidget(posX, posY, 20,20, Text.of(""), this::switchToSellerTab, true, STORAGE_ICON, true));


        posX = SETTINGS.tab2ButtonX+x;
        posY = SETTINGS.tab2ButtonY+y;
        SettingsTabButton = addDrawableChild(new TabWidget(posX, posY, 20,20, Text.of(""), this::switchToSettingsTab, true, COG_ICON));


        posX = SETTINGS.tab3ButtonX+x;
        posY = SETTINGS.tab3ButtonY+y;
        ShopFrontTabButton = addDrawableChild(new TabWidget(posX, posY, 20,20, Text.of(""), this::switchToCustomerTab, true, SHOPFRONT_ICON));


        posX = 130+x;
        posY = 150+y;
        WarningCancel = addDrawableChild(new TabWidget(posX, posY, 20,20, Text.of(""), this::closeWarnPopup, false, COG_ICON));


        posX = 150+x;
        posY = 150+y;
        WarningProceed = addDrawableChild(new TabWidget(posX, posY, 20,20, Text.of(""), this::endOwnership, false, COG_ICON));

        addToolTipTexts();
    }


    private void switchToCustomerTab() {
        handler.updateTabSelectionClientside(ShopScreenHandlerOwner.CUSTOMER_TAB);
        customerGUI();
    }
    private void customerGUI(){
        TEXTURE = SETTINGS.CUSTOMER.textureID();
        SellerTabButton.unToggle();
        SettingsTabButton.unToggle();
    }

    private void switchToSellerTab(){
        handler.updateTabSelectionClientside(ShopScreenHandlerOwner.SELLER_TAB);
        sellerGUI();
    }

    private void sellerGUI(){
        TEXTURE = SETTINGS.SELLER.textureID();
        ShopFrontTabButton.unToggle();
        SettingsTabButton.unToggle();
    }

    private void switchToSettingsTab() {
        handler.updateTabSelectionClientside(ShopScreenHandlerOwner.SETTINGS_TAB);
        settingsGUI();
    }
    private void settingsGUI(){
        TEXTURE = SETTINGS.SETTINGS.textureID();
        ShopFrontTabButton.unToggle();
        SellerTabButton.unToggle();
    }

    void openWarnPopup(ShopScreenHandlerOwner.InteractorContractRemover remover){
        handler.updateTabSelectionClientside(ShopScreenHandlerOwner.WARNING_TAB);
        this.remover = remover;
        warnGUI();
    }
    private void warnGUI(){
        TEXTURE = WARNING_TEXTURE;
        setStateAllButtons(false);
    }

    @Environment(EnvType.CLIENT)
    public void updateTabSelectionResponse(int tab) {
        if(handler.updateTabSelectionResponse(tab)) {
            switch (tab) {
                case ShopScreenHandlerOwner.WARNING_TAB -> {
                    warnGUI();
                }
                case ShopScreenHandlerOwner.CUSTOMER_TAB -> {
                    customerGUI();
                }
                case ShopScreenHandlerOwner.SETTINGS_TAB -> {
                    settingsGUI();
                }
                default -> {
                    sellerGUI();
                }
            }
        }
    }


    TabWidget SellerTabButton;
    TabWidget SettingsTabButton;
    TabWidget ShopFrontTabButton;
    TabWidget WarningCancel;
    TabWidget WarningProceed;

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE, x , y, 0, 0, backgroundWidth, backgroundHeight);
    }

    private static final Identifier CONTRACT_SLOT = SpudaciousShops.id("textures/gui/contract_slot.png");

    private static final MutableText OWNER = Text.translatable("gui.spudaciousshops.owner");
    private static final MutableText MANAGER = Text.translatable("gui.spudaciousshops.manager");
    private static final MutableText SUPERVISOR = Text.translatable("gui.spudaciousshops.supervisor");
    private static final MutableText CLERK = Text.translatable("gui.spudaciousshops.clerk");

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        if(this.handler.tabSettingsSlots.get(0).isEnabled()) {
            for (int k = 0; k < this.handler.tabSettingsSlots.size(); k++) {
                Slot slot = this.handler.tabSettingsSlots.get(k);
                if (slot.isEnabled()) {
                    if (this.client != null) {
                        if (this.client.world != null) {
                            context.drawTexture(CONTRACT_SLOT, slot.x + x-8, slot.y + y-8, 1, 0.0F, 0.0F, 32, 32, 32, 32);
                        } else {
                            context.fillGradient(slot.x, slot.y, slot.x + 18, slot.y + 18, -1072689136, -804253680);
                        }
                    }
                }
            }

            if(client != null) {
                TextRenderer textRenderer = client.textRenderer;
                for(ToolTipText ttt : TEXTS){
                    ttt.render(context,textRenderer,mouseX,mouseY);
                }
            }
        }
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void addToolTipTexts(){
        int textX = x+14;
        int textY = y+64;
        @MagicConstant
        int increment = 22;

        TEXTS[0] = new ToolTipText(OWNER, textX, textY,
                List.of(
                        Text.of("§l Permissions:"),
                        Text.of("§a + Import Items: YES"),
                        Text.of("§a + Take Items: YES"),
                        Text.of("§a + Edit permissions of: ALL"),
                        Text.of("§a + Change the trade: YES"),
                        Text.of("§a + Break the shop: YES")
        ));
        textY += increment;
        TEXTS[1] = new ToolTipText(MANAGER, textX, textY,
                List.of(
                        Text.of("§l Permissions:"),
                        Text.of("§a + Import Items: YES"),
                        Text.of("§a + Take Items: YES"),
                        Text.of("§9 + Edit permissions of: Supervisor and lower"),
                        Text.of("§c - Change the trade: NO"),
                        Text.of("§c - Break the shop: NO")
                ));
        textY += increment;
        TEXTS[2] = new ToolTipText(SUPERVISOR, textX, textY,
                List.of(
                        Text.of("§l Permissions:"),
                        Text.of("§a + Import Items: YES"),
                        Text.of("§a + Take Items: YES"),
                        Text.of("§c - Edit permissions of: NONE"),
                        Text.of("§c - Change the trade: NO"),
                        Text.of("§c - Break the shop: NO")
                ));
        textY += increment;
        TEXTS[3] = new ToolTipText(CLERK, textX, textY,
                List.of(
                        Text.of("§l Permissions:"),
                        Text.of("§a + Import Items: YES"),
                        Text.of("§c - Take Items: NO"),
                        Text.of("§c - Edit permissions of: NONE"),
                        Text.of("§c - Change the trade: NO"),
                        Text.of("§c - Break the shop: NO")
                ));

    }

    private final ToolTipText[] TEXTS = new ToolTipText[4];

    private static class ToolTipText{
        private final MutableText TEXT;
        private final int X;
        private final int Y;
        private final int Xmax;
        private final int Ymax;
        private final List<Text> TOOLTIP;


        private ToolTipText(MutableText text, int x, int y, List<Text> tooltip) {
            TEXT = text;
            X = x;
            Y = y;
            Xmax = X+56;
            Ymax = Y+8;
            TOOLTIP = tooltip;
        }

        public void render(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY){
            context.drawText(textRenderer, TEXT, X, Y, 11141290, true);
            if(mouseX >= X && mouseX<=Xmax){
                if(mouseY >= Y && mouseY<=Ymax){
                    context.drawTooltip(textRenderer, TOOLTIP,mouseX,mouseY);
                }
            }
        }
    }


    interface TabSwitcher{
        void switchTo();
    }

    private class TabWidget extends ClickableWidget{

        private final TabSwitcher thisTab;

        private final Identifier ICON_TEXTURE;

        private boolean toggle;

        public TabWidget(int x, int y, int width, int height, Text message, TabSwitcher tab, boolean visible, Identifier texture) {
            this(x,y,width,height,message,tab,visible,texture,false);
        }

        public TabWidget(int x, int y, int width, int height, Text message, TabSwitcher tab, boolean visible, Identifier texture, boolean toggle) {
            super(x, y, width, height, message);
            this.thisTab = tab;
            this.visible = visible;
            this.toggle = toggle;
            this.ICON_TEXTURE = texture;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = this.getX();
            int y = this.getY();
            if(toggle){
                context.drawTexture(TAB_SELECTED,x,y,32,32,0f,0f,32,32,32,32);
            }else if(hovered){
                context.drawTexture(TAB_HOVER,x,y,32,32,0f,0f,32,32,32,32);
            }else{
                context.drawTexture(TAB_DESELECTED,x,y,32,32,0f,0f,32,32,32,32);
            }
            context.drawTexture(ICON_TEXTURE,x+3,y+9,16,16,0f,0f,16,16,16,16);
        }

        public void onClick(double mouseX, double mouseY) {
            thisTab.switchTo();
            toggle = true;
        }

        public void onRelease(double mouseX, double mouseY) {
        }

        void unToggle(){
            this.toggle = false;
        }

        void toggle(){
            this.toggle = true;
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
    }
}
