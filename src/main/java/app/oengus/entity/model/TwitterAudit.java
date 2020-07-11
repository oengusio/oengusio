package app.oengus.entity.model;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "twitter_audit")
public class TwitterAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "marathon_id")
	private Marathon marathon;

	@Column(name = "action_date")
	private ZonedDateTime actionDate;

	@Column(name = "action")
	private String action;

	public TwitterAudit() {
	}

	public TwitterAudit(final Marathon marathon, final String action) {
		this.marathon = marathon;
		this.action = action;
		this.actionDate = ZonedDateTime.now();
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Marathon getMarathon() {
		return this.marathon;
	}

	public void setMarathon(final Marathon marathon) {
		this.marathon = marathon;
	}

	public ZonedDateTime getActionDate() {
		return this.actionDate;
	}

	public void setActionDate(final ZonedDateTime actionDate) {
		this.actionDate = actionDate;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(final String action) {
		this.action = action;
	}
}
