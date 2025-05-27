package net.spudacious5705.shops.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;

import static net.minecraft.block.Block.dropStack;

public class ShopScreenHandlerCustomer extends ScreenHandler {
    private final AbstractShopEntity.InventoryDelegate shopInventory;
    //private final PropertyDelegate propertyDelegate;
    final int SCREEN_TEXTURE_ID;
    private final PlayerInventory playerInventory;



    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;
    private static final int STOCK_END = 53;
    private static final int PROFIT_END = 75;



    public ShopScreenHandlerCustomer(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {//clientInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER, syncId);
        BlockPos pos = buf.readBlockPos();
        boolean openTop = buf.readBoolean();
        PlayerEntity player = playerInventory.player;

        this.playerInventory = playerInventory;

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

    public ShopScreenHandlerCustomer(int syncId, PlayerInventory playerInventory, AbstractShopEntity.InventoryDelegate inventory, int SCREEN_TEXTURE_ID) {//serverInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER, syncId);
        this.shopInventory = inventory;
        this.SCREEN_TEXTURE_ID = SCREEN_TEXTURE_ID;
        this.playerInventory = playerInventory;
        finishSetup();
    }

    private void finishSetup(){

        playerInventory.onOpen(playerInventory.player);


        addPlayerInventory(playerInventory);
        addCustomerInventory();

    }

    public int textureId() {
        return this.SCREEN_TEXTURE_ID;
    }

    public boolean hasEnoughStock(){
        int stock = 0;
        Item displayItem = getDisplayItem();
        for (int i = 0; i <= STOCK_END; i++) {
            if(shopInventory.getStack(i).getItem() == displayItem){
                stock += shopInventory.getStack(i).getCount();
                if(stock >= shopInventory.getStack(VENDING_SLOT).getCount()){return true;};
            }
        }
        return false;
    }

    public boolean spaceForMoney(){
        int space = 0;
        ItemStack stack;
        for(int i = PROFIT_END; i > STOCK_END; i--) {
            stack = shopInventory.getStack(i);
            if(stack.isEmpty()){
                space += 64;
            } else if (stack.isOf(getPaymentType())) {
                space += 64 -stack.getCount();
            }
            if(space >= getPrice()){return true;}
        }
        return false;
    }

    private Item getDisplayItem(){
        return shopInventory.getStack(VENDING_SLOT).getItem();
    }

    private int getPrice(){
        return shopInventory.getStack(PAYMENT_SLOT).getCount();
    }

    private Item getPaymentType(){
        return shopInventory.getStack(PAYMENT_SLOT).getItem();
    }

    public void addCustomerInventory() {
        this.addSlot(new shop_payment_slot(shopInventory, PAYMENT_SLOT, 80, 11));
        this.addSlot(new shop_vendor_slot(shopInventory, VENDING_SLOT, 80, 59, this));
    }

    public void addPlayerInventory(PlayerInventory playerInv) {
        addPlayerInventory(playerInv,8,84);
    }

    private void addPlayerInventory(PlayerInventory playerInventory, int offsetx, int offsety) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, offsetx + l * 18, offsety + i * 18));
            }
        }
        offsety += 58;
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, offsetx + i * 18, offsety));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (!slot.hasStack()) {return newStack;}
        ItemStack originalStack = slot.getStack();
        newStack = originalStack.copy();

        if(!(this.slots.get(invSlot) instanceof ShopScreenHandlerCustomer.shop_vendor_slot vendorSlot)){return ItemStack.EMPTY;}
        if(!vendorSlot.canTakeItems(player)){return ItemStack.EMPTY;}



        if (!this.insertItem(newStack, 0, 36, false)) {
            return ItemStack.EMPTY;
        }

        takeStack(64);

        if (!originalStack.isEmpty()) {
            dropStack(player.getWorld(), player.getBlockPos(), newStack);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    static class shop_payment_slot extends Slot {

        public shop_payment_slot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return false;
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            return stack;
        }

        @Override
        public ItemStack insertStack(ItemStack stack) {
            return stack;
        }

        @Override
        public void setStack(ItemStack stack) {
        }
    }

    private boolean hasEnoughMoneyyyyyyyy(PlayerEntity player) {
        Inventory inv = player.getInventory();
        Item paymentType = this.shopInventory.getPaymentType();
        int money = 0;
        for (int i = 0; i <= 36; i++) {
            if(inv.getStack(i).getItem() == paymentType){
                money += inv.getStack(i).getCount();
                if(money >= this.shopInventory.getPrice()){return true;};
            }
        }
        return false;
    }

    private void extractItems(int quantity, int endPoint, Item item, Inventory inv){

        for(int i = STOCK_END; i >= 0; i--){

            if(inv.getStack(i).getItem() != item){continue;}

            if(inv.getStack(i).getCount() >= quantity){
                inv.getStack(i).decrement(quantity);
                break;
            } else {
                quantity -= inv.getStack(i).getCount();
                inv.removeStack(i);
            }
        }
    }



    public ItemStack takeStack(int amount) {
        int vendQuantity = shopInventory.getStack(VENDING_SLOT).getCount();
        if(amount<vendQuantity){return ItemStack.EMPTY;}

        //extract from stockpile

        extractItems(vendQuantity, STOCK_END, this.shopInventory.getDisplayItem(), this.shopInventory);

        //extract payment from player inventory

        extractItems(this.shopInventory.getPrice(), 35, this.shopInventory.getPaymentType(), this.playerInventory);

        //insert payment into shop

        ItemStack stack = new ItemStack(this.shopInventory.getPaymentType(),this.shopInventory.getPrice());
        int pointer = STOCK_END;
        ItemStack shopStack;
        int space;

        while (stack.getCount() > 0){
            pointer++;
            shopStack = this.shopInventory.getStack(pointer);
            if(shopStack.isEmpty()){
                this.shopInventory.setStack(pointer, stack);
                break;
            }
            if(!shopStack.isOf(this.shopInventory.getPaymentType())){continue;}

            space = 64 - shopStack.getCount();
            if(space >= stack.getCount()){
                shopInventory.getStack(pointer).increment(stack.getCount());
                break;
            } else {
                stack.decrement(space);
                shopInventory.getStack(pointer).increment(space);
            }

        }


        return new ItemStack(shopInventory.getStack(VENDING_SLOT).getItem(), shopInventory.getStack(VENDING_SLOT).getCount());
    }

    private void message(String message){

        PlayerEntity player = playerInventory.player;
        if(player.getWorld().isClient()) {
            player.sendMessage(Text.of(message), true);
        }
    }

    static class shop_vendor_slot extends Slot {
        private final ShopScreenHandlerCustomer handler;
        public shop_vendor_slot(Inventory inventory, int index, int x, int y, ShopScreenHandlerCustomer handler1) {
            super(inventory, index, x, y);
            this.handler = handler1;

        }

        @Override
        public ItemStack takeStack(int amount) {
            return this.handler.takeStack(amount);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            if(playerEntity.getWorld().isClient()) {
                if (!handler.hasEnoughStock()) {
                    errorMessage("Shop is out of stock", playerEntity);
                    return false;
                }
                if (!handler.spaceForMoney()) {
                    errorMessage("Shop cannot store any more currency", playerEntity);
                    return false;
                }
                if (!handler.hasEnoughMoneyyyyyyyy(playerEntity)) {
                    errorMessage("You do not have enough currency", playerEntity);
                    return false;
                }
            }
            return true;
        }

        private void errorMessage(String message, PlayerEntity player){
            if(player.getWorld().isClient()) {
                player.sendMessage(Text.of(message), true);
            }
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int amount) {
            return stack;
        }
    }
}