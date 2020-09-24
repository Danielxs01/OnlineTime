package de.landofrails.onlinetime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import de.landofrails.onlinetime.database.PlayTimeFacade;

public class Updater implements Listener {

	private Map<UUID, Long> onlineTime = new HashMap<>();
	private BukkitTask task = null;
	private PlayTimeFacade facade;

	// Setzen der Facade
	public Updater(PlayTimeFacade facade) {
		this.facade = facade;
	}

	// Starten des Updaters
	public void init(JavaPlugin plugin) {
		task = Bukkit.getScheduler().runTaskTimer(plugin, this::updatePlayTime, 1, 20 * 60L);
	}

	// Beenden des Updaters
	public void destroy() {
		task.cancel();
	}

	// Rückgabe der Onlinezeit
	public Optional<Long> getOnlineTime(UUID uuid) {
		return Optional.ofNullable(onlineTime.get(uuid));
	}

	// Rückgabe der Spielzeit
	public Optional<Long> getPlayTime(UUID uuid) {
		return facade.getPlayTime(uuid);
	}

	// Aktualisiert für jeden Spieler die Spiel- & Onlinezeit
	private void updatePlayTime() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			UUID uuid = p.getUniqueId();
			increaseOnlineTime(uuid);
			increasePlayTime(uuid);
		}
	}

	// Erhöht die Onlinezeit für den Spieler mit der angegebenen UUID
	private void increaseOnlineTime(UUID uuid) {
		Long time = 1l;
		if (onlineTime.containsKey(uuid))
			time += onlineTime.get(uuid);
		onlineTime.put(uuid, time);
	}

	// Erhöht die Spielzeit für den Spieler mit der angegebenen UUID
	private void increasePlayTime(UUID uuid) {
		facade.increaseEntity(uuid);
	}

	// Listener

	// Einträge erstellen falls nicht vorhanden
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		onlineTime.putIfAbsent(event.getPlayer().getUniqueId(), 0l);
		facade.createIfAbsent(event.getPlayer().getUniqueId());
	}

	// Entfernen der Einträe in der Onlinezeit
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		onlineTime.remove(event.getPlayer().getUniqueId());
	}

}
