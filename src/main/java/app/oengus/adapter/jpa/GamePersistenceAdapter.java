package app.oengus.adapter.jpa;

import app.oengus.adapter.jpa.mapper.GameMapper;
import app.oengus.adapter.jpa.repository.GameRepository;
import app.oengus.application.port.persistence.GamePersistencePort;
import app.oengus.domain.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GamePersistenceAdapter implements GamePersistencePort {
    private final GameRepository repository;
    private final GameMapper mapper;

    @Override
    public Optional<Game> findById(int id) {
        return this.repository.findById(id).map(this.mapper::toDomain);
    }
}
