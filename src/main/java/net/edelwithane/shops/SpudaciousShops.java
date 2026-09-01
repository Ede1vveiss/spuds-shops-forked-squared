package net.edelwithane.shops;

import com.mojang.logging.LogUtils;

import net.edelwithane.shops.block.*;
import net.edelwithane.shops.block.*;
import net.edelwithane.shops.block.entity.renderer.ShopIconModels;
import net.edelwithane.shops.config.ConfigHandler;
import net.edelwithane.shops.item.ModItemGroups;
import net.edelwithane.shops.item.ModItems;
import net.edelwithane.shops.lootcondition.ModLootConditions;
import net.edelwithane.shops.properties.ModProperties;
import net.edelwithane.shops.screen.ModScreenHandlers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static net.edelwithane.shops.block.ModBlocks.postRegistryTasks;

import org.slf4j.Logger;

//import net.spudacious5705.shops.command.DebugShopsStatesCommand;

@Mod("spudaciousshops")
public class SpudaciousShops {
    public static final String MOD_ID = "spudaciousshops";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpudaciousShops(IEventBus modEventBus) {

        ConfigHandler.initialise();

        ModItems.registerModItems(modEventBus);
        ModBlocks.registerModBlocks(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModBlockTags.initialise();
        ModProperties.registerModProperties();

        ModBlockEntities.registerBlockEntities(modEventBus);
        ModScreenHandlers.registerScreenHandlers(modEventBus);

        ModLootConditions.register(modEventBus);

        ModItemGroups.register(modEventBus);

        // DebugShopsStatesCommand.register(); //for DEBUG purposes only

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("SETTING UP SPUD'S SHOPS...");

        postRegistryTasks.forEach(Runnable::run);
        PostRegAssigner.runAllAssigners();
        ShopIconModels.initialise();
        VariantResources.register();
        // Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static ResourceLocation getResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

}