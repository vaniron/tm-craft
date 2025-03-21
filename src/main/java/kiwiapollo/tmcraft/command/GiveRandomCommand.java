package kiwiapollo.tmcraft.command;

import com.cobblemon.mod.common.Cobblemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kiwiapollo.tmcraft.TMCraft;
import kiwiapollo.tmcraft.item.tmmove.TMMoveItems;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class GiveRandomCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("giveRandom").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("type", StringArgumentType.string()).suggests(GiveRandomCommand::suggestTypes)
                            .executes(context -> giveRandom(context.getSource(), EntityArgumentType.getPlayer(context, "player"), StringArgumentType.getString(context, "type")))
                        )
                )
        );
    }

    private static final List<String> options = Arrays.stream(Type.values()).map(type -> type.name().toLowerCase()).toList();
    private static CompletableFuture<Suggestions> suggestTypes(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        String input = builder.getInput();
        if(input.endsWith(" ")) {
            options.forEach(builder::suggest);
            return builder.buildFuture();
        }

        String[] splitInput = input.split(" ");
        String typing = splitInput[splitInput.length - 1];
        options.stream().filter(key -> key.startsWith(typing)).forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static int giveRandom(ServerCommandSource source, ServerPlayerEntity player, String typeName) {
        Type type = Type.get(typeName);
        if (type == null) {
            source.sendError(Text.literal("Unknown type: " + typeName));
            return 0;
        }

        Item randomItem = type.getRandom();
        player.getInventory().offerOrDrop(new ItemStack(randomItem));
        source.sendMessage(Text.literal("Gave [" + Registries.ITEM.getId(randomItem) + "] to " + player.getGameProfile().getName()).formatted(Formatting.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    public enum Type {
        TM(() -> Arrays.stream(TMMoveItems.values()).map(TMMoveItems::getItem).toList()),
        TYPE_GEM(() -> getTag(Identifier.of(Cobblemon.MODID, "type_gems"))),
        MINT(() -> getTag(Identifier.of(Cobblemon.MODID, "mints"))),
        BOTTLE_CAP(() -> getTag(Identifier.of(TMCraft.MOD_ID, "stat_bottle_caps")));
        private final Supplier<List<Item>> listSupplier;
        Type(Supplier<List<Item>> listSupplier) {
            this.listSupplier = listSupplier;
        }

        private static List<Item> getTag(Identifier tagId) {
            TagKey<Item> tag = TagKey.of(Registries.ITEM.getKey(), tagId);
            Optional<RegistryEntryList.Named<Item>> matches = Registries.ITEM.getEntryList(tag);
            if (matches.isEmpty()) {
                TMCraft.LOGGER.error("Error while getting random items from tag " + tag + " - tag could not be found");
                return List.of(Items.STONE);
            }

            List<Item> items = matches.get().stream().map(RegistryEntry::value).toList();
            if(items.isEmpty()) {
                TMCraft.LOGGER.error("Error while getting random items from tag " + tag + " - tag is empty");
                return List.of(Items.STONE);
            }

            return items;
        }

        public Item getRandom() {
            List<Item> items = this.listSupplier.get();
            return items.get((int)Math.round((items.size() - 1) * Math.random()));
        }

        public static Type get(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }
    }
}