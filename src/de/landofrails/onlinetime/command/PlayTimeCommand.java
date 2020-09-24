package de.landofrails.onlinetime.command;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.landofrails.onlinetime.OnlineTimePlugin;
import de.landofrails.onlinetime.Updater;

public class PlayTimeCommand implements CommandExecutor {

	private Updater updater = null;

	private static final String PREFIX = OnlineTimePlugin.PREFIX;

	public PlayTimeCommand(Updater updater) {
		this.updater = updater;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("playtime")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					sender.sendMessage(PREFIX + "§3Statistik von §bdir§3:");
					sender.sendMessage(playtime(((Player) sender).getUniqueId()));
				} else {
					sender.sendMessage(PREFIX + "§cDu bist kein Spieler!");
				}
			} else {
				@SuppressWarnings("deprecation")
				OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
				if (off != null && off.hasPlayedBefore()) {

					sender.sendMessage(PREFIX + "§3Statistik von §b" + off.getName() + "§3:");
					sender.sendMessage(playtime(off.getUniqueId()));

				} else {
					sender.sendMessage(PREFIX + "§cSpieler konnte nicht gefunden werden!");
				}
			}
		}
		return false;
	}

	private String playtime(UUID target) {
		Optional<Long> minutes = updater.getPlayTime(target);
		if (!minutes.isPresent()) {
			return PREFIX + "§cKeine Daten verfügbar!";
		} else {
			Long[] time = convertMinutes(minutes.get());
			final String message = PREFIX + "§7Spielzeit: §a{0} {1} {2}";
			String d = time[0] + " " + (time[0] == 1 ? "Tag" : "Tage");
			String h = time[1] + " " + (time[1] == 1 ? "Stunde" : "Stunden");
			String m = time[2] + " " + (time[2] == 1 ? "Minute" : "Minuten");
			return MessageFormat.format(message, d, h, m);
		}
	}

	public Long[] convertMinutes(long minutes) {
		return new Long[] { minutes / 24 / 60, minutes / 60 % 24, minutes % 60 };
	}

}
