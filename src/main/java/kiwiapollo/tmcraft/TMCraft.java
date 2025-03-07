package kiwiapollo.tmcraft;

import kiwiapollo.tmcraft.block.ModBlocks;
import kiwiapollo.tmcraft.command.GiveRandomCommand;
import kiwiapollo.tmcraft.item.eggmove.EggMoveItemGroup;
import kiwiapollo.tmcraft.item.eggmove.EggMoveItems;
import kiwiapollo.tmcraft.item.misc.*;
import kiwiapollo.tmcraft.item.moverecorder.MoveRecorderItems;
import kiwiapollo.tmcraft.item.starmove.StarMoveItemGroup;
import kiwiapollo.tmcraft.item.starmove.StarMoveItems;
import kiwiapollo.tmcraft.item.tmmove.TMMoveItemGroup;
import kiwiapollo.tmcraft.item.tmmove.TMMoveItems;
import kiwiapollo.tmcraft.item.tutormove.TutorMoveItemGroup;
import kiwiapollo.tmcraft.item.tutormove.TutorMoveItems;
import kiwiapollo.tmcraft.villager.MoveTutorTradeOffer;
import kiwiapollo.tmcraft.villager.MoveTutorVillager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class TMCraft implements ModInitializer {
	public static final String MOD_ID = "tmcraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> GiveRandomCommand.register(dispatcher));

		Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(TMCraft.MOD_ID, MoveTutorVillager.PROFESSION_ID), MoveTutorVillager.PROFESSION);
		new MoveTutorTradeOffer().register();

		Arrays.stream(ModBlocks.values()).forEach(block -> {
			Registry.register(Registries.BLOCK, block.getIdentifier(), block.getBlock());
			Registry.register(Registries.ITEM, block.getIdentifier(), block.getItem());
		});

		Arrays.stream(SmithingTemplateItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});

		Arrays.stream(MoveRecorderItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});


		Arrays.stream(BlankDiscItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});

		Arrays.stream(TMMoveItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});


		Arrays.stream(BlankEggItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});

		Arrays.stream(EggMoveItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});


		Arrays.stream(BlankBookItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});

		Arrays.stream(TutorMoveItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});


		Arrays.stream(BlankStarItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});

		Arrays.stream(StarMoveItems.values()).forEach(item -> {
			Registry.register(Registries.ITEM, item.getIdentifier(), item.getItem());
		});


		Registry.register(Registries.ITEM_GROUP, MiscItemGroup.ITEM_GROUP_REGISTRY_KEY, MiscItemGroup.ITEM_GROUP);

		ItemGroupEvents.modifyEntriesEvent(MiscItemGroup.ITEM_GROUP_REGISTRY_KEY).register(itemGroup -> {
			Arrays.stream(ModBlocks.values()).forEach(block -> {
				itemGroup.add(block.getItem());
			});

			Arrays.stream(SmithingTemplateItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});

			Arrays.stream(MoveRecorderItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});

			Arrays.stream(BlankDiscItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});

			Arrays.stream(BlankEggItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});

			Arrays.stream(BlankBookItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});

			Arrays.stream(BlankStarItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});
		});


		Registry.register(Registries.ITEM_GROUP, TMMoveItemGroup.ITEM_GROUP_REGISTRY_KEY, TMMoveItemGroup.ITEM_GROUP);

		ItemGroupEvents.modifyEntriesEvent(TMMoveItemGroup.ITEM_GROUP_REGISTRY_KEY).register(itemGroup -> {
			Arrays.stream(TMMoveItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});
		});


		Registry.register(Registries.ITEM_GROUP, EggMoveItemGroup.ITEM_GROUP_REGISTRY_KEY, EggMoveItemGroup.ITEM_GROUP);

		ItemGroupEvents.modifyEntriesEvent(EggMoveItemGroup.ITEM_GROUP_REGISTRY_KEY).register(itemGroup -> {
			Arrays.stream(EggMoveItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});
		});


		Registry.register(Registries.ITEM_GROUP, TutorMoveItemGroup.ITEM_GROUP_REGISTRY_KEY, TutorMoveItemGroup.ITEM_GROUP);

		ItemGroupEvents.modifyEntriesEvent(TutorMoveItemGroup.ITEM_GROUP_REGISTRY_KEY).register(itemGroup -> {
			Arrays.stream(TutorMoveItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});
		});


		Registry.register(Registries.ITEM_GROUP, StarMoveItemGroup.ITEM_GROUP_REGISTRY_KEY, StarMoveItemGroup.ITEM_GROUP);

		ItemGroupEvents.modifyEntriesEvent(StarMoveItemGroup.ITEM_GROUP_REGISTRY_KEY).register(itemGroup -> {
			Arrays.stream(StarMoveItems.values()).forEach(item -> {
				itemGroup.add(item.getItem());
			});
		});
	}
}