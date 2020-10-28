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

public class ModularCore extends JavaPlugin {

	private static ModularCore plugin;

	private static final SimpleSemVersion CURRENT_VERSION = SimpleSemVersion.fromString("1.0.0");
	private final List<IModularPlugin> MODULES = new ArrayList<>();

	public void onEnable() {
		plugin = this;

		loadModules();
		getCommand("modularcore").setExecutor(new CoreCommandExecutor(this));
	}

	private void loadModules() {
		File moduleFolder = new File(this.getDataFolder(), "modules");
		if (!moduleFolder.exists())
			moduleFolder.mkdirs();

		PluginManager pluginManager = Bukkit.getPluginManager();

		for (File file : moduleFolder.listFiles((f) -> f.getName().endsWith(".jar"))) {
			try {
				Plugin plugin = pluginManager.loadPlugin(file);

				if (plugin instanceof IModularPlugin) {
					PluginDescriptionFile description = plugin.getDescription();
					getLogger().info(String.format("Loading ModularPlugin \"%s\". Version: %s", description.getName(),
							description.getVersion()));

					pluginManager.enablePlugin(plugin);
					this.MODULES.add((IModularPlugin) plugin);
				} else {
					getLogger().warning("Found a valid plugin: " + plugin.getName()
							+ " but it is not a valid module, it should be placed in your regular plugin folder.");
				}

			} catch (InvalidPluginException | UnknownDependencyException | InvalidDescriptionException ex) {
				getLogger().severe("Tried to load an invalid jar file: " + file.getName());
			}

		}
	}

	public static ModularCore getInstance() {
		return plugin;
	}

	public static SimpleSemVersion getCurrentVersion() {
		return CURRENT_VERSION;
	}

	public List<IModularPlugin> getModules() {
		return this.MODULES;
	}

}
