package me.jishunamatata.modularcore.users;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.NamespacedKey;

import me.jishunamatata.modularcore.database.AbstractPendingUpdate;
import me.jishunamatata.modularcore.database.PendingUpdate;
import me.jishunamatata.modularcore.utils.ModularPlugin;

public class User {

	private int id;
	private UUID uuid;

	private Map<String, Object> dataMap;
	private Map<NamespacedKey, AbstractPendingUpdate> pendingUpdates = new HashMap<>();

	public User(int id, UUID uuid) {
		this.id = id;
		this.uuid = uuid;
	}

	public void save() {
		pendingUpdates.values().forEach(AbstractPendingUpdate::execute);
		pendingUpdates.clear();
	}

	public void addPendingUpdate(ModularPlugin plugin, String key, String statement, Object... args) {
		this.pendingUpdates.put(new NamespacedKey(plugin, key), new PendingUpdate(plugin, statement, args));
	}

	public void addPendingUpdate(ModularPlugin plugin, NamespacedKey key, String statement, Object... args) {
		this.pendingUpdates.put(key, new PendingUpdate(plugin, statement, args));
	}

	public int getUserId() {
		return id;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setData(String key, Object value) {
		dataMap.put(key, value);
	}

	public Object getDataObject(String key) {
		return this.dataMap.get(key);
	}

	public String getDataString(String key, String def) {
		Object value = getDataObject(key);
		return value != null ? value.toString() : def;
	}

	public int getDataInt(String key, int def) {
		Object value = getDataObject(key);
		return value instanceof Number ? ((Number) value).intValue() : def;
	}

	public float getDataFloat(String key, Float def) {
		Object value = getDataObject(key);
		return value instanceof Number ? ((Number) value).floatValue() : def;
	}

	public <T> T getDataObject(String key, T def, Class<T> clazz) {
		Object value = getDataObject(key);
		return value != null && clazz.isInstance(value) ? clazz.cast(value) : def;
	}

}
