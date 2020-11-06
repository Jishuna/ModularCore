package me.jishunamatata.modularcore;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishunamatata.modularcore.commands.CoreCommandExecutor;
import me.jishunamatata.modularcore.database.DatabaseConnectionPool;
import me.jishunamatata.modularcore.database.DatabaseManager;
import me.jishunamatata.modularcore.listeners.LoginListener;
import me.jishunamatata.modularcore.utils.ModularPlugin;
import me.jishunamatata.modularcore.utils.SimpleSemVersion;

public class ModularCore extends JavaPlugin {

	private static ModularCore plugin;

	private final DatabaseManager databaseManager = new DatabaseManager();

	private static final SimpleSemVersion CURRENT_VERSION = SimpleSemVersion.fromString("1.0.0");
	private final List<ModularPlugin> modules = new ArrayList<>();

	private final DatabaseConnectionPool corePool = new DatabaseConnectionPool("CoreData");

	@Override
	public void onLoad() {
		loadModules();
	}

	@Override
	public void onEnable() {
		plugin = this;

		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(new LoginListener(corePool), this);

		getCommand("modularcore").setExecutor(new CoreCommandExecutor(this));

		enableModules();

		String stmt = "CREATE TABLE IF NOT EXISTS `players` (" + "  `player_id` INTEGER PRIMARY KEY,"
				+ "  `uuid` char(36) UNIQUE NOT NULL," + "  `last_username` varchar(16) DEFAULT NULL" + ")";

		try (Connection connection = corePool.getConnection()) {
			PreparedStatement statement = connection.prepareStatement(stmt);
			DatabaseManager.executeUpdate(statement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadModules() {
		File moduleFolder = new File(this.getDataFolder(), "modules");
		if (!moduleFolder.exists())
			moduleFolder.mkdirs();

		PluginManager pluginManager = Bukkit.getPluginManager();

		for (File file : moduleFolder.listFiles(f -> f.getName().endsWith(".jar"))) {
			try {
				Plugin plugin = pluginManager.loadPlugin(file);

				if (plugin instanceof ModularPlugin) {
					PluginDescriptionFile description = plugin.getDescription();
					if (getLogger().isLoggable(Level.INFO)) {
						getLogger().info(String.format("Loading ModularPlugin \"%s\". Version: %s",
								description.getName(), description.getVersion()));
					}

					this.modules.add((ModularPlugin) plugin);
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
		this.modules.forEach(module -> Bukkit.getPluginManager().enablePlugin(module));

		for (Plugin loadedPlugin : Bukkit.getPluginManager().getPlugins()) {
			if (loadedPlugin instanceof ModularPlugin) {
				this.modules.add((ModularPlugin) loadedPlugin);
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
		return this.modules;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

}
