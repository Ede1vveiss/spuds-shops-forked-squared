package net.spudacious5705.shops.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ContractScroll extends Item {
    public ContractScroll(Settings settings) {
        super(settings.rarity(Rarity.UNCOMMON).maxCount(1));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (isSigned(stack)) {
            return TypedActionResult.pass(stack);
        }

        NbtCompound nbt = new NbtCompound();

        String name = user.getEntityName();

        nbt.putString(NBTname, name);
        nbt.putUuid(NBTuuid, user.getUuid());

        stack.setNbt(nbt);

        stack.setCustomName(Text.of("Contract - "+name));

        BlockPos pos = user.getBlockPos();
        world.playSound(pos.getX(),pos.getY(),pos.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1f,1f,true);

        return TypedActionResult.success(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return isSigned(stack);
    }

    @MagicConstant
    public static final String NBTuuid = "player_uuid";
    @MagicConstant
    public static final String NBTname = "player_name";

    public static boolean isSigned(ItemStack stack){
        if (stack.hasNbt()) {
            NbtCompound originalNBT = stack.getNbt();
            if(originalNBT != null) {
                boolean v = originalNBT.contains(NBTuuid);
                return v;
            }
        }
        return false;
    }

    @Nullable
    public static UUID getUUID(ItemStack stack){
        if(isSigned(stack)) {
            assert stack.getNbt() != null;
            return stack.getNbt().getUuid(NBTuuid);
        }
        return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(isSigned(stack)){
                tooltip.add(Text.of("Signed by - " + stack.getNbt().getString(NBTname)));//ignore warning
        }
    }
}
