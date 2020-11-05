package me.jishunamatata.modularcore.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import org.bukkit.plugin.java.JavaPlugin;

import me.jishunamatata.modularcore.ModularCore;
import me.jishunamatata.modularcore.utils.ModularPlugin;

public class DatabaseManager {

	private DatabaseConnectionPool corePool = new DatabaseConnectionPool();
	private Map<ModularPlugin, DatabaseConnectionPool> databasePoolMap = new HashMap<>();

	public static Connection getCoreConnection() {
		return ModularCore.getInstance().getDatabaseManager().corePool.getConnection(ModularCore.getInstance());
	}

	public static Connection getConnection(ModularPlugin plugin) {
		DatabaseConnectionPool pool = ModularCore.getInstance().getDatabaseManager().databasePoolMap
				.computeIfAbsent(plugin, k -> new DatabaseConnectionPool());
		return pool.getConnection(plugin);
	}

	public static int executeUpdate(JavaPlugin plugin, String stmt, Connection connection, boolean closeConnection,
			Object... args) {

		try (connection; PreparedStatement statement = connection.prepareStatement(stmt);) {
			int rows = executeUpdate(plugin, statement, args);
			if (closeConnection) {
				connection.close();
			}
			return rows;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static int executeUpdate(JavaPlugin plugin, PreparedStatement statement, Object... args) {
		try {
			for (int i = 0; i < args.length; i++) {
				statement.setObject(i + 1, args[i]);
			}
			return statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static CachedRowSet executeQuery(JavaPlugin plugin, String stmt, Connection connection,
			boolean closeConnection, Object... args) {
		try (connection; PreparedStatement statement = connection.prepareStatement(stmt);) {
			CachedRowSet result = executeQuery(plugin, statement, args);
			if (closeConnection) {
				connection.close();
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static CachedRowSet executeQuery(JavaPlugin plugin, PreparedStatement statement, Object... args) {
		try {
			for (int i = 0; i < args.length; i++) {
				statement.setObject(i + 1, args[i]);
			}
			RowSetFactory factory = RowSetProvider.newFactory();
			CachedRowSet rowset = factory.createCachedRowSet();

			rowset.populate(statement.executeQuery());
			return rowset;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
