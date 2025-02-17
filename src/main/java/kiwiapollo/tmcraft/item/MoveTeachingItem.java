package kiwiapollo.tmcraft.item;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;
import com.cobblemon.mod.common.api.moves.categories.DamageCategory;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kiwiapollo.tmcraft.common.DamageCategoryTextColorFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class MoveTeachingItem extends Item implements ElementalTypeItem, PokemonSelectingItem {
    protected final String move;
    private final ElementalType type;

    public MoveTeachingItem(String move, ElementalType type) {
        super(new Item.Settings());

        this.move = move;
        this.type = type;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (world.isClient()) {
            return TypedActionResult.pass(itemStack);
        }

        return use((ServerPlayerEntity) player, itemStack);
    }

    protected abstract boolean canPokemonLearnMove(PlayerEntity player, Pokemon pokemon);

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (getMoveTemplate().getDamageCategory() == DamageCategories.INSTANCE.getSTATUS()) {
            tooltip.add(getMoveTypeTooltipText());
            tooltip.add(getMoveDamageCategoryTooltipText());

        } else {
            tooltip.add(getMoveTypeTooltipText());
            tooltip.add(getMoveDamageCategoryTooltipText());
            tooltip.add(getMovePowerTooltipText());
        }
    }

    private Text getMoveTypeTooltipText() {
        return type.getDisplayName().setStyle(Style.EMPTY.withColor(type.getHue()));
    }

    private Text getMoveDamageCategoryTooltipText() {
        DamageCategory category = getMoveTemplate().getDamageCategory();
        Style style = Style.EMPTY.withColor(new DamageCategoryTextColorFactory().create(category));
        return category.getDisplayName().copy().setStyle(style);
    }

    private Text getMovePowerTooltipText() {
        Style style = Style.EMPTY.withColor(Formatting.YELLOW);
        return Text.literal(String.valueOf(getMoveTemplate().getPower())).setStyle(style);
    }

    @Override
    public ElementalType getMoveType() {
        return type;
    }

    @NotNull
    private MoveTemplate getMoveTemplate() {
        Objects.requireNonNull(move);
        return Objects.requireNonNull(Moves.INSTANCE.getByName(move));
    }

    @Override
    public @Nullable BagItem getBagItem() {
        return null;
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> use(@NotNull ServerPlayerEntity player, @NotNull ItemStack itemStack) {
        return PokemonSelectingItem.DefaultImpls.use(this, player, itemStack);
    }

    @Override
    public @Nullable TypedActionResult<ItemStack> applyToPokemon(@NotNull ServerPlayerEntity player, @NotNull ItemStack itemStack, @NotNull Pokemon pokemon) {
        if (!canPokemonLearnMove(player, pokemon)) {
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return TypedActionResult.pass(itemStack);
        }

        teachPokemonMove(pokemon);

        if (!player.isCreative()) {
            itemStack.decrement(1);
        }

        player.sendMessage(Text.translatable("item.tmcraft.success.pokemon_learned_move", pokemon.getDisplayName(), getMoveTemplate().getDisplayName()));
        player.getWorld().playSound(null, player.getBlockPos(), CobblemonSounds.PC_CLICK, SoundCategory.PLAYERS, 1.0f, 1.0f);

        return TypedActionResult.success(itemStack);
    }

    private void teachPokemonMove(Pokemon pokemon) {
        if (pokemon.getMoveSet().hasSpace()) {
            pokemon.getMoveSet().add(getMoveTemplate().create());

        } else {
            pokemon.getBenchedMoves().add(new BenchedMove(getMoveTemplate(), 0));
        }
    }

    @Override
    public void applyToBattlePokemon(@NotNull ServerPlayerEntity player, @NotNull ItemStack itemStack, @NotNull BattlePokemon battlePokemon) {
        PokemonSelectingItem.DefaultImpls.applyToBattlePokemon(this, player, itemStack, battlePokemon);
    }

    @Override
    public boolean canUseOnPokemon(@NotNull Pokemon pokemon) {
        return true;
    }

    @Override
    public boolean canUseOnBattlePokemon(@NotNull BattlePokemon battlePokemon) {
        return false;
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> interactWithSpecificBattle(@NotNull ServerPlayerEntity player, @NotNull ItemStack itemStack, @NotNull BattlePokemon battlePokemon) {
        return PokemonSelectingItem.DefaultImpls.interactWithSpecificBattle(this, player, itemStack, battlePokemon);
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> interactGeneral(@NotNull ServerPlayerEntity player, @NotNull ItemStack itemStack) {
        return PokemonSelectingItem.DefaultImpls.interactGeneral(this, player, itemStack);
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> interactGeneralBattle(@NotNull ServerPlayerEntity player, @NotNull ItemStack itemStack, @NotNull BattleActor battleActor) {
        return PokemonSelectingItem.DefaultImpls.interactGeneralBattle(this, player, itemStack, battleActor);
    }
}
