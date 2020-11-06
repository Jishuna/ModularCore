package me.jishunamatata.modularcore.database;

import me.jishunamatata.modularcore.utils.ModularPlugin;

public abstract class AbstractPendingUpdate {
	
	protected ModularPlugin plugin;
	protected String statement;
	
	public abstract void execute();

}
