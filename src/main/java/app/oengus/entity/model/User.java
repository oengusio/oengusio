package app.oengus.entity.model;

import app.oengus.spring.model.Role;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Public.class)
	private Integer id;

	@Column
	@JsonView(Views.Public.class)
	@Size(min = 3, max = 16)
	@Pattern(regexp = "^[\\w\\-]{3,16}$")
	private String username;

	@Column(name = "username_ja")
	@JsonView(Views.Public.class)
	@Size(max = 16)
	private String usernameJapanese;

	@Column(name = "active")
	@JsonView(Views.Public.class)
	private Boolean enabled;

	@ElementCollection
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Column(name = "role")
	@JsonView(Views.Public.class)
	private List<Role> roles;

	@Column
	@JsonView(Views.Internal.class)
	@Email
	private String mail;

	@Column(name = "discord_id")
	@JsonView(Views.Internal.class)
	private String discordId;

	@Column(name = "twitch_id")
	@JsonView(Views.Internal.class)
	private String twitchId;

	@Column(name = "twitter_id")
	@JsonView(Views.Internal.class)
	private String twitterId;

	@JsonView(Views.Internal.class)
	@Transient
	private String discordName;

	@Column(name = "twitter_name")
	@JsonView(Views.Public.class)
	@Size(max = 15)
	private String twitterName;

	@Column(name = "twitch_name")
	@JsonView(Views.Public.class)
	@Size(max = 25)
	private String twitchName;

	@Column(name = "speedruncom_name")
	@JsonView(Views.Public.class)
	@Size(max = 20)
	@Pattern(regexp = "^[\\w\\.\\-À-Üà-øoù-ÿŒœ]{0,20}$")
	private String speedruncomName;

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (final Role r : this.roles) {
			authorities.add(new SimpleGrantedAuthority(r.toString()));
		}
		return authorities;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@AssertTrue
	public boolean isAtLeastOneAccountSynchronized() {
		return !StringUtils.isEmpty(this.discordId) || !StringUtils.isEmpty(this.twitchId) ||
				!StringUtils.isEmpty(this.twitterId);
	}

	@AssertTrue
	public boolean isEmailPresentForExistingUser() {
		if (this.id != null) {
			return !StringUtils.isEmpty(this.mail);
		}
		return true;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public void setEnabled(final Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(final List<Role> roles) {
		this.roles = roles;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getMail() {
		return this.mail;
	}

	public void setMail(final String mail) {
		this.mail = mail;
	}

	public String getDiscordId() {
		return this.discordId;
	}

	public void setDiscordId(final String discordId) {
		this.discordId = discordId;
	}

	public String getTwitchId() {
		return this.twitchId;
	}

	public void setTwitchId(final String twitchId) {
		this.twitchId = twitchId;
	}

	public String getDiscordName() {
		return this.discordName;
	}

	public void setDiscordName(final String discordName) {
		this.discordName = discordName;
	}

	public String getTwitterName() {
		return this.twitterName;
	}

	public void setTwitterName(final String twitterName) {
		this.twitterName = twitterName;
	}

	public String getTwitchName() {
		return this.twitchName;
	}

	public void setTwitchName(final String twitchName) {
		this.twitchName = twitchName;
	}

	public String getSpeedruncomName() {
		return this.speedruncomName;
	}

	public void setSpeedruncomName(final String speedruncomName) {
		this.speedruncomName = speedruncomName;
	}

	public String getUsernameJapanese() {
		return this.usernameJapanese;
	}

	public void setUsernameJapanese(final String usernameJapanese) {
		this.usernameJapanese = usernameJapanese;
	}

	public String getTwitterId() {
		return this.twitterId;
	}

	public void setTwitterId(final String twitterId) {
		this.twitterId = twitterId;
	}

	@JsonIgnore
	public String getUsername(final String locale) {
		if ("ja".equals(locale) && StringUtils.isNotEmpty(this.usernameJapanese)) {
			return this.usernameJapanese;
		}
		return this.username;
	}
}
