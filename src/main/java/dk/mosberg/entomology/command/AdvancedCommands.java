package dk.mosberg.entomology.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dk.mosberg.entomology.balance.TelemetrySystem;
import dk.mosberg.entomology.integration.SystemIntegration;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

/**
 * Advanced commands for system management.
 */
public class AdvancedCommands {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(CommandManager.literal("entomology")
        .then(CommandManager.literal("reload")
            .requires(source -> source.hasPermissionLevel(2))
            .executes(AdvancedCommands::reload))
        .then(CommandManager.literal("stats")
            .requires(source -> source.hasPermissionLevel(2))
            .executes(AdvancedCommands::showStats))
        .then(CommandManager.literal("validate")
            .requires(source -> source.hasPermissionLevel(2))
            .executes(AdvancedCommands::validate)));
  }

  private static int reload(CommandContext<ServerCommandSource> context) {
    context.getSource().sendFeedback(
        () -> Text.literal("§eReloading Entomology systems..."),
        true);

    try {
      SystemIntegration.getInstance().reload();

      context.getSource().sendFeedback(
          () -> Text.literal("§aReload complete!"),
          true);
      return 1;
    } catch (Exception e) {
      context.getSource().sendError(
          Text.literal("§cReload failed: " + e.getMessage()));
      return 0;
    }
  }

  private static int showStats(CommandContext<ServerCommandSource> context) {
    TelemetrySystem telemetry = TelemetrySystem.getInstance();

    if (!telemetry.isEnabled()) {
      context.getSource().sendFeedback(
          () -> Text.literal("§eTelemetry is disabled"),
          false);
      return 0;
    }

    context.getSource().sendFeedback(
        () -> Text.literal("§6=== Entomology Statistics ==="),
        false);

    Map<String, Double> metrics = telemetry.getMetrics();
    for (Map.Entry<String, Double> entry : metrics.entrySet()) {
      String key = entry.getKey();
      double value = entry.getValue();

      context.getSource().sendFeedback(
          () -> Text.literal(String.format("§e%s: §f%.2f", key, value)),
          false);
    }

    return 1;
  }

  private static int validate(CommandContext<ServerCommandSource> context) {
    context.getSource().sendFeedback(
        () -> Text.literal("§eValidating configurations..."),
        false);

    try {
      // Validate all system configurations
      boolean valid = SystemIntegration.getInstance().validate();

      if (valid) {
        context.getSource().sendFeedback(
            () -> Text.literal("§aValidation complete! All configurations valid."),
            false);
        return 1;
      } else {
        context.getSource().sendError(
            Text.literal("§cValidation failed: One or more configurations invalid"));
        return 0;
      }
    } catch (Exception e) {
      context.getSource().sendError(
          Text.literal("§cValidation failed: " + e.getMessage()));
      return 0;
    }
  }
}
