package app.oengus.service.repository;

import app.oengus.dao.UserRepository;
import app.oengus.entity.model.User;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRepositoryService {
	private final UserRepository userRepository;

    public User findById(final int runnerId) throws NotFoundException {
		return this.userRepository.findById(runnerId)
		                          .orElseThrow(() -> new NotFoundException("User not found"));
	}

	public User findByUsername(final String username) throws NotFoundException {
		return this.userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found"));
	}

    public Optional<User> findByEmail(final String email) {
        return this.userRepository.findByMail(email);
    }

	public Optional<User> findByUsernameRaw(final String username) {
		return this.userRepository.findByUsername(username);
	}

	public User update(final User user) {
		return this.userRepository.save(user);
	}

	public List<User> findByUsernameContainingIgnoreCase(final String username) {
		return this.userRepository.findByUsernameContainingIgnoreCaseAndEnabledTrue(username);
	}

	public boolean existsByUsername(final String name) {
		return this.userRepository.existsByUsernameIgnoreCase(name);
	}

	public boolean existsByDiscordId(final String discordId) {
		return this.userRepository.existsByDiscordId(discordId);
	}

	public boolean existsByTwitchId(final String twitchId) {
		return this.userRepository.existsByTwitchId(twitchId);
	}

	public User findByDiscordId(final String id) {
		return this.userRepository.findByDiscordId(id);
	}

	public User findByTwitchId(final String id) {
		return this.userRepository.findByTwitchId(id);
	}

	public User findByTwitterId(final String id) {
		return this.userRepository.findByTwitterId(id);
	}

	public User findByPatreonId(final String id) {
		return this.userRepository.findByPatreonId(id);
	}

	public User save(final User user) {
		return this.userRepository.save(user);
	}
}
