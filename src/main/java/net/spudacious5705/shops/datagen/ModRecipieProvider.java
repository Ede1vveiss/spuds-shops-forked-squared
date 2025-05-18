package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.custom.AngledShopBlock;
import net.spudacious5705.shops.model.CushionResources;
import net.spudacious5705.shops.properties.Colour;

import java.util.function.Consumer;

public class ModRecipieProvider extends FabricRecipeProvider {
    public ModRecipieProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        CushionResources.initialise();

        for(Colour colour: Colour.values()) {

            for(Wood wood: Wood.values()){
                makeShopRecipie(consumer, wood.angledShopBlock.getColouredShopItem(colour),wood.block,CushionResources.COLOUR_MAP.get(colour).wool());
            }

        }
    }

    private void makeShopRecipie(Consumer<RecipeJsonProvider> consumer, Item shopItem, Block wood, Item wool){
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, shopItem)
                .pattern(" g ").pattern("pwp").pattern("ccc")
                .input('g', Blocks.GLASS)
                .input('c', Blocks.CHEST)
                .input('p', wood)
                .input('w', wool)
                .criterion(hasItem(Blocks.GLASS), conditionsFromItem(Blocks.GLASS))
                .criterion(hasItem(Blocks.CHEST),
                        conditionsFromItem(Blocks.CHEST))
                .criterion(hasItem(wood), conditionsFromItem(wood))
                .criterion(hasItem(wool),
                        conditionsFromItem(wool))
                .offerTo(consumer);

    }

    public enum Wood {
        ACACIA(ModBlocks.SHOP_BLOCK_ACACIA, Blocks.ACACIA_WOOD),
        BAMBOO(ModBlocks.SHOP_BLOCK_BAMBOO, Blocks.BAMBOO_PLANKS),
        BIRCH(ModBlocks.SHOP_BLOCK_BIRCH, Blocks.BIRCH_PLANKS),
        CHERRY(ModBlocks.SHOP_BLOCK_CHERRY, Blocks.CHERRY_PLANKS),
        CRIMSON(ModBlocks.SHOP_BLOCK_CRIMSON, Blocks.CRIMSON_PLANKS),
        DARK_OAK(ModBlocks.SHOP_BLOCK_DARK_OAK, Blocks.DARK_OAK_PLANKS),
        MANGROVE(ModBlocks.SHOP_BLOCK_MANGROVE, Blocks.MANGROVE_PLANKS),
        OAK(ModBlocks.SHOP_BLOCK_OAK, Blocks.OAK_PLANKS),
        SPRUCE(ModBlocks.SHOP_BLOCK_SPRUCE, Blocks.SPRUCE_PLANKS),
        WARPED(ModBlocks.SHOP_BLOCK_WARPED, Blocks.WARPED_PLANKS),
        JUNGLE(ModBlocks.SHOP_BLOCK_JUNGLE, Blocks.JUNGLE_PLANKS);

        private final AngledShopBlock angledShopBlock;
        private final Block block;

        // Constructor
        Wood(AngledShopBlock angledShopBlock, Block block) {
            this.angledShopBlock = angledShopBlock;
            this.block = block;
        }

        // Getters for the properties
        public AngledShopBlock getShopBlock() {
            return angledShopBlock;
        }

        public Block getBlock() {
            return block;
        }
    }
}
