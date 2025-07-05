package app.oengus.application;

import app.oengus.application.port.persistence.SavedGamePersistencePort;
import app.oengus.domain.user.SavedGame;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedGameService {
    private final SavedGamePersistencePort savedGamePort;

    public List<SavedGame> getByUserId(int userId) {
        return this.savedGamePort.findAllByUser(userId, Pageable.unpaged()).getContent();
    }

    public SavedGame save(SavedGame game) {
        return this.savedGamePort.save(game);
    }

}
