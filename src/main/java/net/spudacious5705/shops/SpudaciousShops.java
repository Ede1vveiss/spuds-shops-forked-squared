package net.spudacious5705.shops;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.entity.ModBlockEntities;
import net.spudacious5705.shops.command.DebugShopsStatesCommand;
import net.spudacious5705.shops.item.ModItemGroups;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpudaciousShops implements ModInitializer {
	public static final String MOD_ID = "spudaciousshops";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {


        ModProperties.registerModProperties();

		ModBlocks.registerModBlocks();
		ModItems.registerModItems();

		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();

		ModItemGroups.initialise();

		//DebugShopsStatesCommand.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

}