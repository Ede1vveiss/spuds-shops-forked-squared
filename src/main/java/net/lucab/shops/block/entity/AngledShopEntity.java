package net.lucab.shops.block.entity;

import net.lucab.shops.block.ModBlockEntities;
import net.lucab.shops.properties.Colour;
import net.lucab.shops.util.CushionTextures;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AngledShopEntity extends AbstractShopEntity {

    /**
     * Do not read from directly in case of null value
     * Use getCushionColour()
     */
    private Colour cushionColour;

    public AngledShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANGLED_SHOP_ENTITY.get(), pos, state, 0.375f);
    }

    private static final String COLOUR_NBT_TAG = "cushion_colour";

    @Override
    public int getTextureId() {
        return 0;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag,
            @NotNull net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(COLOUR_NBT_TAG)) {
            this.cushionColour = Colour.fromId(tag.getInt(COLOUR_NBT_TAG));
        } else {
            this.cushionColour = Colour.RED;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, @NotNull net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(COLOUR_NBT_TAG, this.getCushionColour().getId());
    }

    public Colour getCushionColour() {
        return this.cushionColour == null ? Colour.ORANGE : this.cushionColour;
    }

    public void setCushionColour(@NotNull Colour colour) {
        this.cushionColour = colour;
    }

    public ResourceLocation getCushionTextureID() {
        return CushionTextures.TEXTURE_MAP.get(getCushionColour());
    }

}
