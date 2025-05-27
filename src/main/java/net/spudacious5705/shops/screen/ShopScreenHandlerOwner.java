package net.spudacious5705.shops.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;

import java.util.ArrayList;
import java.util.List;

public class ShopScreenHandlerOwner extends ScreenHandler {
    private final AbstractShopEntity.InventoryDelegate shopInventory;
    //private final PropertyDelegate propertyDelegate;

    private final PlayerInventory playerInventory;

    final int SCREEN_TEXTURE_ID;

    private final List<TogglableSlot> playerInvSlots = new ArrayList<>();
    private final List<TogglableSlot> tab1Slots = new ArrayList<>();
    private final List<TogglableSlot> tab2Slots = new ArrayList<>();


    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;
    private static final int profit_itemStacks_start = 54;
    private static final int profit_itemStacks_range = 21;
    private static final int stock_itemStacks_start = 0;
    private static final int stock_itemStacks_range = 53;

    public ShopScreenHandlerOwner(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {//clientInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER, syncId);
        BlockPos pos = buf.readBlockPos();
        boolean openTop = buf.readBoolean();
        PlayerEntity player = playerInventory.player;

        this.playerInventory = playerInventory;
        playerInventory.onOpen(playerInventory.player);

        if(player.getWorld().getBlockEntity(pos) instanceof AbstractShopEntity shop) {
            AbstractShopEntity.InventoryDelegate inventoryDelegate = null;

            if (openTop) {
                inventoryDelegate = shop.getOtherInventoryDelegate(player);
            }

            if (inventoryDelegate == null) {
                inventoryDelegate = shop.getInventoryDelegate(player);
            }
            checkSize(inventoryDelegate, 78 );
            this.shopInventory = inventoryDelegate;

            this.SCREEN_TEXTURE_ID = shop.getTextureId();

            finishSetup();
        } else {
            MinecraftClient.getInstance().setScreen(null);
            this.shopInventory = null;
            this.SCREEN_TEXTURE_ID = 0;
        }
    }

    public ShopScreenHandlerOwner(int syncId, PlayerInventory playerInventory, AbstractShopEntity.InventoryDelegate inventory, int SCREEN_TEXTURE_ID) {//serverInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER, syncId);
        checkSize(inventory, 78 );
        this.shopInventory = inventory;
        this.SCREEN_TEXTURE_ID = SCREEN_TEXTURE_ID;
        this.playerInventory = playerInventory;
        playerInventory.onOpen(playerInventory.player);
        finishSetup();
    }

    private void finishSetup() {


        addPlayerInventory(playerInventory);

        addShopInventory();

        new shop_trade_slot(shopInventory, PAYMENT_SLOT, 23, 11);
        new shop_trade_slot(shopInventory, VENDING_SLOT, 23, 49);

        activeTab = 0;



    }

    private int activeTab = 0;
    void updateTabSelection(int tab){
        activeTab = tab;
        updateTabSelection();
    }
    void updateTabSelection(){
        if(activeTab == 0){
            tab1Slots.forEach(TogglableSlot::enable);
            playerInvSlots.forEach(TogglableSlot::enable);

            tab2Slots.forEach(TogglableSlot::disable);
        }
        if(activeTab == 1){
            tab2Slots.forEach(TogglableSlot::enable);

            tab1Slots.forEach(TogglableSlot::disable);
            playerInvSlots.forEach(TogglableSlot::disable);
        }
    }

    public void addShopInventory(){
        int offsetx = 60;
        int offsety = 10;

        for (int i = 0; i<6; ++i){
            for (int j = 0; j<9; ++j){
                createShopInvSlot(j+i*9,offsetx + j*18,offsety + i*18);
            }
        }
        offsetx += -44;
        offsety += 112;

        for (int i = 0; i<11; ++i){
            createShopInvSlot(profit_itemStacks_start+i,offsetx+i*18,offsety);
        }
        offsety += 18;

        for (int i = 0; i<11; ++i){
            createShopInvSlot(profit_itemStacks_start+11+i,offsetx+i*18,offsety);
        }

    }

    private void createShopInvSlot(int index, int x, int y){
        TogglableSlot slot = new TogglableSlot(shopInventory,index,x,y);
        tab1Slots.add(slot);
        addSlot(slot);
    }


    private void addPlayerInventory(PlayerInventory playerInventory) {

        int offsetx = 33;
        int offsety = 172;

        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                new player_slot(playerInventory, l + i * 9 + 9, offsetx + l * 18, offsety + i * 18);
            }
        }
        offsety += 58;
        for (int i = 0; i < 9; ++i) {
            new player_slot(playerInventory, i, offsetx + i * 18, offsety);
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (!slot.hasStack()) {return newStack;}
        ItemStack originalStack = slot.getStack();
        newStack = originalStack.copy();

        if(this.slots.get(invSlot) instanceof shop_trade_slot){return ItemStack.EMPTY;}

        if(this.slots.get(invSlot) instanceof player_slot){
            if (!this.insertItem(originalStack, 36, 90, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.insertItem(originalStack, 0, 35, false)) {
                return ItemStack.EMPTY;
            }
        }


        if (originalStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return newStack;

    }

    @Override
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        return super.insertItem(stack, startIndex, endIndex, fromLast);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.shopInventory.canPlayerUse(player);
    }

    public int textureId() {
        return this.SCREEN_TEXTURE_ID;
    }

    class TogglableSlot extends Slot {
        private boolean toggled = true;
        public TogglableSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isEnabled() {
            return toggled;
        }

        public void enable(){
            toggled=true;
        }

        public void disable(){
            toggled=false;
        }
    }

    ;

    class player_slot extends TogglableSlot {
        /**
         *
         * Automatically adds itself to the neccecary lists
         */
        public player_slot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            playerInvSlots.add(this);
            addSlot(this);
        }

        public boolean isPlayerSlot() {return true;}
    }

    class shop_trade_slot extends TogglableSlot {

        public final AbstractShopEntity.InventoryDelegate inventory;

        public shop_trade_slot(AbstractShopEntity.InventoryDelegate inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            this.inventory = inventory;
            tab1Slots.add(this);
            addSlot(this);
        }

        @Override
        public ItemStack takeStack(int amount) {
            super.takeStack(amount);

            return ItemStack.EMPTY;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return this.getStack().getItem() == stack.getItem() || this.getStack().isEmpty();
        }

        @Override
        public ItemStack insertStack(ItemStack newStack, int count) {
            this.inventory.insertTradeStack(this.getIndex(),newStack, count);
            return newStack;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return false;
        }
    }
}