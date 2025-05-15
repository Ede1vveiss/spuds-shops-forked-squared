package net.spudacious5705.shops.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.spudacious5705.shops.block.custom.ShopBlock;
import net.spudacious5705.shops.model.CushionTextures;
import net.spudacious5705.shops.properties.Colour;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screen.ShopScreenHandlerCustomer;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShopEntity extends BlockEntity implements ExtendedScreenHandlerFactory{
    protected final int INV_SIZE = 78;
    protected final DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;

    protected RendererData rendererData;

    private int breakableTicks = 0;

    private final inventoryDelegate inventoryDelegate;

    public PermissionLevel userSignIn(PlayerEntity player) {
        if(ownerID == null){
            setOwner(player);
        }
        if(isOwner(player))return PermissionLevel.OWNER;
        if(player.isCreative()) return PermissionLevel.SERVER_ADMIN;
        return PermissionLevel.CUSTOMER;
    }

    private void clearAllPermissions(){
        this.ownerName = null;
        this.ownerID = null;
    }

    private class inventoryDelegate implements Inventory{

        private final ShopEntity shop;
        //private final PermissionLevel permissions;

        public inventoryDelegate(ShopEntity shop) {
            this.shop = shop;
            //this.permissions = permissions; ##### future improvement
        }

        @Override
        public int size() {
            return shop.INV_SIZE;
        }

        @Override
        public boolean isEmpty() {
            return shop.itemStacks.isEmpty();
        }
        @Override
        public ItemStack getStack(int slot) {
            return shop.itemStacks.get(slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {return shop.itemStacks.get(slot).split(amount);}

        @Override
        public ItemStack removeStack(int slot) {
            return shop.itemStacks.remove(slot);
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            shop.itemStacks.set(slot, stack);
        }

        @Override
        public void markDirty() {
            assert shop.world != null;
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
    }


    public Inventory getInventory() {
        return this.inventoryDelegate;
    }


    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;
    private static final int STOCK_END = 53;
    private static final int PROFIT_END = 75;

    private UUID ownerID;

    private String ownerName;


    public ShopEntity(BlockPos pos, BlockState state) {

        super(ModBlockEntities.SHOP_ENTITY, pos, state);
        this.inventoryDelegate = new inventoryDelegate(this);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return ShopEntity.this.inventoryDelegate.getStack(index).getCount();
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int size() {
                return 0;
            }
        };
    }

    public void setOwner(PlayerEntity player) {
            this.ownerID = player.getUuid();
            this.ownerName = player.getEntityName();
            markDirty();
    }

    public boolean hasEnoughStock(){
        int stock = 0;
        Item displayItem = this.getDisplayItem();
        for (int i = 0; i <= STOCK_END; i++) {
            if(this.inventoryDelegate.getStack(i).getItem() == displayItem){
                stock += this.inventoryDelegate.getStack(i).getCount();
                if(stock >= this.inventoryDelegate.getStack(VENDING_SLOT).getCount()){return true;}
            }
        }
        return false;
    }

    public boolean spaceForMoney(){
        int space = 0;
        ItemStack stack;
        for(int i = PROFIT_END; i > STOCK_END; i--) {
            stack = this.itemStacks.get(i);
            if(stack.isEmpty()){
                space += 64;
            } else if (stack.isOf(this.getPaymentType())) {
                space += 64 -stack.getCount();
            }
            if(space >= this.getPrice()){return true;}
        }
        return false;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Shop");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        //runs serverside

        int result = player.getUuid().compareTo(ownerID);

        if(result==0) return new ShopScreenHandlerOwner(syncId, playerInventory, this, this.propertyDelegate);

        if(!isShopFunctional()) {return null;}

        return new ShopScreenHandlerCustomer(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public BlockState getCachedState() {
        return super.getCachedState();
    }

    public Item getPaymentType() {
        return this.inventoryDelegate.getStack(PAYMENT_SLOT).getItem();
    }

    private boolean functionalCheck(){
        if(null == ownerID){return false;}
        if(null == this.itemStacks){return false;}
        if(this.itemStacks.get(PAYMENT_SLOT).isEmpty()){return false;}
        if(this.itemStacks.get(VENDING_SLOT).isEmpty()){return false;}
        return this.getWorld() != null;
    }

    private int decayTimer = -1;

    private static final int hourInTicks = 72000;

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
        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public Direction getFacingDirection() {
        assert this.world != null;
        if(!this.world.isClient()) {return Direction.NORTH;}
        BlockState state = this.world.getBlockState(this.pos);
        if(!(state.getBlock() instanceof ShopBlock)) {return Direction.NORTH;}
        Direction dir = state.get(Properties.HORIZONTAL_FACING);
        if(dir == null){return Direction.NORTH;}
        return dir;
    }

    public Direction getCachedFacingDirection(){
        Direction direction = this.getCachedState().get(Properties.HORIZONTAL_FACING);
        if(direction == null) return Direction.NORTH;
        return direction;
    }

    public Colour getCachedCushionColour(){
        Colour colour = this.getCachedState().get(ModProperties.CUSHION_COLOUR);
        if(colour == null) return Colour.RED;
        return colour;
    }

    public Item getDisplayItem() {return this.inventoryDelegate.getStack(VENDING_SLOT).getItem();}

    public int getVendingQuantity() {return this.inventoryDelegate.getStack(VENDING_SLOT).getCount();}

    public int getPrice() {
        if(this.itemStacks.get(PAYMENT_SLOT).isEmpty()){return 0;}
        return this.itemStacks.get(PAYMENT_SLOT).getCount();
    }

    @Environment(EnvType.CLIENT)
    public RendererData getRendererData() {
        return this.rendererData;
    }

    @Environment(EnvType.CLIENT)
    public void renderTick() {
            if(rendererData == null){
                rendererData = new RendererData(this);
            }
            this.rendererData.onTick();
    }

    public boolean isOwner(PlayerEntity player){
        return isOwner(player.getUuid());
    }

    public boolean isOwner(UUID id) {
        if(ownerID == null){return true;}
        return id.compareTo(ownerID) == 0;
    }

    public boolean canBreak(PlayerEntity player) {
        breakableTicks = 140;
        if(player.isCreative())return true;
        if(this.isOwner(player.getUuid())) return true;
        breakableTicks = 0;
        return false;
    }

    public void itemScatter(World world, BlockPos pos) {
        itemStacks.set(PAYMENT_SLOT, ItemStack.EMPTY);
        itemStacks.set(VENDING_SLOT, ItemStack.EMPTY);
        ItemScatterer.spawn(world, pos, inventoryDelegate);
    }

    public Identifier getCushionTextureID() {
        Colour colour = getCachedCushionColour();
        return CushionTextures.TEXTURE_MAP.get(colour);
    }

    //Only call from the CLIENT
    @Environment(EnvType.CLIENT)
    public void forceUpdateRenderData() {
        rendererData.update();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    public boolean canTakeItems(PlayerEntity player) {
        return hasEnoughStock() && spaceForMoney() && player.getInventory().contains(itemStacks.get(PAYMENT_SLOT));
    }

    public Text cantBreakMessage() {
        if(ownerName == null){
            return Text.of("Cannot break #OWNER NAME NULL#");
        }
        return Text.of("Cannot break - Owned by " + ownerName);
    }

    public void serverTick(ServerWorld world, BlockPos pos, ShopBlock.ShopBlockState shopState) {
        if(decayTimer > -1){
            decayTimer++;
            if(decayTimer>hourInTicks){
                if(!isShopFunctional()){
                    clearAllPermissions();
                    breakableTicks = 140;
                    shopState.makeBreakable(world, pos);
                }
            }
        }
        if(shopState.unbreakable())return;
        if(breakableTicks > 0){
            if(this.ownerID != null) {
                breakableTicks--;
                return;
            }
        }
        shopState.makeUnbreakable(world,pos);
    }

    @Environment(EnvType.CLIENT)
    public static final class RendererData{

        private final ShopEntity shop;
        public double lastRotation = 0;
        public double targetRotation = 0;
        public double frameRotation = 0;
        public final double doublePi = Math.PI*2;
        public String stockQuantity;
        private final World world;
        private Direction direction = Direction.NORTH;
        private int rotation;
        private float width;
        private boolean shopFunctional = false;
        private ItemStack paymentType;
        private ItemStack displayItem;
        private String text;
        private float tickAccumulation = 0;
        private boolean displayType = false;
        private boolean tickPassed = true;
        public boolean stockWarning = false;
        public boolean paymentWarning = false;
        public final BlockPos pos;
        private float qWidth;

        public RendererData(ShopEntity shop1){
            this.shop = shop1;
            this.world = shop1.getWorld();
            shop1.getCushionTextureID();
            pos = shop1.getPos();
        }

        public void update(){
            this.shopFunctional = shop.isShopFunctional();

            if(this.shopFunctional) {
                this.paymentType = new ItemStack(shop.getPaymentType());

                this.stockQuantity = Integer.toString(shop.getVendingQuantity());

                this.paymentWarning = !this.shop.spaceForMoney();
                this.stockWarning = !this.shop.hasEnoughStock();


                this.displayItem = new ItemStack(shop.getDisplayItem());

                //this.lightLevel = getLightLevel(shop.getWorld(), shop.getPos());

                this.text = Integer.toString(shop.getPrice());

                this.direction = shop.getFacingDirection();

                getRotation();

                if(shop.getPrice()>=10) {
                    this.width = -7.0f;
                } else {
                    this.width = -2.5f;
                }

                if(shop.getVendingQuantity()>=10) {
                    this.qWidth = -7.0f;
                } else {
                    this.qWidth = -2.5f;
                }


                displayType = displayItem.getItem() instanceof BlockItem;
            } else {
                this.displayItem = ItemStack.EMPTY;
                this.paymentType = ItemStack.EMPTY;
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

        /*private int getLightLevel(World world, BlockPos pos) {
            int bLight = world.getLightLevel(LightType.BLOCK, pos);
            int sLight = world.getLightLevel(LightType.SKY, pos);
            return LightmapTextureManager.pack(bLight, sLight);
        }*/


        private void getRotation(){
            this.rotation = switch (direction) {
                default -> 180;
                case EAST -> 90;
                case SOUTH -> 0;
                case WEST -> 270;
            };
        }

        public boolean shopFunctional() {
            return this.shopFunctional;
        }

        public boolean displayType() {
            return this.displayType;
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
            return this.paymentType;
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
