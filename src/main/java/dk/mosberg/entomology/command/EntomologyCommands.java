package dk.mosberg.entomology.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dk.mosberg.entomology.EntomologyMod;
import dk.mosberg.entomology.data.DataDrivenRegistry;
import dk.mosberg.entomology.data.SpecimenDefinition;
import dk.mosberg.entomology.item.SpecimenJarItem;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Advanced command system for Entomology mod.
 * Commands: /entomology give <specimen> [count], /entomology info <specimen>,
 * /entomology reload
 */
public class EntomologyCommands {

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
      CommandManager.RegistrationEnvironment environment) {
    dispatcher.register(CommandManager.literal("entomology")
        .requires(source -> source.hasPermissionLevel(2))
        .then(CommandManager.literal("give")
            .then(CommandManager.argument("specimen", StringArgumentType.word())
                .executes(context -> giveSpecimen(context, StringArgumentType.getString(context, "specimen"), 1))
                .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 64))
                    .executes(context -> giveSpecimen(context, StringArgumentType.getString(context, "specimen"),
                        IntegerArgumentType.getInteger(context, "count"))))))
        .then(CommandManager.literal("research")
            .executes(EntomologyCommands::showResearchProgress))
        .then(CommandManager.literal("reload")
            .executes(EntomologyCommands::reloadData))
        .then(CommandManager.literal("info")
            .then(CommandManager.argument("specimen", StringArgumentType.word())
                .executes(context -> showSpecimenInfo(context, StringArgumentType.getString(context, "specimen"))))));
  }

  private static int giveSpecimen(CommandContext<ServerCommandSource> context, String specimenId, int count) {
    ServerCommandSource source = context.getSource();
    try {
      ServerPlayerEntity player = source.getPlayerOrThrow();

      SpecimenDefinition specimen = DataDrivenRegistry.getSpecimen(specimenId);
      if (specimen == null) {
        source.sendError(Text.literal("Unknown specimen: " + specimenId));
        return 0;
      }

      for (int i = 0; i < count; i++) {
        ItemStack jar = new ItemStack(EntomologyMod.specimenJarItem);
        SpecimenJarItem.setSpecimenId(jar, specimenId);
        player.giveItemStack(jar);
      }

      Text feedback = Text.literal("Gave " + count + "x ")
          .append(Text.translatable(specimen.displayNameKey()))
          .append(" specimen jar(s)").formatted(Formatting.GREEN);
      source.sendFeedback(() -> feedback, true);
      return count;
    } catch (Exception e) {
      source.sendError(Text.literal("This command can only be used by players"));
      return 0;
    }
  }

  private static int showResearchProgress(CommandContext<ServerCommandSource> context) {
    ServerCommandSource source = context.getSource();
    var entries = DataDrivenRegistry.getResearchEntries();

    Text header = Text.literal("=== Research Entries ===").formatted(Formatting.AQUA, Formatting.BOLD);
    source.sendFeedback(() -> header, false);

    Text total = Text.literal("Total: " + entries.size()).formatted(Formatting.BLUE);
    source.sendFeedback(() -> total, false);

    entries.forEach(entry -> {
      Text entryText = Text.literal("  • ")
          .formatted(Formatting.GRAY)
          .append(Text.translatable(entry.pageKey()).formatted(Formatting.WHITE))
          .append(Text.literal(" (Specimen: " + entry.specimenId() + ")").formatted(Formatting.DARK_GRAY));
      source.sendFeedback(() -> entryText, false);
    });

    return entries.size();
  }

  private static int reloadData(CommandContext<ServerCommandSource> context) {
    ServerCommandSource source = context.getSource();

    Text reloading = Text.literal("Reloading Entomology data...").formatted(Formatting.YELLOW);
    source.sendFeedback(() -> reloading, true);

    // Trigger reload via resource manager
    source.getServer().reloadResources(source.getServer().getDataPackManager().getIds())
        .thenRun(() -> {
          Text success = Text.literal("✓ Entomology data reloaded successfully!").formatted(Formatting.GREEN);
          source.sendFeedback(() -> success, true);
        })
        .exceptionally(throwable -> {
          source.sendError(Text.literal("✗ Failed to reload data: " + throwable.getMessage()));
          return null;
        });

    return 1;
  }

  private static int showSpecimenInfo(CommandContext<ServerCommandSource> context, String specimenId) {
    ServerCommandSource source = context.getSource();
    SpecimenDefinition specimen = DataDrivenRegistry.getSpecimen(specimenId);

    if (specimen == null) {
      source.sendError(Text.literal("Unknown specimen: " + specimenId));
      return 0;
    }

    Text header = Text.literal("=== Specimen Information ===").formatted(Formatting.GOLD, Formatting.BOLD);
    source.sendFeedback(() -> header, false);

    Text id = Text.literal("ID: ").formatted(Formatting.GRAY)
        .append(Text.literal(specimen.id()).formatted(Formatting.WHITE));
    source.sendFeedback(() -> id, false);

    Text display = Text.literal("Display: ").formatted(Formatting.GRAY)
        .append(Text.translatable(specimen.displayNameKey()).formatted(Formatting.WHITE));
    source.sendFeedback(() -> display, false);

    Text rarity = Text.literal("Rarity: ").formatted(Formatting.GRAY)
        .append(Text.literal(specimen.rarity()).formatted(Formatting.YELLOW));
    source.sendFeedback(() -> rarity, false);

    return 1;
  }
}
