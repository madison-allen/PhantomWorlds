package me.lokka30.phantomworlds.commands.phantomworlds;

import me.lokka30.phantomworlds.PhantomWorlds;
import me.lokka30.phantomworlds.commands.phantomworlds.subcommands.*;
import me.lokka30.phantomworlds.misc.MultiMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Command: /phantomworlds
 *
 * @author lokka30
 * @since v2.0.0
 */
public class PhantomWorldsCommand implements TabExecutor {

    private final PhantomWorlds main;

    public PhantomWorldsCommand(PhantomWorlds main) {
        this.main = main;
    }

    final CreateSubcommand createSubcommand = new CreateSubcommand();
    final UnloadSubcommand unloadSubcommand = new UnloadSubcommand();
    final TeleportSubcommand teleportSubcommand = new TeleportSubcommand();
    final ListSubcommand listSubcommand = new ListSubcommand();
    final SetSpawnSubcommand setSpawnSubcommand = new SetSpawnSubcommand();
    final ReloadSubcommand reloadSubcommand = new ReloadSubcommand();
    final InfoSubcommand infoSubcommand = new InfoSubcommand();
    final DebugSubcommand debugSubcommand = new DebugSubcommand();
    final CompatibilitySubcommand compatibilitySubcommand = new CompatibilitySubcommand();

    /**
     * @author lokka30
     * @since v2.0.0
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!sender.hasPermission("phantomworlds.command.phantomworlds")) {
            (new MultiMessage(
                    main.messages.getConfig().getStringList("common.no-permission"), Arrays.asList(
                    new MultiMessage.Placeholder("prefix", main.messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                    new MultiMessage.Placeholder("permission", "phantomworlds.command.phantomworlds", false)
            ))).send(sender);
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "create":
                    createSubcommand.parseCommand(main, sender, cmd, label, args);
                    break;
                case "unload":
                    unloadSubcommand.parseCommand(main, sender, cmd, label, args);
                    break;
                case "teleport":
                case "tp":
                    teleportSubcommand.parseCommand(main, sender, cmd, label, args);
                    break;
                case "list":
                    listSubcommand.parseCommand(main, sender, cmd, label, args);
                    break;
                case "setspawn":
                    setSpawnSubcommand.parseCommand(main, sender, cmd, label, args);
                    break;
                case "reload":
                    reloadSubcommand.parseCommand(main, sender, cmd, label, args);
                    break;
                case "info":
                    infoSubcommand.parseCommand(main, sender, cmd, label, args);
                    break;
                case "debug":
                    debugSubcommand.parseCommand(main, sender, cmd, label, args);
                    break;
                case "compatibility":
                    compatibilitySubcommand.parseCommand(main, sender, cmd, label, args);
                    break;
                default:
                    (new MultiMessage(
                            main.messages.getConfig().getStringList("command.phantomworlds.invalid-subcommand"), Arrays.asList(
                            new MultiMessage.Placeholder("prefix", main.messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                            new MultiMessage.Placeholder("arg", args[0], false)
                    ))).send(sender);

                    sendAvailableSubcommands(sender, label);
                    break;
            }
        } else {
            sendAvailableSubcommands(sender, label);
        }
        return true;
    }

    /**
     * Displays messages that list available subcommands for /phantomworlds
     *
     * @param label  label of the command (alias used).
     * @param sender commandsender of the command
     * @author lokka30
     * @since v2.0.0
     */
    void sendAvailableSubcommands(CommandSender sender, String label) {
        (new MultiMessage(
                main.messages.getConfig().getStringList("command.phantomworlds.usage"), Arrays.asList(
                new MultiMessage.Placeholder("prefix", main.messages.getConfig().getString("common.prefix", "&b&lPhantomWorlds: &7"), true),
                new MultiMessage.Placeholder("label", label, false)
        ))).send(sender);
    }

    /**
     * @author lokka30
     * @since v2.0.0
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "info", "list", "setspawn", "reload", "teleport", "unload", "debug", "compatibility");
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "create":
                return createSubcommand.parseTabCompletion(main, sender, cmd, label, args);
            case "info":
                return infoSubcommand.parseTabCompletion(main, sender, cmd, label, args);
            case "list":
                return listSubcommand.parseTabCompletion(main, sender, cmd, label, args);
            case "setspawn":
                return setSpawnSubcommand.parseTabCompletion(main, sender, cmd, label, args);
            case "reload":
                return reloadSubcommand.parseTabCompletion(main, sender, cmd, label, args);
            case "teleport":
                return teleportSubcommand.parseTabCompletion(main, sender, cmd, label, args);
            case "unload":
                return unloadSubcommand.parseTabCompletion(main, sender, cmd, label, args);
            case "debug":
                return debugSubcommand.parseTabCompletion(main, sender, cmd, label, args);
            case "compatibility":
                return compatibilitySubcommand.parseTabCompletion(main, sender, cmd, label, args);
            default:
                return new ArrayList<>();
        }
    }
}
