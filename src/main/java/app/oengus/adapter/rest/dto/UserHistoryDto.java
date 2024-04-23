package app.oengus.adapter.rest.dto;

import app.oengus.adapter.jpa.entity.GameEntity;
import app.oengus.adapter.jpa.entity.OpponentEntity;

import java.time.ZonedDateTime;
import java.util.List;

public class UserHistoryDto {

	private String marathonId;
	private String marathonName;
	private ZonedDateTime marathonStartDate;
	private List<GameEntity> games;
	private List<OpponentEntity> opponents;

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

	public List<GameEntity> getGames() {
		return this.games;
	}

	public void setGames(final List<GameEntity> games) {
		this.games = games;
	}

	public List<OpponentEntity> getOpponents() {
		return this.opponents;
	}

	public void setOpponents(final List<OpponentEntity> opponents) {
		this.opponents = opponents;
	}
}
