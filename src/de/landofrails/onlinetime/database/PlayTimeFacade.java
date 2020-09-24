package de.landofrails.onlinetime.database;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.OptimisticLockException;

import com.avaje.ebean.EbeanServer;

public class PlayTimeFacade {

	private static final Logger logger = Logger.getLogger("PlayTimeFacade");
	private EbeanServer database;
	private Runnable shutdownHook;

	public PlayTimeFacade(EbeanServer database, Runnable shutdownHook) {
		this.database = database;
		this.shutdownHook = shutdownHook;
	}

	public Optional<PlayTimeEntity> getEntity(UUID playerID) {
		Optional<PlayTimeEntity> opt = Optional.empty();
		try {
			opt = Optional
					.ofNullable(database.find(PlayTimeEntity.class).where().eq("playerID", playerID).findUnique());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Beim Abruf eines Entities ist ein Fehler aufgetreten!", e);
		}
		return opt;
	}

	public long increaseEntity(UUID playerID) {
		Optional<PlayTimeEntity> result = getEntity(playerID);
		PlayTimeEntity entity = null;
		if (result.isPresent()) {
			entity = result.get();
			entity.setPlaytime(entity.getPlaytime() + 1);
		} else {
			entity = new PlayTimeEntity(playerID, 1l);
		}
		try {
			database.save(entity);
		} catch (OptimisticLockException e) {
			logger.log(Level.SEVERE, "Beim Speichern eines Entities ist ein Fehler aufgetreten!", e);
			shutdownHook.run();
		}
		return entity.getPlaytime();
	}

	public Optional<Long> getPlayTime(UUID playerID) {
		Optional<Long> opt = Optional.empty();
		Optional<PlayTimeEntity> entity = getEntity(playerID);
		if (entity.isPresent()) {
			opt = Optional.of(entity.get().getPlaytime());
		}
		return opt;
	}

	public void createIfAbsent(UUID playerID) {
		if (!getEntity(playerID).isPresent()) {
			try {
				database.save(new PlayTimeEntity(playerID, 0l));
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Beim Speichern eines Entities ist ein Fehler aufgetreten!", e);
				shutdownHook.run();
			}
		}
	}

}
