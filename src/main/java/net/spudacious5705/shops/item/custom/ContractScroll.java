package net.spudacious5705.shops.item.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

        nbt.putString("player_name", name);
        nbt.putUuid("player_uuid", user.getUuid());

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

    private static boolean isSigned(ItemStack stack){
        if (stack.hasNbt()) {
            NbtCompound originalNBT = stack.getNbt();
            if(originalNBT != null) {
                boolean v = originalNBT.contains("player_uuid");
                return v;
            }
        }
        return false;
    }

    @Override
    public boolean isNbtSynced() {
        return super.isNbtSynced();
    }
}
