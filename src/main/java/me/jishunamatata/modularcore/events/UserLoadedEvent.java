package me.jishunamatata.modularcore.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.jishunamatata.modularcore.users.User;

public class UserLoadedEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	private User user;

	public UserLoadedEvent(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
