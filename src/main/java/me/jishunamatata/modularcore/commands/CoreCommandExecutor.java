package me.jishunamatata.modularcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import me.jishunamatata.modularcore.ModularCore;
import me.jishunamatata.modularcore.utils.ModularPlugin;
import net.md_5.bungee.api.ChatColor;

public class CoreCommandExecutor implements CommandExecutor {

	private ModularCore plugin;

	public CoreCommandExecutor(ModularCore plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		sender.sendMessage(ChatColor.GREEN + "-================= " + ChatColor.GOLD + ChatColor.BOLD + "Modules"
				+ ChatColor.GREEN + " =================-");

		for (ModularPlugin modularPlugin : this.plugin.getModules()) {
			PluginDescriptionFile description = modularPlugin.getDescription();

			boolean invalidCore = modularPlugin.getMinCoreVersion().isNewerThan(ModularCore.getCurrentVersion());

			sender.sendMessage(ChatColor.GOLD + "Module: " + ChatColor.GREEN + description.getName());
			sender.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.GREEN + description.getVersion());
			sender.sendMessage(ChatColor.GOLD + "Minimum Core Version: "
					+ (invalidCore ? ChatColor.RED : ChatColor.GREEN) + modularPlugin.getMinCoreVersion().toString());

			if (invalidCore) {
				sender.sendMessage("");

				sender.sendMessage(String.format("%1$sModule %2$s%3$s %1$sexpects core version",
						ChatColor.RED.toString(), ChatColor.GOLD.toString(), description.getName()));
				sender.sendMessage(String.format("%2$s%3$s %1$s(You have %2$s%4$s%1$s). It may not work correctly.",
						ChatColor.RED.toString(), ChatColor.GOLD.toString(),
						modularPlugin.getMinCoreVersion().toString(), ModularCore.getCurrentVersion().toString()));
			}
			sender.sendMessage(ChatColor.GREEN + "-===========================================-");
		}
		return true;
	}

}
