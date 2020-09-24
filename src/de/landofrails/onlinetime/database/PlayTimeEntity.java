package de.landofrails.onlinetime.database;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name = "playtime")
public class PlayTimeEntity {

	public PlayTimeEntity() {

	}

	public PlayTimeEntity(UUID playerID, Long playtime) {
		setPlayerID(playerID);
		setPlaytime(playtime);
	}

	@Id
	private UUID playerID;

	@NotNull
	private Long playtime;

	public void setPlayerID(UUID playerID) {
		this.playerID = playerID;
	}

	public UUID getPlayerID() {
		return playerID;
	}

	public void setPlaytime(Long playtime) {
		this.playtime = playtime;
	}

	public Long getPlaytime() {
		return playtime;
	}

}
