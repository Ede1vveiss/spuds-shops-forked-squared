package net.lucab.shops.block.entity;

import net.lucab.shops.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

import static net.lucab.shops.block.entity.ShopInventory.ItemScatterer;

import org.jetbrains.annotations.NotNull;

public class ShelfShopEntity extends AbstractShopEntity {

    private final ShopInventory shopInventoryTop = ShopInventory.create();

    @OnlyIn(Dist.CLIENT)
    protected ShelfRenderData furtherDataTop;
    @OnlyIn(Dist.CLIENT)
    protected ShelfRenderData furtherDataBottom;

    @OnlyIn(Dist.CLIENT)
    public ShelfRenderData furtherDataBottom() {
        return furtherDataBottom;
    }

    @OnlyIn(Dist.CLIENT)
    public ShelfRenderData furtherDataTop() {
        return furtherDataTop;
    }

    @OnlyIn(Dist.CLIENT)
    public static class ShelfRenderData {
        public final float itemLrotation = (float) ((Math.random() * 90) + 80f);
        public final float itemRrotation = (float) ((Math.random() * 90) + 80f);
    }

    @Override
    protected @NotNull ShopInventory otherInventory() {
        return shopInventoryTop;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void forceUpdateRenderData() {
        super.forceUpdateRenderData();
        rendererDataTop.update();
    }

    @Override
    public void itemScatter(Level world, BlockPos pos) {
        super.itemScatter(world, pos);
        ItemScatterer(world, pos, shopInventoryTop.prepForItemScatterer());

    }

    @Override
    public void renderTick() {
        this.rendererData.onTick();
        this.rendererDataTop.onTick();
    }

    public ShelfShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHELF_SHOP_ENTITY.get(), pos, state, -0.3f);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            createRendererDataForShelf();
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void createRendererDataForShelf() {
        this.furtherDataTop = new ShelfRenderData();
        this.furtherDataBottom = new ShelfRenderData();
        this.rendererDataTop = new RendererData(shopInventoryTop);
    }

    @OnlyIn(Dist.CLIENT)
    protected RendererData rendererDataTop;

    @OnlyIn(Dist.CLIENT)
    public RendererData rendererDataTop() {
        return rendererDataTop;
    }

    @Override
    protected boolean hasTrade() {
        return shopInventory.tradeFunctional() || shopInventoryTop.tradeFunctional();
    }

    @Override
    public int getTextureId() {
        return 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag nbtList = new ListTag();

        for (int i = 0; i < shopInventoryTop.size(); i++) {
            ItemStack itemStack = shopInventoryTop.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("SlotTwo", (byte) i);
                itemStack.save(registries, compoundtag);
                nbtList.add(compoundtag);
            }
        }

        if (!nbtList.isEmpty()) {
            tag.put("ItemsTwo", nbtList);
        }
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        ListTag nbtList = nbt.getList("ItemsTwo", 10);

        for (int i = 0; i < nbtList.size(); i++) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("SlotTwo") & 255;
            if (j < shopInventoryTop.size()) {
                shopInventoryTop.set(j, ItemStack.parseOptional(registries, nbtCompound));
            }
        }
    }

}
