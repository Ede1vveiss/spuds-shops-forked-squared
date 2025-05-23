package net.spudacious5705.shops.block.custom;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.spudacious5705.shops.block.entity.ModBlockEntities;
import net.spudacious5705.shops.block.entity.WindowSillShopEntity;
import net.spudacious5705.shops.util.MaterialShopPair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WindowSillShopBlock extends AbstractShopBlock{

    private final Item stoneType;
    private static final List<MaterialShopPair<WindowSillShopBlock>> possibleStoneTypes = new ArrayList<>();

    public static final VoxelShape SHAPE = Block.createCuboidShape(0, -1.0, -1.0, 16.0, 2.0, 17.0);
    public static final VoxelShape SHAPE_ROTATED = Block.createCuboidShape(-1.0, -1.0, 0, 17.0, 2.0, 16.0);

    public WindowSillShopBlock(Settings settings, Item stoneType) {
        super(settings, WindowShopBlockState::new);
        this.stoneType = stoneType;
        possibleStoneTypes.add(new MaterialShopPair<>(stoneType,this));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WindowSillShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if(!stack.isEmpty()){
            Item item = stack.getItem();
            if(item != this.stoneType){

                MaterialShopPair<WindowSillShopBlock> pair = MaterialShopPair.findShopByMaterial(possibleStoneTypes,item);

            if(pair != null){
                world.spawnEntity(new ItemEntity(world,pos.getX()+0.5f,pos.getY(),pos.getZ()+0.5f,this.stoneType.getDefaultStack(),0f,0.1f,0f));
                BlockState newBlockState = importProperties(pair.shop().getDefaultState(), state);
                world.setBlockState(pos, newBlockState);
                return true;
                }}}

        return false;
    }

    private static BlockState importProperties(BlockState defaultState, BlockState originalState) {
        return defaultState
                .with(FACING, originalState.get(FACING))
                .with(BREAKABLE, originalState.get(BREAKABLE));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(
                type,
                ModBlockEntities.WINDOW_SHOP_ENTITY,
                world.isClient() ?
                        (world1, pos, state1, blockEntity) -> blockEntity.renderTick()
                        :
                        (world1, pos, shopState, blockEntity) -> blockEntity.serverTick((ServerWorld) world1, pos, (WindowSillShopBlock.WindowShopBlockState) shopState)
        );
    }

    public static class WindowShopBlockState extends AbstractShopBlockState {
        public WindowShopBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
            super(block, immutableMap, mapCodec);
        }

        @Override
        public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
            return getShape();
        }

        @Override
        public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
            return getShape();
        }

        @Override
        public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
            return getShape();
        }

        private VoxelShape getShape(){
            return switch (this.get(FACING)) {
                case EAST, WEST -> SHAPE_ROTATED;
                default -> SHAPE;
            };
        }

        @Override
        protected boolean isStateReplacedValid(AbstractShopBlockState newShopState) {
            return newShopState instanceof WindowShopBlockState;
        }
    }
}
