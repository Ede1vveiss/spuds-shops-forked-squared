package net.spudacious5705.shops.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.util.CushionTextures;
import net.spudacious5705.shops.properties.Colour;
import net.spudacious5705.shops.properties.ModProperties;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class AngledShopEntity extends AbstractShopEntity{

    public Colour cushionColour;

    public AngledShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANGLED_SHOP_ENTITY, pos, state);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        Logger LOGGER = SpudaciousShops.LOGGER;
        if (!world.isClient) {
            BlockState state;
            try {
                state = world.getBlockState(pos);
                LOGGER.debug("Retrieved BlockState: {}", state);
            } catch (Exception e){
                LOGGER.warn("ERROR retreiving blocksate");
            }

            state = world.getBlockState(this.pos);

            
            if(state.contains(ModProperties.CUSHION_COLOUR)){
                LOGGER.debug("Depreciated blockstate property 'cushion_colour' found");
                if(cushionColour == null){
                    LOGGER.debug("No saved cushion colour NBT found. Using depreciated blockstate property as value");
                    cushionColour = state.get(ModProperties.CUSHION_COLOUR);
                    if(cushionColour == null){
                        LOGGER.warn("FAILED - using default value orange");
                        cushionColour = Colour.ORANGE;
                    }
                } else {
                    LOGGER.debug("Cushion colour NBT found. Ignoring depreciated value");
                }
            }

        }
        SpudaciousShops.LOGGER.debug("Loaded an angled shop");
    }

    private static final String COLOUR_NBT_TAG = "cushion_colour";
    @Override
    public void readNbt(NbtCompound nbt) {
        if(nbt.contains(COLOUR_NBT_TAG)) {
            this.cushionColour = Colour.fromId(nbt.getInt(COLOUR_NBT_TAG));
        }
        super.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt(COLOUR_NBT_TAG, this.cushionColour.getId());
        super.writeNbt(nbt);
    }

    public Colour getCachedCushionColour(){
        Colour colour = this.getCachedState().get(ModProperties.CUSHION_COLOUR);
        if(colour == null) return Colour.RED;
        return colour;
    }


    public Identifier getCushionTextureID() {
        Colour colour = getCachedCushionColour();
        return CushionTextures.TEXTURE_MAP.get(colour);
    }

}
