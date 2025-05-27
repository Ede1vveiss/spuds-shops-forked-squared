package net.spudacious5705.shops.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.AbstractShopBlock;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screen.ShopScreenHandlerCustomer;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public abstract class AbstractShopEntity extends BlockEntity implements ExtendedScreenHandlerFactory{
    protected final int INV_SIZE = 78;
    protected final DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);

    public static final BooleanProperty TOP_ACTIVE = Properties.UP;
    public static final BooleanProperty BOTTOM_ACTIVE = Properties.DOWN;

    protected final float particleOffset;

    protected UUID ownerID;
    protected String ownerName;

    private ArrayList<playerID> identificationRecords;

    private record playerID(UUID uuid, String name, PermissionLevel permissionLevel){

    };

    public PermissionLevel userSignIn(PlayerEntity player) {

        if(ownerID == null){
            setNewOwner(player);
        }
        if(isOwner(player))return PermissionLevel.OWNER;
        if(player.isCreative()) return PermissionLevel.SERVER_ADMIN;
        return PermissionLevel.CUSTOMER;
    }

    private void setNewOwner(PlayerEntity player){
        if(identificationRecords.isEmpty()){
            this.ownerID = player.getUuid();
            this.ownerName = player.getEntityName();
            markDirty();
        }

        int maxValue = identificationRecords.stream()
                .mapToInt(record -> record.permissionLevel.asInt())
                .max()
                .orElse(-1);

        identificationRecords = (ArrayList<playerID>) identificationRecords.stream()
                .map(record -> record.permissionLevel.asInt() == maxValue ?
                        new playerID(record.uuid,record.name,PermissionLevel.OWNER) : record)
                .toList();

    }

    public boolean isOwner(PlayerEntity player){
        return isOwner(player.getUuid());
    }

    public boolean isOwner(UUID id) {
        if(ownerID == null){return true;}
        return id.compareTo(ownerID) == 0;
    }

    protected final void clearAllPermissions(){
        this.ownerName = null;
        this.ownerID = null;
    }

    public Direction getCachedFacingDirection(){
        Direction direction = this.getCachedState().get(Properties.HORIZONTAL_FACING);
        if(direction == null) return Direction.NORTH;
        return direction;
    }

    public abstract int getTextureId();

    public final class InventoryDelegate implements Inventory{

        private final AbstractShopEntity shop;
        private final DefaultedList<ItemStack> items;
        private final PermissionLevel permissions;

        public InventoryDelegate(AbstractShopEntity shop, PermissionLevel permissions, DefaultedList<ItemStack> items) {
            this.shop = shop;
            this.permissions = permissions;
            this.items = items;
        }

        public PermissionLevel checkPermissions(){
            return permissions;
        }

        @Override
        public int size() {
            return shop.INV_SIZE;
        }

        @Override
        public boolean isEmpty() {
            return items.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            if(slot>this.size()||slot<0) return ItemStack.EMPTY;

            if(slot>PROFIT_END) {
                return items.get(slot);
            }

            if(permissions.canViewShopScreen()) return items.get(slot);

            return ItemStack.EMPTY;
        }

        @Nullable
        public ItemStack trade(PlayerInventory playerInv, int purchaseID, int paymentID){
            return null;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {

            if(slot>this.size()||slot<0) return ItemStack.EMPTY;

            if(permissions.canEditTrades()) return items.get(slot).split(amount);

            if(slot<PAYMENT_SLOT){
                if(permissions.canTakeItems()) return items.get(slot).split(amount);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot) {
            return removeStack(slot,64);
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            items.set(slot, stack); //CURRENTLY UNPROTECTED
        }

        private void validatedSetStack(int slot, ItemStack stack){
            items.set(slot, stack);
        }

        @Override
        public void markDirty() {
            assert this.shop.world != null;
            shop.world.updateListeners(pos,getCachedState(),getCachedState(),3);
            shop.isShopFunctional();
            shop.markDirty();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return true;
        }


        @Override
        public void clear() {
        }

        public Item getPaymentType() {
            return AbstractShopEntity.getPaymentType(items);
        }

        public int getPrice() {
            return AbstractShopEntity.getPrice(items);
        }

        public Item getDisplayItem() {
            return AbstractShopEntity.getDisplayItem(items);
        }

        public boolean canTakeItems(PlayerEntity playerEntity) {
            return hasEnoughStock() && spaceForMoney() && playerEntity.getInventory().contains(items.get(PAYMENT_SLOT));
        }

        public void insertTradeStack(int index, ItemStack newStack, int count) {

            if(!permissions.canEditTrades())return;

            if (newStack.isEmpty()) return;

            ItemStack thisStack = items.get(index);;

            if (thisStack.isEmpty() || thisStack.getItem() != newStack.getItem()) {
                this.validatedSetStack(index,newStack.copy());
                return;
            }

            int addition = thisStack.getCount() + count;

            if(addition>+64){
                thisStack.setCount(64);
            } else {
                thisStack.setCount(addition);
            }

            this.validatedSetStack(index,thisStack);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        forceUpdateRenderData();
    }

    @NotNull
    public InventoryDelegate getInventoryDelegate(PlayerEntity player) {
        return new InventoryDelegate(this, userSignIn(player), this.itemStacks);
    }

    @Nullable
    public final InventoryDelegate getOtherInventoryDelegate(PlayerEntity player){
        DefaultedList<ItemStack> inv = otherInventory();
        
        if(inv != null){
            return new InventoryDelegate(this,userSignIn(player),inv);
        }
        
        return null;
    }

    @Nullable
    protected DefaultedList<ItemStack> otherInventory(){
        return null;
    }


    protected static final int PAYMENT_SLOT = 76;
    protected static final int VENDING_SLOT = 77;
    protected static final int STOCK_END = 53;
    protected static final int PROFIT_END = 75;


    public <S extends AbstractShopEntity>AbstractShopEntity(BlockEntityType<S> type, BlockPos pos, BlockState state, float particleOffset) {
        super(type, pos, state);
        this.particleOffset = particleOffset;
        //this.inventoryDelegate = new InventoryDelegate(this, PermissionLevel.OWNER, this.itemStacks);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.rendererData = new RendererData(itemStacks,this);
        }
    }

    public void setOwner(PlayerEntity player) {
        this.ownerID = player.getUuid();
        this.ownerName = player.getEntityName();
        markDirty();
    }

    public boolean hasEnoughStock(){
        return hasEnoughStock(itemStacks);
    }

    public static boolean hasEnoughStock(DefaultedList<ItemStack> items){
        int stock = 0;
        Item displayItem = getDisplayItem(items);
        for (int i = 0; i <= STOCK_END; i++) {
            if(items.get(i).getItem() == displayItem){
                stock += items.get(i).getCount();
                if(stock >= items.get(VENDING_SLOT).getCount()){return true;}
            }
        }
        return false;
    }

    protected boolean spaceForMoney(){return spaceForMoney(itemStacks);}
    protected static boolean spaceForMoney(DefaultedList<ItemStack> items){
        int space = 0;
        ItemStack stack;
        Item paymentType = getPaymentType(items).asItem();
        int maxStackSize = paymentType.getMaxCount();
        for(int i = PROFIT_END; i > STOCK_END; i--) {
            stack = items.get(i);
            if(stack.isEmpty()){
                space += maxStackSize;
            } else if (stack.isOf(paymentType)) {
                space += maxStackSize -stack.getCount();
            }
            if(space >= getPrice(items)){return true;}
        }
        return false;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Shop");
    }

    @Environment(EnvType.CLIENT)
    protected RendererData rendererData;

    @Environment(EnvType.CLIENT)
    public RendererData rendererData(){return  rendererData;}

    /*@Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        //runs serverside

        PermissionLevel perms = userSignIn(player);

        if(perms.canViewShopScreen()) return new ShopScreenHandlerOwner(syncId, playerInventory, this.inventoryDelegateConstructor(perms), this.getTextureId());

        if(!isShopFunctional()) {return null;}

        return new ShopScreenHandlerCustomer(syncId, playerInventory, this.inventoryDelegateConstructor(perms), this.getTextureId());
    }*/


    @Deprecated
    @Override
    public @Nullable final ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        SpudaciousShops.LOGGER.debug("Attempted to directly retrieve ScreenHandler from shop");

        return null;
    }

    public ExtendedScreenHandlerFactory createScreenHandlerFactory(boolean openTop) {

        return new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeBlockPos(pos);
                buf.writeBoolean(openTop);
            }

            @Override
            public Text getDisplayName() {
                return Text.literal("Shop");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                PermissionLevel perms = userSignIn(player);


                InventoryDelegate delegate = openTop?getOtherInventoryDelegate(player):getInventoryDelegate(player);

                if (perms.canViewShopScreen()) {
                    return new ShopScreenHandlerOwner(syncId, playerInventory, delegate, getTextureId());
                }

                if (!isShopFunctional()) {
                    return null;
                }

                return new ShopScreenHandlerCustomer(syncId, playerInventory, delegate, getTextureId());
            }
        };
    }
    
    

    @Override
    public BlockState getCachedState() {
        return super.getCachedState();
    }

    public Item getPaymentType() {
        return getPaymentType(itemStacks);
    }

    public static Item getPaymentType(DefaultedList<ItemStack> items) {
        return items.get(PAYMENT_SLOT).getItem();
    }

    protected final boolean functionalCheck(){
        if(null == ownerID){return false;}
        if(isShopTradeless())return false;
        return this.getWorld() != null;
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, itemStacks);
        if(nbt.containsUuid("owner_id")) {
            this.ownerID = nbt.getUuid("owner_id");
        }
        if(nbt.contains("owner_name")) {
            this.ownerName = nbt.getString("owner_name");
        }
        if(nbt.contains("decay_timer")) {
            this.decayTimer = nbt.getInt("decay_timer");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, itemStacks);
        if(this.ownerID != null) {
            nbt.putUuid("owner_id", this.ownerID);
            if(this.ownerName != null) {
                nbt.putString("owner_name", this.ownerName);
            }
        }
        nbt.putInt("decay_timer",this.decayTimer);
        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public abstract Packet<ClientPlayPacketListener> toUpdatePacket();

    @Override
    public final NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public Direction getFacingDirection() {
        assert this.world != null;
        if(!this.world.isClient()) {return Direction.NORTH;}
        BlockState state = this.world.getBlockState(this.pos);
        if(!(state instanceof AbstractShopBlock.AbstractShopBlockState)) {return Direction.NORTH;}
        Direction dir = state.get(Properties.HORIZONTAL_FACING);
        if(dir == null){return Direction.NORTH;}
        return dir;
    }

    public  Item getDisplayItem() {return getDisplayItem(itemStacks);}
    public static Item getDisplayItem(DefaultedList<ItemStack> items) {return items.get(VENDING_SLOT).getItem();}

    public  int getVendingQuantity() {return getVendingQuantity(itemStacks);}
    public static int getVendingQuantity(DefaultedList<ItemStack> items) {return items.get(VENDING_SLOT).getCount();}

    public int getPrice() {return getPrice(itemStacks);}
    public static int getPrice(DefaultedList<ItemStack> items) {
        if(items.get(PAYMENT_SLOT).isEmpty()){return 0;}
        return items.get(PAYMENT_SLOT).getCount();
    }

    @Environment(EnvType.CLIENT)
    public void renderTick() {
            this.rendererData.onTick();
    }

    public boolean canBreak(PlayerEntity player) {
        if(player.isCreative())return true;
        return this.isOwner(player.getUuid());
    }

    public void itemScatter(World world, BlockPos pos) {
        itemStacks.set(PAYMENT_SLOT, ItemStack.EMPTY);
        itemStacks.set(VENDING_SLOT, ItemStack.EMPTY);
        ItemScatterer.spawn(world, pos, itemStacks);
    }

    //Only call from the CLIENT
    @Environment(EnvType.CLIENT)
    public void forceUpdateRenderData() {
        rendererData.update();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(false);
    }



    public Text cantBreakMessage() {
        if(ownerName == null){
            return Text.of("Cannot break #OWNER NAME NULL#");
        }
        return Text.of("Cannot break - Owned by " + ownerName);
    }

    protected int decayTimer = -1;

    protected static final int hourInTicks = 72000;

    public boolean isShopFunctional(){
        if(functionalCheck()){
            decayTimer = -1;
            return true;
        }
        if(decayTimer < 0){
            decayTimer = 0;//starts decay timer.
        }
        return false;
    }

    protected int checkIntervalTimer = 200;//short initial check interval for server restarts

    public void serverTick(ServerWorld world, BlockPos pos, AbstractShopBlock.AbstractShopBlockState shopState) {
        if(decayTimer > -1) {
            if (decayTimer > hourInTicks) {
                if (isShopTradeless()) {
                    if (ownerID != null) {
                        clearAllPermissions();
                        shopState.makeBreakable(world, pos);
                    }
                    if (world.random.nextFloat() < 0.05f) {
                        for (int i = 0; i < 3; i++) {
                            world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, pos.getX() + .2 + world.random.nextFloat(), pos.getY() + world.random.nextFloat() + particleOffset, pos.getZ() + world.random.nextFloat(), 1, 0, 0, 0, 0);
                        }
                    }
                    breakableTicks = 140;
                } else {
                    decayTimer = -1;
                }
            } else {
                decayTimer++;
            }
        }
        checkIntervalTimer--;
        if(checkIntervalTimer<0){
            checkIntervalTimer=6000;
            //intervaled functionality check in case of bug and for startup.
            if (isShopTradeless()) {
                if(decayTimer<0){
                    decayTimer=0;
                    //start decay timer if not already started
                }
            }
        }
        if(shopState.unbreakable())return;

        if (breakableTicks > 0) {
            breakableTicks--;
            return;
        }

        if (breakableTicks == -1) {
            // Shop has become breakable; start the countdown (140 ticks)
            breakableTicks = 140;
        } else {
            shopState.makeUnbreakable(world, pos);
            breakableTicks = -1; // Reset
        }
    }

    private boolean isShopTradeless() {
        if(this.itemStacks.get(PAYMENT_SLOT).isEmpty()){return true;}
        return this.itemStacks.get(VENDING_SLOT).isEmpty();
    }

    protected int breakableTicks = -1;

    @Environment(EnvType.CLIENT)
    public static class RendererData{

        private final DefaultedList<ItemStack> inventory;
        protected AbstractShopEntity shop;
        public double lastRotation = 0;
        public double targetRotation = 0;
        public double frameRotation = 0;
        public final double doublePi = Math.PI*2;
        public String stockQuantity;
        protected final World world;
        protected Direction direction = Direction.NORTH;
        protected int rotation;
        protected float width;
        protected boolean shopFunctional = false;
        protected ItemStack paymentItem;
        protected ItemStack displayItem;
        protected String text;
        protected float tickAccumulation = 0;
        protected boolean stockDisplayType = false;
        protected boolean currencyDisplayType = true;
        protected boolean tickPassed = true;
        public boolean stockWarning = false;
        public boolean paymentWarning = false;
        public final BlockPos pos;
        protected float qWidth;

        public RendererData(DefaultedList<ItemStack> inv, AbstractShopEntity shop){
            this.inventory = inv;
            this.world = shop.getWorld();
            this.pos = shop.getPos();
            this.shop = shop;
        }

        public void update(){
            this.shopFunctional = shop.isShopFunctional();

            if(this.shopFunctional) {
                this.paymentItem = new ItemStack(getPaymentType(inventory));

                this.stockQuantity = Integer.toString(getVendingQuantity(inventory));

                this.paymentWarning = !spaceForMoney(inventory);
                this.stockWarning = !hasEnoughStock(inventory);


                this.displayItem = new ItemStack(getDisplayItem(inventory));

                //this.lightLevel = getLightLevel(shop.getWorld(), shop.getPos());

                this.text = Integer.toString(getPrice(inventory));

                this.direction = shop.getFacingDirection();

                getRotation();

                if(getPrice(inventory)>=10) {
                    this.width = -7.0f;
                } else {
                    this.width = -2.5f;
                }

                if(getVendingQuantity(inventory)>=10) {
                    this.qWidth = -7.0f;
                } else {
                    this.qWidth = -2.5f;
                }


                stockDisplayType = displayItem.getItem() instanceof BlockItem;
                currencyDisplayType = paymentItem.getItem() instanceof BlockItem;
            } else {
                this.displayItem = ItemStack.EMPTY;
                this.paymentItem = ItemStack.EMPTY;
            }
        }

        public void tickAccumulator(float tickDelta){//makes retrieving data periodic instead of on frame
            if (this.tickAccumulation == 0.0f) {

                this.tickAccumulation += (float) Math.random()*40;

                tickPassed = true;

                update();
            }

            this.tickAccumulation += tickDelta;
            if (this.tickAccumulation >= 100.0f) {
                this.tickAccumulation= 0.0f;
            }

        }

        /** MAY BE REQUIRED FOR HIGHER/LOWER VERSIONS
         * private int getLightLevel(World view, BlockPos pos) {
         *    int bLight = view.getLightLevel(LightType.BLOCK, pos);
         *    int sLight = view.getLightLevel(LightType.SKY, pos);
         * return LightmapTextureManager.pack(bLight, sLight);
         * }
         * * */


        private void getRotation(){
            this.rotation = switch (direction) {
                case EAST -> 90;
                case SOUTH -> 0;
                case WEST -> 270;
                default -> 180;
            };
        }

        public boolean shopFunctional() {
            return this.shopFunctional;
        }

        public boolean stockDisplayType() {
            return this.stockDisplayType;
        }

        public boolean currencyDisplayType() {
            return this.currencyDisplayType;
        }

        public ItemStack displayItem() {
            return this.displayItem;
        }

        public World world() {
            return this.world;
        }

        public Direction direction() {
            return this.direction;
        }

        public String text() {

            return this.text;
        }

        public float width() {
            return this.width;
        }
        public float qWidth() {
            return this.qWidth;
        }

        public ItemStack paymentType() {
            return this.paymentItem;
        }

        public int rotation() {
            return this.rotation;
        }

        public boolean updateIconRotation() {
            if(tickPassed){tickPassed = false; return true;}
            return false;
        }

        public void onTick(){
            tickPassed = true;
        }
    }


}
