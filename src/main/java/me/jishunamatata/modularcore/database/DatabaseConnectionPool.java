package me.jishunamatata.modularcore.database;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import me.jishunamatata.modularcore.ModularCore;

public class DatabaseConnectionPool {
	
	private String name;
	
	public DatabaseConnectionPool(String name) {
		this.name = name;
	}

	private HikariDataSource ds = null;

	public void close() {
		if (ds != null && !ds.isClosed()) {
			ds.close();
		}
	}

	public Connection getConnection() {
		if (this.ds == null) {

			File dataDir = new File(ModularCore.getInstance().getDataFolder().getAbsolutePath() + "/data");

			if (!dataDir.exists()) {
				dataDir.mkdirs();
			}

			String url = "jdbc:sqlite:" + dataDir.getAbsolutePath() + "/" + name + ".db";

			this.ds = new HikariDataSource();
			ds.setJdbcUrl(url);

			ds.setMinimumIdle(2);
			ds.setMaximumPoolSize(15);
			ds.setPoolName(name + "-Connection-Pool");

			ds.addDataSourceProperty("useUnicode", "true");
			ds.addDataSourceProperty("characterEncoding", "utf-8");
			ds.addDataSourceProperty("rewriteBatchedStatements", "true");
			ds.addDataSourceProperty("tcpKeepAlive", true);
			ds.setLeakDetectionThreshold(60 * 1000);

			ds.addDataSourceProperty("cachePrepStmts", "true");
			ds.addDataSourceProperty("prepStmtCacheSize", "250");
			ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			ds.addDataSourceProperty("useServerPrepStmts", "true");

			ds.addDataSourceProperty("useSSL", false);
			ds.addDataSourceProperty("verifyServerCertificate", "false");
		}

		Connection connection = null;
		try {
			connection = ds.getConnection();
		} catch (SQLException e) {
			// PANIC
			e.printStackTrace();
		}
		return connection;
	}

}
