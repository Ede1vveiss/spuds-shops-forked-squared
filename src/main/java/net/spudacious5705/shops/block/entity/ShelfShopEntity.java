package net.spudacious5705.shops.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.spudacious5705.shops.block.custom.ShelfShopBlock.SHELVES_ENABLED;
import static net.spudacious5705.shops.screen.ShopScreenHandlerOwner.canUseInTrade;

public class ShelfShopEntity extends AbstractShopEntity{

    private final DefaultedList<ItemStack> itemStacksTop = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);

    @Override
    protected @NotNull DefaultedList<ItemStack> otherInventory() {
        return itemStacksTop;
    }

    @Override
    public void forceUpdateRenderData() {
        super.forceUpdateRenderData();
        rendererData2.update();

    }

    @Override
    public void itemScatter(World world, BlockPos pos) {
        super.itemScatter(world, pos);
        itemStacksTop.set(PAYMENT_SLOT, ItemStack.EMPTY);
        itemStacksTop.set(VENDING_SLOT, ItemStack.EMPTY);
        ItemScatterer.spawn(world, pos, itemStacksTop);

    }

    @Override
    public void renderTick() {
        this.rendererData.onTick();
        this.rendererData2.onTick();
    }

    public ShelfShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHELF_SHOP_ENTITY, pos, state, -0.3f);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.rendererData2 = new RendererData2(itemStacksTop, this, true);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void createRendererData() {
        this.rendererData = new RendererData2(itemStacks,this,false);
    }

    @Environment(EnvType.CLIENT)
    protected RendererData2 rendererData2;


    @Environment(EnvType.CLIENT)
    public RendererData2 rendererData2(){return  rendererData2;}

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected boolean isShopTradeless() {
        boolean bl1 = super.isShopTradeless();
        boolean bl2 = this.itemStacksTop.get(PAYMENT_SLOT).isEmpty();
        boolean bl3 = this.itemStacksTop.get(VENDING_SLOT).isEmpty();
        return bl1 && (bl2 || bl3);
    }


    boolean isShelfActive(boolean shelf){
        SlabType t = getCachedState().get(SHELVES_ENABLED);
        if(t == SlabType.DOUBLE)return true;
        if(shelf)return t == SlabType.TOP;
        return t == SlabType.BOTTOM;
    }

    public static class RendererData2 extends RendererData<ShelfShopEntity>{

        public final float itemLrotation = (float)((Math.random()*90)+80f);
        public final float itemRrotation = (float)((Math.random()*90)+80f);
        private final boolean isTopShelf;

        public RendererData2(@NotNull DefaultedList<ItemStack> inv, @NotNull ShelfShopEntity shop, boolean belongsToTopShelf) {
            super(inv, shop);
            this.isTopShelf = belongsToTopShelf;
        }

        @Override
        protected void functionalCheck() {
            if(shop.isShelfActive(isTopShelf)){
                this.shopFunctional = (shop.isShopFunctional() && !(this.inventory.get(PAYMENT_SLOT).isEmpty()
                        || this.inventory.get(VENDING_SLOT).isEmpty()));
            } else {
                this.shopFunctional = false;
            }
        }

        @Override
        protected boolean paymentRegisterFull(){
            if(isTopShelf){
                return shop.paymentRegister2Full();
            }
            return shop.paymentRegisterFull();
        }

        @Override
        protected boolean outOfStock(){
            if(isTopShelf) {
                return shop.outOfStock2();
            }
            return shop.outOfStock();
        }

    }

    private boolean outOfStock2() {
        int stock = 0;
        ItemStack vend = itemStacksTop.get(VENDING_SLOT);
        ItemStack stockStack;
        for (int i = 0; i <= STOCK_END; i++) {
            stockStack = itemStacksTop.get(i);
            if(canUseInTrade(vend,stockStack)){
                stock += stockStack.getCount();
                if(stock >= vend.getCount()){return false;}
            }
        }
        return true;
    }

    private boolean paymentRegister2Full() {
            int space = 0;
            ItemStack paymentSlot;
            ItemStack paymentType = itemStacksTop.get(PAYMENT_SLOT);
            int price = paymentType.getCount();
            for(int i = PROFIT_END; i > STOCK_END; i--) {
                paymentSlot = itemStacksTop.get(i);
                if(paymentSlot.isEmpty()){
                    space += paymentType.getMaxCount();
                } else if(canUseInTrade(paymentSlot,paymentType)){
                    space += paymentSlot.getMaxCount() - paymentSlot.getCount();
                }
                if(space >= price){return false;}
            }
            return true;

    }

    @Override
    public int getTextureId() {
        return 0;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();

        for (int i = 0; i < itemStacksTop.size(); i++) {
            ItemStack itemStack = itemStacksTop.get(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("SlotTwo", (byte)i);
                itemStack.writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }

        if (!nbtList.isEmpty()) {
            nbt.put("ItemsTwo", nbtList);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList nbtList = nbt.getList("ItemsTwo", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("SlotTwo") & 255;
            if (j < itemStacksTop.size()) {
                itemStacksTop.set(j, ItemStack.fromNbt(nbtCompound));
            }
        }
        super.readNbt(nbt);
    }
    
    
}
