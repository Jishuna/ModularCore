package me.jishunamatata.modularcore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishunamatata.modularcore.commands.CoreCommandExecutor;
import me.jishunamatata.modularcore.database.DatabaseManager;
import me.jishunamatata.modularcore.utils.ModularPlugin;
import me.jishunamatata.modularcore.utils.SimpleSemVersion;

public class ModularCore extends JavaPlugin {

	private static ModularCore plugin;

	private final DatabaseManager databaseManager = new DatabaseManager();

	private static final SimpleSemVersion CURRENT_VERSION = SimpleSemVersion.fromString("1.0.0");
	private final List<ModularPlugin> MODULES = new ArrayList<>();

	public void onLoad() {
		loadModules();
	}

	public void onEnable() {
		plugin = this;

		enableModules();
		getCommand("modularcore").setExecutor(new CoreCommandExecutor(this));
		
		String stmt = "CREATE TABLE IF NOT EXISTS `players` ("
				+ "  `player_id` int PRIMARY KEY NOT NULL,"
				+ "  `uuid` char(36) UNIQUE NOT NULL,"
				+ "  `last_username` varchar(16) DEFAULT NULL"
				+ ")";
		
		DatabaseManager.executeUpdate(this, stmt, DatabaseManager.getCoreConnection(), true);
	}

	private void loadModules() {
		File moduleFolder = new File(this.getDataFolder(), "modules");
		if (!moduleFolder.exists())
			moduleFolder.mkdirs();

		PluginManager pluginManager = Bukkit.getPluginManager();

		for (File file : moduleFolder.listFiles((f) -> f.getName().endsWith(".jar"))) {
			try {
				Plugin plugin = pluginManager.loadPlugin(file);

				if (plugin instanceof ModularPlugin) {
					PluginDescriptionFile description = plugin.getDescription();
					getLogger().info(String.format("Loading ModularPlugin \"%s\". Version: %s", description.getName(),
							description.getVersion()));

					this.MODULES.add((ModularPlugin) plugin);
				} else {
					getLogger().warning("Found a valid plugin: " + plugin.getName()
							+ " but it is not a valid module, it should be placed in your regular plugin folder.");
				}

			} catch (InvalidPluginException | UnknownDependencyException | InvalidDescriptionException ex) {
				getLogger().severe("Tried to load an invalid jar file: " + file.getName());
			}

		}
	}

	private void enableModules() {
		this.MODULES.forEach((module) -> Bukkit.getPluginManager().enablePlugin(module));

		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (plugin instanceof ModularPlugin) {
				this.MODULES.add((ModularPlugin) plugin);
			}
		}
	}

	public static ModularCore getInstance() {
		return plugin;
	}

	public static SimpleSemVersion getCurrentVersion() {
		return CURRENT_VERSION;
	}

	public List<ModularPlugin> getModules() {
		return this.MODULES;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

}
