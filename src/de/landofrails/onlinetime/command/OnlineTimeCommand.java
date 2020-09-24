package de.landofrails.onlinetime.command;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.landofrails.onlinetime.OnlineTimePlugin;
import de.landofrails.onlinetime.Updater;

public class OnlineTimeCommand implements CommandExecutor {

	private Updater updater = null;

	private static final String PREFIX = OnlineTimePlugin.PREFIX;

	public OnlineTimeCommand(Updater updater) {
		this.updater = updater;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("onlinetime")) {
			if (args.length == 0) {
				noArgument(sender);
			} else {
				oneArgument(sender, args);
			}
		}
		return true;
	}

	private void noArgument(CommandSender sender) {
		if (sender instanceof Player) {
			sender.sendMessage(PREFIX + "§3Statistik von §bdir§3:");
			sender.sendMessage(onlinetime((Player) sender));
		} else {
			sender.sendMessage(PREFIX + "§cDu bist kein Spieler!");
		}
	}

	private void oneArgument(CommandSender sender, String[] args) {
		@SuppressWarnings("deprecation")
		OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
		if (off != null && off.hasPlayedBefore()) {

			sender.sendMessage(PREFIX + "§3Statistik von §b" + off.getName() + "§3:");
			if (off.isOnline()) {
				sender.sendMessage(onlinetime(off.getPlayer()));
			} else {
				sender.sendMessage(offlinetime(off));
			}

		} else {
			sender.sendMessage(PREFIX + "§cSpieler konnte nicht gefunden werden!");
		}
	}

	private String onlinetime(Player target) {
		Player p = target;
		Optional<Long> minutes = updater.getOnlineTime(p.getUniqueId());
		if (!minutes.isPresent()) {
			return PREFIX + "§cKeine Daten verfügbar!";
		} else {
			Long[] time = convertMinutes(minutes.get());
			final String message = PREFIX + "§7Onlinezeit: §a{0} {1} {2}";
			String d = time[0] + " " + (time[0] == 1 ? "Tag" : "Tage");
			String h = time[1] + " " + (time[1] == 1 ? "Stunde" : "Stunden");
			String m = time[2] + " " + (time[2] == 1 ? "Minute" : "Minuten");
			return MessageFormat.format(message, d, h, m);
		}
	}

	private String offlinetime(OfflinePlayer target) {
		Optional<Long> milli = Optional.ofNullable(target.getLastPlayed());
		if (!milli.isPresent()) {
			return PREFIX + "§cKeine Daten verfügbar!";
		} else {

			LocalDateTime now = LocalDateTime.now();
			LocalDateTime offlineSince = Instant.ofEpochMilli(milli.get()).atZone(ZoneId.systemDefault())
					.toLocalDateTime();

			long min = ChronoUnit.MINUTES.between(offlineSince, now);

			Long[] time = convertMinutes(min);
			final String message = PREFIX + "§7Offlinezeit: §c{0} {1} {2}";
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
