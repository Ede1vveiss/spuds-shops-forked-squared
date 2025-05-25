package net.spudacious5705.shops.block.custom;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.spudacious5705.shops.block.entity.ModBlockEntities;
import net.spudacious5705.shops.block.entity.RugShopEntity;
import org.jetbrains.annotations.Nullable;


public class RugShopBlock extends AbstractShopBlock{

    public static final BooleanProperty CONNECTED_NORTH = Properties.NORTH;
    public static final BooleanProperty CONNECTED_EAST = Properties.EAST;
    public static final BooleanProperty CONNECTED_SOUTH = Properties.SOUTH;
    public static final BooleanProperty CONNECTED_WEST = Properties.WEST;

    public static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

    public RugShopBlock(Settings settings) {
        super(settings, RugShopBlockState::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RugShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    private static BlockState importProperties(BlockState defaultState, BlockState originalState) {
        return defaultState
                .with(CONNECTED_NORTH, originalState.get(CONNECTED_NORTH))
                .with(CONNECTED_EAST, originalState.get(CONNECTED_EAST))
                .with(CONNECTED_SOUTH, originalState.get(CONNECTED_SOUTH))
                .with(CONNECTED_WEST, originalState.get(CONNECTED_WEST))
                .with(BREAKABLE, originalState.get(BREAKABLE));
    }

    @Override
    protected AbstractShopBlockState defaultStateProperties(AbstractShopBlockState state) {
        return (AbstractShopBlockState) state
                .with(CONNECTED_NORTH, false)
                .with(CONNECTED_EAST, false)
                .with(CONNECTED_SOUTH, false)
                .with(CONNECTED_WEST, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED_NORTH).add(CONNECTED_EAST).add(CONNECTED_SOUTH).add(CONNECTED_WEST);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(
                type,
                ModBlockEntities.RUG_SHOP_ENTITY,
                world.isClient() ?
                        (world1, pos, state1, blockEntity) -> blockEntity.renderTick()
                        :
                        (world1, pos, shopState, blockEntity) -> blockEntity.serverTick((ServerWorld) world1, pos, (RugShopBlockState) shopState)
        );
    }

    public static class RugShopBlockState extends AbstractShopBlockState {
        public RugShopBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
            super(block, immutableMap, mapCodec);
        }

        @Override
        public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
            return SHAPE;
        }

        @Override
        public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
            return SHAPE;
        }

        @Override
        public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
            return SHAPE;
        }

        @Override
        public BlockState rotate(BlockRotation rotation) {
            return this;
        }

        @Override
        public BlockState mirror(BlockMirror mirror) {
            return this;
        }

        @Override
        public BlockState getStateForNeighborUpdate(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
            if(!world.isClient()){
                BooleanProperty CONNECTION = switch(direction){
                    case NORTH -> CONNECTED_NORTH;
                    case SOUTH -> CONNECTED_SOUTH;
                    case EAST -> CONNECTED_EAST;
                    case WEST -> CONNECTED_WEST;
                    default -> null;
                };

                if(CONNECTION != null){
                    return this.with(
                        CONNECTION,
                        neighborState instanceof RugShopBlockState
                );
                }
            }
            return this;
        }

        @Override
        protected boolean isStateReplacedValid(BlockState newState) {
            return newState instanceof RugShopBlockState;
        }

        @Override
        public boolean canPlaceAt(WorldView world, BlockPos pos) {
            return world.getBlockState(pos.down()).isSideSolid(world,pos.up(),Direction.UP, SideShapeType.FULL);
        }
    }
}
