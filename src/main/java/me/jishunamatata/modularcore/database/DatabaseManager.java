package me.jishunamatata.modularcore.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import me.jishunamatata.modularcore.ModularCore;
import me.jishunamatata.modularcore.utils.ModularPlugin;

public class DatabaseManager {

	private Map<ModularPlugin, DatabaseConnectionPool> databaseMap = new HashMap<>();

	public static Connection getConnection(ModularPlugin plugin) {
		DatabaseConnectionPool pool = ModularCore.getInstance().getDatabaseManager().databaseMap.computeIfAbsent(plugin,
				k -> new DatabaseConnectionPool());
		return pool.getConnection(plugin);
	}

	public static int executeUpdate(ModularPlugin plugin, String stmt, Object... args) {

		try (Connection connection = getConnection(plugin);
				PreparedStatement statement = connection.prepareStatement(stmt);) {
			return executeUpdate(plugin, statement, args);
		} catch (SQLException e) {
			return -1;
		}
	}

	public static int executeUpdate(ModularPlugin plugin, PreparedStatement statement, Object... args) {
		try {
			for (int i = 0; i < args.length; i++) {
				statement.setObject(i + 1, args[i]);
			}
			return statement.executeUpdate();
		} catch (SQLException e) {
			return -1;
		}
	}

	public static CachedRowSet executeQuery(ModularPlugin plugin, String stmt, Object... args) {
		try (Connection connection = getConnection(plugin);
				PreparedStatement statement = connection.prepareStatement(stmt);) {
			return executeQuery(plugin, statement, args);
		} catch (SQLException e) {
			return null;
		}
	}

	public static CachedRowSet executeQuery(ModularPlugin plugin, PreparedStatement statement, Object... args) {
		try {
			for (int i = 0; i < args.length; i++) {
				statement.setObject(i + 1, args[i]);
			}
			RowSetFactory factory = RowSetProvider.newFactory();
			CachedRowSet rowset = factory.createCachedRowSet();

			rowset.populate(statement.executeQuery());
			return rowset;
		} catch (SQLException e) {
			return null;
		}
	}

}
