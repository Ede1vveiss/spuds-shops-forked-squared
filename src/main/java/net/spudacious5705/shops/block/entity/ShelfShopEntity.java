package net.spudacious5705.shops.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShelfShopEntity extends AbstractShopEntity{

    private final DefaultedList<ItemStack> itemStacksTop = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);

    @Override
    protected @NotNull DefaultedList<ItemStack> otherInventory() {
        return itemStacksTop;
    }

    public ShelfShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHELF_SHOP_ENTITY, pos, state, -0.3f);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.rendererData2 = new RendererData(itemStacks,this);
        }
    }

    @Environment(EnvType.CLIENT)
    protected RendererData rendererData2;

    @Environment(EnvType.CLIENT)
    public RendererData rendererData2(){return  rendererData2;}

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
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
