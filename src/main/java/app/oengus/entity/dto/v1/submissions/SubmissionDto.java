package app.oengus.entity.dto.v1.submissions;

import app.oengus.entity.model.Game;

import java.util.Set;

public class SubmissionDto {
    private int id;
    private SubmissionUserDto user;
    private Set<Game> games;

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

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }
}
