package app.oengus.service.repository;

import app.oengus.dao.UserRepository;
import app.oengus.entity.model.User;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRepositoryService {

	@Autowired
	private UserRepository userRepository;

	public User findById(final Integer runnerId) throws NotFoundException {
		return this.userRepository.findById(runnerId)
		                          .orElseThrow(() -> new NotFoundException("User not found"));
	}

	public User findByUsername(final String username) {
		return this.userRepository.findByUsername(username);
	}

	public void update(final User user) {
		this.userRepository.save(user);
	}

	public List<User> findByUsernameContainingIgnoreCase(final String username) {
		return this.userRepository.findByUsernameContainingIgnoreCaseAndEnabledTrue(username);
	}

	public Boolean existsByUsername(final String name) {
		return this.userRepository.existsByUsername(name) || this.userRepository.existsByUsernameJapanese(name);
	}

	public Boolean existsByDiscordId(final String discordId) {
		return this.userRepository.existsByDiscordId(discordId);
	}

	public Boolean existsByTwitchId(final String twitchId) {
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

	public User save(final User user) {
		return this.userRepository.save(user);
	}
}
