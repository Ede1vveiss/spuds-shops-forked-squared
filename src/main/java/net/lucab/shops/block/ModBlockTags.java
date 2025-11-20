package net.lucab.shops.block;


import net.lucab.shops.SpudaciousShops;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> SPUDS_SHOPS = TagKey.create(Registries.BLOCK, SpudaciousShops.getResource("spuds_shops"));

    public static void initialise() {}
}
