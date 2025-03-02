package kiwiapollo.tmcraft.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import kiwiapollo.tmcraft.item.tmmove.TMMoveItems;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GiveRandomTMCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("giveRandomTM").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> giveRandomTM(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                )
        );
    }

    private static int giveRandomTM(ServerCommandSource source, ServerPlayerEntity player) {
        TMMoveItems[] items = TMMoveItems.values();
        TMMoveItems randomTm = items[(int)Math.round((items.length - 1) * Math.random())];
        ItemStack tmStack = new ItemStack(randomTm.getItem(), 1);
        player.getInventory().offerOrDrop(tmStack);
        source.sendMessage(Text.literal("Gave TM [" + randomTm.getIdentifier().getPath().substring(3) + "] to " + player.getGameProfile().getName()).formatted(Formatting.GREEN));
        return Command.SINGLE_SUCCESS;
    }
}
