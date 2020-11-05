package me.jishunamatata.modularcore.utils;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class ModularPlugin extends JavaPlugin {

	public abstract SimpleSemVersion getMinCoreVersion();

}
