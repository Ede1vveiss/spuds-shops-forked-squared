package net.spudacious5705.shops.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.spudacious5705.shops.block.custom.AbstractShopBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RugShopEntity extends AbstractShopEntity{

    public long lastNanoTime;

    public RugShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUG_SHOP_ENTITY, pos, state, -0.3f);
    }

    public float itemRotationY = (float)(Math.random()*360);
    public float itemRotationX =  (float)(Math.random()*360);
    public float itemRotationZ = (float)(Math.random()*360);
    public float itemRotationSpeedZ = (float)(Math.random()*360);
    public float itemHeight =  (float)(Math.random()*Math.PI*2);
    public final boolean rotateDirectionY = Math.random()>0.5f;
    public final boolean rotateDirectionX = Math.random()>0.5f;

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    @Override
    public Direction getFacingDirection() {
        return Direction.NORTH;
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos pos, AbstractShopBlock.AbstractShopBlockState shopState) {
        if(decayTimer<0){
            if (world.random.nextFloat() < 0.05f) {
                world.spawnParticles(ParticleTypes.ENCHANT, pos.getX() + 0.325f + world.random.nextFloat()*0.35f, pos.getY() + 0.2f + world.random.nextFloat()*0.2f, pos.getZ() + 0.325f + world.random.nextFloat()*0.35f, 1, 0, 0, 0, -0.2);
            }
        }
        super.serverTick(world, pos, shopState);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void createRendererData(){
        this.rendererData = new RugRendererData<>(itemStacks,this);
    }

    public static class RugRendererData<shopType extends RugShopEntity> extends RendererData<shopType> {

        public RugRendererData(@NotNull DefaultedList<ItemStack> inv, shopType shop) {
            super(inv, shop);
        }

        @Override
        protected Direction facingDirection() {
            return Direction.NORTH;
        }
    }

    public int getTextureId() {
        return 0;
    }
}
