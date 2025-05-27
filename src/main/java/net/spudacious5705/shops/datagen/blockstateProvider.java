package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.SlabType;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.custom.ShelfShopBlock;

import java.util.Optional;

public class blockstateProvider extends FabricModelProvider {
    public blockstateProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        ModBlocks.registerModBlocks();
        ModBlocks.ALL_SHELF_SHOPS.forEach(shelfShopBlock -> {generateShelfBlock(generator, shelfShopBlock, shelfShopBlock.woodName);});
    }

    private void generateShelfBlock(BlockStateModelGenerator generator, ShelfShopBlock block, String woodType){

        Model model_double = makeModel("",woodType);
        Model model_top = makeModel("_top",woodType);
        Model model_bottom = makeModel("_bottom",woodType);

        TextureMap textures = new TextureMap()
                .put(TextureKey.PARTICLE, Identifier.of("minecraft","block/"+woodType+"_planks"))
                .put(TextureKey.ALL,SpudaciousShops.id("block/shelf/shelf_"+woodType));

        Identifier itop = model_top.upload(block, textures, generator.modelCollector);
        Identifier ibottom = model_bottom.upload(block, textures, generator.modelCollector);
        Identifier idouble = model_double.upload(block, textures, generator.modelCollector);

        Identifier fullBlockModel = ModelIds.getBlockModelId(block);

        BlockStateVariantMap.DoubleProperty<SlabType,Direction> map = BlockStateVariantMap.create(ShelfShopBlock.SHELVES_ENABLED, ShelfShopBlock.FACING);
        map = generateForDirection(map,Direction.NORTH, VariantSettings.Rotation.R0, ibottom, itop, idouble);
        map = generateForDirection(map,Direction.EAST, VariantSettings.Rotation.R90, ibottom, itop, idouble);
        map = generateForDirection(map,Direction.SOUTH, VariantSettings.Rotation.R180, ibottom, itop, idouble);
        map = generateForDirection(map,Direction.WEST, VariantSettings.Rotation.R270, ibottom, itop, idouble);

        generator.blockStateCollector.accept(
                        VariantsBlockStateSupplier.create(block)
                .coordinate(map));

    }

    private BlockStateVariantMap.DoubleProperty<SlabType,Direction> generateForDirection(BlockStateVariantMap.DoubleProperty<SlabType,Direction> map, Direction direction, VariantSettings.Rotation rotation, Identifier ibottom, Identifier itop, Identifier idouble){
        return map.register(SlabType.BOTTOM, direction, BlockStateVariant.create().put(VariantSettings.MODEL, ibottom).put(VariantSettings.Y, rotation))
                .register(SlabType.TOP, direction, BlockStateVariant.create().put(VariantSettings.MODEL, itop).put(VariantSettings.Y, rotation))
                .register(SlabType.DOUBLE, direction, BlockStateVariant.create().put(VariantSettings.MODEL, idouble).put(VariantSettings.Y, rotation));

    }


    private static final String header = "block/shelf/shelf_shop";

    //helper method for creating Models with variants
    private static Model makeModel(String parent, String variant) {
        String id = header+parent;
        return new Model(Optional.of(SpudaciousShops.id(id)), Optional.of(parent), TextureKey.PARTICLE, TextureKey.ALL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    }
}
