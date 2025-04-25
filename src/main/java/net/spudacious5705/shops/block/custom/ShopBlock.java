package net.spudacious5705.shops.block.custom;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;

import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.TickPriority;
import net.spudacious5705.shops.block.entity.ModBlockEntities;
import net.spudacious5705.shops.block.entity.ShopEntity;
import net.spudacious5705.shops.item.custom.ShopItem;
import net.spudacious5705.shops.model.CushionResources;
import net.spudacious5705.shops.properties.Colour;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.properties.PermissionLevel;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;


public class ShopBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<Colour> CUSHION_COLOUR = ModProperties.CUSHION_COLOUR;
    public static final BooleanProperty BREAKABLE = ModProperties.BREAKABLE;

    public static final VoxelShape CULLING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);


    public static final VoxelShape BASE_NORTH = Block.createCuboidShape(0.0, 0.0, 2.0, 16.0, 6.0, 16.0);
    public static final VoxelShape BASE_EAST = Block.createCuboidShape(0.0, 0.0, 0.0, 14.0, 6.0, 16.0);
    public static final VoxelShape BASE_SOUTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 14.0);
    public static final VoxelShape BASE_WEST = Block.createCuboidShape(2.0, 0.0, 0.0, 16.0, 6.0, 16.0);

    public static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1.0, 6.0, 3.0, 15.0, 9.0, 12.0),
            Block.createCuboidShape(1.0, 9.0, 8.0, 15.0, 11.5, 15.0),
            BASE_NORTH
    );
    public static final VoxelShape EAST_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(4.0, 6.0, 1.0, 13.0, 9.0, 15.0),
            Block.createCuboidShape(1.0, 9, 1.0, 8.0, 11.5, 15.0),
            BASE_EAST
    );
    public static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1.0, 6.0, 4.0, 15.0, 9.0, 13.0),
            Block.createCuboidShape(1.0, 9, 1.0, 15.0, 11.5, 8.0),
            BASE_SOUTH
    );
    public static final VoxelShape WEST_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3.0, 6.0, 1.0, 12.0, 9.0, 15),
            Block.createCuboidShape(8.0, 9.0, 1.0, 15, 11.5, 15),
            BASE_WEST
    );

    protected final StateManager<Block, BlockState> shopStateManager;

    public ShopBlock(Settings settings) {
        super(settings);
        StateManager.Builder<Block, BlockState> builder = new StateManager.Builder<>(this);
        this.appendProperties(builder);
        this.shopStateManager = builder.build(Block::getDefaultState, ShopBlockState::new);

        this.setDefaultState(
                this.shopStateManager.getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(CUSHION_COLOUR, Colour.WHITE)
                        .with(BREAKABLE, false)
        );
    }

    @Override
    public StateManager<Block, BlockState> getStateManager() {
        return this.shopStateManager;
    }

    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapesForStates(Function<BlockState, VoxelShape> stateToShape) {
        return this.shopStateManager.getStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), stateToShape));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Colour colour = getColourUsed(ctx);

        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(CUSHION_COLOUR, colour)
                .with(BREAKABLE, false);
    }

    private static Colour getColourUsed(ItemPlacementContext ctx){
        PlayerEntity player = ctx.getPlayer();
        if(player != null) {
            Iterator<ItemStack> iterator = player.getHandItems().iterator();
            if (iterator.next().getItem() instanceof ShopItem item) {
                return item.colour;
            } else if (iterator.next().getItem() instanceof ShopItem item) {
                return item.colour;
            }
        }
        return Colour.RED;//default colour
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(CUSHION_COLOUR).add(BREAKABLE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(placer != null) {
            if (placer instanceof PlayerEntity player) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof ShopEntity shopEntity) {
                    shopEntity.setOwner(player);
                }
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShopEntity(pos, state);
    }

    private static PermissionLevel userSignIn(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ShopEntity shopEntity) {
            return shopEntity.userSignIn(player);
        }
        return PermissionLevel.CUSTOMER;
    }

    public static class ShopBlockState extends BlockState{

        public ShopBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
            super(block, immutableMap, mapCodec);
        }

        @Override
        public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
            if(this.getBlock() instanceof ShopBlock){
                world.getBlockEntity(pos);
            }
            ShopEntity shop = (ShopEntity) world.getBlockEntity(pos);
            assert shop != null;
            if(shop.canBreak(player)){
                world.setBlockState(pos,this.withIfExists(BREAKABLE,true));
                world.scheduleBlockTick(pos, this.owner,140, TickPriority.EXTREMELY_HIGH);
            }else{
                world.setBlockState(pos,this.withIfExists(BREAKABLE,false));
                if(world.isClient()){
                    player.sendMessage(shop.cantBreakMessage(),true);
                }
            }

            super.onBlockBreakStart(world, pos, player);
        }


        @Override
        public void onBlockAdded(World world, BlockPos pos, BlockState state, boolean notify) {
            super.onBlockAdded(world, pos, state, notify);
        }



        @Override
        public float getHardness(BlockView world, BlockPos pos) {
            if(this.get(BREAKABLE))return 2.0f;
            return -1f;
        }

        @Override
        public void scheduledTick(ServerWorld world, BlockPos pos, Random random) {
            world.setBlockState(pos,this.withIfExists(BREAKABLE,false));
        }

        @Override
        public ActionResult onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
            BlockPos pos = hit.getBlockPos();
            if (world.isClient) return ActionResult.SUCCESS;

            ItemStack stack = player.getStackInHand(hand);

            BlockEntity be = world.getBlockEntity(pos);

            if(!(be instanceof ShopEntity)) return ActionResult.FAIL;

            PermissionLevel perm = userSignIn(world, pos, player);

            if(!stack.isEmpty() && perm.canEditTrades()){
                if(onUseWithItem(stack,this.asBlockState(),world,pos,player)) return ActionResult.SUCCESS;
            }

            NamedScreenHandlerFactory screenHandlerFactory = (ShopEntity)world.getBlockEntity(pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }

            return ActionResult.SUCCESS;
        }

        @Override
        public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
            return CULLING_SHAPE;
        }

        @Override
        public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
            return switch (this.get(FACING)) {
                case NORTH -> NORTH_SHAPE;
                case SOUTH -> SOUTH_SHAPE;
                case EAST -> EAST_SHAPE;
                case WEST -> WEST_SHAPE;
                default -> CULLING_SHAPE;
            };
        }

        @Override
        public BlockState rotate(BlockRotation rotation) {
            return this.with(FACING, rotation.rotate(this.get(FACING)));
        }

        @Override
        public BlockState mirror(BlockMirror mirror) {
            return this.rotate(mirror.getRotation(this.get(FACING)));
        }

        @Override
        public void onStateReplaced(World world, BlockPos pos, BlockState newState, boolean moved) {
            if (this.getBlock() != newState.getBlock()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null) {
                    if (blockEntity instanceof ShopEntity shopEntity) {
                        shopEntity.itemScatter(world,pos);
                        world.updateComparators(pos, this.getBlock());
                    }
                }
                world.removeBlockEntity(pos);
            }
        }

        @Override
        public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
            return switch (this.get(FACING)) {
                case NORTH -> BASE_NORTH;
                case SOUTH -> BASE_SOUTH;
                case EAST -> BASE_EAST;
                case WEST -> BASE_WEST;
                default -> CULLING_SHAPE;
            };
        }

        public boolean unbreakable() {
            return !this.get(BREAKABLE);
        }

        public void makeUnbreakable(ServerWorld world, BlockPos pos) {
            world.setBlockState(pos,this.withIfExists(BREAKABLE,false));
        }
    }//end of ShopBlockState


    protected static boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
            if(!stack.isEmpty()){
                Item item = stack.getItem();
                if(CushionResources.DYE_MAP.containsKey(item)){
                    CushionResources.cushionColourGroup group = CushionResources.DYE_MAP.get(item);
                    if(state.get(CUSHION_COLOUR) != group.colour()) {
                        stack.decrement(1);
                        world.setBlockState(pos, state.with(CUSHION_COLOUR, group.colour()));
                        world.playSound(player,pos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS);
                        attemptRenderDataForceUpdate(world, pos);
                        return true;
                    }
                } else if (CushionResources.WOOL_MAP.containsKey(item)) {
                    CushionResources.cushionColourGroup group = CushionResources.WOOL_MAP.get(item);
                    Colour originalColour = state.get(CUSHION_COLOUR);
                    if(originalColour != group.colour()) {
                        world.setBlockState(pos, state.with(CUSHION_COLOUR, group.colour()));
                        stack.decrement(1);
                        group = CushionResources.COLOUR_MAP.get(originalColour);
                        ItemStack releaseStack = new ItemStack(group.wool(),1);
                        world.spawnEntity(new ItemEntity(world,pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,releaseStack,0f,0.1f,0f));
                        world.playSound(player,pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS);
                        attemptRenderDataForceUpdate(world, pos);
                        return true;
                    }
                }
        }
        return false;
    }

    private static void attemptRenderDataForceUpdate(World world,BlockPos pos){
        if(world.isClient()){
        if(world.getBlockEntity(pos) instanceof ShopEntity shop) {
            if(world.isClient())shop.forceUpdateRenderData();
        }}
    }

    private final Map<Colour,ShopItem> dropMap = new HashMap<>();

    public void addDropItem(ShopItem shopItem, Colour colour) {
        dropMap.put(colour, shopItem);
    }

    public ShopItem getColouredShopItem(Colour colour) {
        return dropMap.getOrDefault(colour,getDefaultColouredShopItem());
    }

    public ShopItem getDefaultColouredShopItem() {
        return dropMap.get(Colour.RED);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(type != ModBlockEntities.SHOP_ENTITY){
            return null;
        }
        if(world.isClient()) {
            return checkType(type, ModBlockEntities.SHOP_ENTITY,
                    (world1, pos, state1, blockEntity) -> blockEntity.renderTick());
        }
        return checkType(type, ModBlockEntities.SHOP_ENTITY,
                (world1, pos, shopState, blockEntity) -> blockEntity.serverTick((ServerWorld) world1, pos, (ShopBlockState) shopState));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof ShopEntity shop){
            if(!shop.canBreak(player)){
                if(world.isClient()) {
                    player.sendMessage(shop.cantBreakMessage(), true);
                }
                return;
            }
        }
        this.spawnBreakParticles(world, player, pos, state);
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));
    }
}



