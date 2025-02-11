package me.lokka30.phantomworlds;

import me.lokka30.microlib.QuickTimer;
import me.lokka30.microlib.UpdateChecker;
import me.lokka30.microlib.YamlConfigFile;
import me.lokka30.phantomworlds.commands.phantomworlds.PhantomWorldsCommand;
import me.lokka30.phantomworlds.managers.FileManager;
import me.lokka30.phantomworlds.managers.WorldManager;
import me.lokka30.phantomworlds.misc.CompatibilityChecker;
import me.lokka30.phantomworlds.misc.UpdateCheckerResult;
import me.lokka30.phantomworlds.misc.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This is the main class of the PhantomWorlds plugin.
 *
 * @author lokka30
 * @since v2.0.0
 */
public class PhantomWorlds extends JavaPlugin {

    /*
    TODO:
     * 1. Complete each subcommand's own to-do list.
     * 2. Translate backslash character in world names as a space so world names with a space can be used in the plugin
     * 3. Vanish compatibility
     *  a. don't send 'by' messages unless the sender is not a player / target can see the (player) sender
     *  b. add vanish compatibility to 'teleport' subcommand
     *  c. add ability to toggle vanish compatibility
     * d. log in console (LogLevel:INFO) when a command is prevented due to a target player seemingly being vanished to the command sender.
     */

    /**
     * If you have contributed code to the plugin,
     * add your name to the end of this list! :)
     */
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public final HashSet<String> contributors = new HashSet<>(Arrays.asList("None"));

    /**
     * This is reported in the 'pw info' command to
     * inform the command sender of what MC versions that
     * this version of PW is designed to run on, and is
     * therefore supported.
     */
    public final String supportedServerVersions = "1.7.x to 1.17.x";

    /**
     * Frequently used vars.
     */
    public final FileManager fileManager = new FileManager(this);
    public final WorldManager worldManager = new WorldManager(this);

    /**
     * Miscellaneous vars.
     */
    public final CompatibilityChecker compatibilityChecker = new CompatibilityChecker();
    public UpdateCheckerResult updateCheckerResult = null;

    /**
     * Data/configuration files.
     */
    public YamlConfigFile settings = new YamlConfigFile(this, new File(getDataFolder(), "settings.yml"));
    public YamlConfigFile advancedSettings = new YamlConfigFile(this, new File(getDataFolder(), "advancedSettings.yml"));
    public YamlConfigFile messages = new YamlConfigFile(this, new File(getDataFolder(), "messages.yml"));
    public YamlConfigFile data = new YamlConfigFile(this, new File(getDataFolder(), "data.yml"));

    /**
     * This method is called by Bukkit when it loads PhantomWorlds.
     *
     * @author lokka30
     * @since v2.0.0
     */
    @Override
    public void onEnable() {
        final QuickTimer timer = new QuickTimer();

        checkCompatibility();
        loadFiles();
        loadWorlds();
        registerCommands();
        registerListeners();
        miscStartupProcedures();

        Utils.LOGGER.info("&f~ Start-up complete. &7Took &b" + timer.getTimer() + "ms");
    }

    /**
     * This method is called by Bukkit when it disables PhantomWorlds.
     *
     * @author lokka30
     * @since v2.0.0
     */
    @Override
    public void onDisable() {
        final QuickTimer timer = new QuickTimer();

        /* ... any on-disable content should be put here. nothing for now */

        Utils.LOGGER.info("&f~ Shut-down complete. &7Took &b" + timer.getTimer() + "ms");
    }

    /**
     * Run the compatibility checkker.
     * Reports in the console if it finds any possible issues.
     *
     * @author lokka30
     * @since v2.0.0
     */
    void checkCompatibility() {
        Utils.LOGGER.info("&3Compatibility: &7Checking compatibility with server...");

        compatibilityChecker.checkAll();

        if (compatibilityChecker.incompatibilities.isEmpty()) return;

        for (int i = 0; i < compatibilityChecker.incompatibilities.size(); i++) {
            CompatibilityChecker.Incompatibility incompatibility = compatibilityChecker.incompatibilities.get(i);
            Utils.LOGGER.warning("Incompatibility #" + (i + 1) + " &8(&7Type: " + incompatibility.type + "&8)&7:");
            Utils.LOGGER.info("&8 &m->&f Reason: &7" + incompatibility.reason);
            Utils.LOGGER.info("&8 &m->&f Recommendation: &7" + incompatibility.recommendation);
        }
    }

    /**
     * (Re)load all data/configuration files.
     * Creates them if they don't exist.
     * Applies version checking where suitable.
     *
     * @author lokka30
     * @since v2.0.0
     */
    public void loadFiles() {
        Utils.LOGGER.info("&3Files: &7Loading files...");
        for (FileManager.PWFile pwFile : FileManager.PWFile.values()) {
            fileManager.init(pwFile);
        }
    }

    /**
     * Checks on the worlds that are created through PhantomWorlds.
     * If they aren't already loaded, PW loads them.
     *
     * @author lokka30
     * @since v2.0.0
     */
    public void loadWorlds() {
        Utils.LOGGER.info("&3Worlds: &7Loading worlds...");
        worldManager.loadManagedWorlds();
    }

    /**
     * Registers the commands for the plugin. In this case, only one
     * command is registered (with an array of sub-commands of course).
     *
     * @author lokka30
     * @since v2.0.0
     */
    void registerCommands() {
        Utils.LOGGER.info("&3Commands: &7Registering commands...");
        Utils.registerCommand(this, new PhantomWorldsCommand(this), "phantomworlds");
    }

    /**
     * Registers the listeners for the plugin. These classes run code
     * when Events happen on the server, e.g. 'player joins server' or 'player changes world'.
     *
     * @author lokka30
     * @since v2.0.0
     */
    void registerListeners() {
        Utils.LOGGER.info("&3Listeners: &7Registering listeners...");
        /* Register any listeners here. */
    }

    /**
     * Miscellaneous startup procedures.
     *
     * @author lokka30
     * @since v2.0.0
     */
    void miscStartupProcedures() {
        Utils.LOGGER.info("&3Startup: &7Running misc startup procedures...");

        /* bStats Metrics */
        new Metrics(this, 8916);

        /* Update Checker */
        if (settings.getConfig().getBoolean("run-update-checker", true)) {
            final UpdateChecker updateChecker = new UpdateChecker(this, 84017);
            updateChecker.getLatestVersion(latestVersion -> {
                updateCheckerResult = new UpdateCheckerResult(
                        !latestVersion.equals(updateChecker.getCurrentVersion()),
                        updateChecker.getCurrentVersion(),
                        latestVersion
                );

                if (updateCheckerResult.isOutdated()) {
                    if (!messages.getConfig().getBoolean("update-checker.console.enabled", true)) return;

                    messages.getConfig().getStringList("update-checker.console.text").forEach(message -> Utils.LOGGER.info(message
                            .replace("%currentVersion%", updateCheckerResult.getCurrentVersion())
                            .replace("%latestVersion%", updateCheckerResult.getLatestVersion())
                    ));
                }
            });
        }
    }
}
