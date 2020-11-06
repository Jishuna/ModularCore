package me.jishunamatata.modularcore.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.jishunamatata.modularcore.database.DatabaseConnectionPool;
import me.jishunamatata.modularcore.database.DatabaseManager;
import me.jishunamatata.modularcore.events.UserLoadedEvent;
import me.jishunamatata.modularcore.users.User;

public class LoginListener implements Listener {

	private DatabaseConnectionPool pool;

	public LoginListener(DatabaseConnectionPool pool) {
		this.pool = pool;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		CompletableFuture.supplyAsync(() -> loadFromDatabase(event.getPlayer())).thenAccept(user -> {
			if (user != null) {
				Bukkit.getPluginManager().callEvent(new UserLoadedEvent(user));
			}
		});

	}

	private User loadFromDatabase(Player player) {
		UUID id = player.getUniqueId();

		try (Connection connection = pool.getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("INSERT OR IGNORE INTO players(uuid, last_username) VALUES(?,?);");

			DatabaseManager.executeUpdate(statement, id.toString(), player.getName());

			statement = connection.prepareStatement("SELECT * FROM players WHERE uuid=?;");
			ResultSet result = DatabaseManager.executeQuery(statement, id.toString());

			if (result.next()) {
				return new User(result.getInt("player_id"), id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
}
