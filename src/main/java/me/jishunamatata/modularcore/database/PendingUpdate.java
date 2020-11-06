package me.jishunamatata.modularcore.database;

import me.jishunamatata.modularcore.utils.ModularPlugin;

public class PendingUpdate extends AbstractPendingUpdate {

	private Object[] args;

	public PendingUpdate(ModularPlugin plugin, String statement, Object[] args) {
		this.plugin = plugin;
		this.statement = statement;
		this.args = args;
	}

	public void execute() {
		DatabaseManager.executeUpdate(this.plugin, this.statement, this.args);
	}

}
