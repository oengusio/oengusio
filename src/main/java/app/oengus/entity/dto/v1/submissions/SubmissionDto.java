package app.oengus.entity.dto.v1.submissions;

import app.oengus.entity.model.GameEntity;

import java.util.Set;

public class SubmissionDto {
    private int id;
    private SubmissionUserDto user;
    private Set<GameEntity> games;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SubmissionUserDto getUser() {
        return user;
    }

    public void setUser(SubmissionUserDto user) {
        this.user = user;
    }

    public Set<GameEntity> getGames() {
        return games;
    }

    public void setGames(Set<GameEntity> games) {
        this.games = games;
    }
}
