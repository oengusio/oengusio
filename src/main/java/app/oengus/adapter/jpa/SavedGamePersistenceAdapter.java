package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.entity.User;
import app.oengus.adapter.jpa.mapper.SavedGameEntityMapper;
import app.oengus.adapter.jpa.repository.SavedGameRepository;
import app.oengus.application.port.persistence.SavedGamePersistencePort;
import app.oengus.domain.user.SavedGame;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SavedGamePersistenceAdapter implements SavedGamePersistencePort {
    private final SavedGameRepository repository;
    private final SavedGameEntityMapper mapper;

    @Override
    public Optional<SavedGame> findById(int id) {
        return this.repository.findById(id).map(this.mapper::toDomain);
    }

    @Override
    public Page<SavedGame> findAllByUser(int userId, Pageable pageable) {
        return this.repository.findByUser(User.ofId(userId), pageable)
            .map(this.mapper::toDomain);
    }

    @Override
    public SavedGame save(SavedGame savedGame) {
        final var entity = this.mapper.fromDomain(savedGame);

        if (entity.getId() < 1) {
            entity.setId(null);
        }

        entity.getCategories().forEach((c) -> {
            if (c.getId() < 1) {
                c.setId(null);
            }
        });

        final var saved = this.repository.save(entity);

        return this.mapper.toDomain(saved);
    }

    @Override
    public void delete(SavedGame savedGame) {
        this.repository.deleteById(savedGame.getId());
    }
}
