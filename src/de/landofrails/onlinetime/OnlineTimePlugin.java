package de.landofrails.onlinetime;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.landofrails.onlinetime.command.OnlineTimeCommand;
import de.landofrails.onlinetime.command.PlayTimeCommand;
import de.landofrails.onlinetime.database.PlayTimeEntity;
import de.landofrails.onlinetime.database.PlayTimeFacade;

public class OnlineTimePlugin extends JavaPlugin {

	private static final Logger logger = Logger.getLogger("OnlineTimePlugin");

	private Updater updater = null;

	public static final String PREFIX = "§8[§9Playtime§8] ";
	public static final String PERMISSION = "landofrails.playtime";

	@Override
	public void onEnable() {
		setupDatabase();

		// Interaktion zur Datenbank
		PlayTimeFacade facade = new PlayTimeFacade(this.getDatabase(), this::shutdown);

		// Aktualisiert die Daten über die Facade
		updater = new Updater(facade);
		updater.init(this);
		Bukkit.getPluginManager().registerEvents(updater, this);

		// Initialisieren der beiden Commandklassen
		OnlineTimeCommand onlinetime = new OnlineTimeCommand(updater);
		PlayTimeCommand playtime = new PlayTimeCommand(updater);

		// Registrieren der beiden Befehle
		Bukkit.getPluginCommand("onlinetime").setExecutor(onlinetime);
		Bukkit.getPluginCommand("playtime").setExecutor(playtime);

	}

	@Override
	public void onDisable() {

		// Den Updater stoppen
		if (updater != null) {
			updater.destroy();
			updater = null;
		}

	}

	/**
	 * Setzt die Datenbank initial auf
	 */
	private void setupDatabase() {
		try {
			getDatabase().find(PlayTimeEntity.class).findRowCount();
		} catch (PersistenceException ex) {
			getLogger().info("Installing database for " + getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}

	/**
	 * Gibt die Liste an Entities zurück
	 */
	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<>();
		list.add(PlayTimeEntity.class);
		return list;
	}

	private Void shutdown() {
		logger.log(Level.WARNING, "Forced shutdown!");
		Bukkit.getPluginManager().disablePlugin(this);
		return null;
	}
}
