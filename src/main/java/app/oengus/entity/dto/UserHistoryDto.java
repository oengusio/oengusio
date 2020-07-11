package app.oengus.entity.dto;

import app.oengus.entity.model.Game;
import app.oengus.entity.model.Opponent;

import java.time.ZonedDateTime;
import java.util.List;

public class UserHistoryDto {

	private String marathonId;
	private String marathonName;
	private ZonedDateTime marathonStartDate;
	private List<Game> games;
	private List<Opponent> opponents;

	public String getMarathonId() {
		return this.marathonId;
	}

	public void setMarathonId(final String marathonId) {
		this.marathonId = marathonId;
	}

	public String getMarathonName() {
		return this.marathonName;
	}

	public void setMarathonName(final String marathonName) {
		this.marathonName = marathonName;
	}

	public ZonedDateTime getMarathonStartDate() {
		return this.marathonStartDate;
	}

	public void setMarathonStartDate(final ZonedDateTime marathonStartDate) {
		this.marathonStartDate = marathonStartDate;
	}

	public List<Game> getGames() {
		return this.games;
	}

	public void setGames(final List<Game> games) {
		this.games = games;
	}

	public List<Opponent> getOpponents() {
		return this.opponents;
	}

	public void setOpponents(final List<Opponent> opponents) {
		this.opponents = opponents;
	}
}
