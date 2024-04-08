package app.oengus.entity.model;

import app.oengus.adapter.jpa.entity.MarathonEntity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "twitter_audit")
public class TwitterAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "marathon_id")
	private MarathonEntity marathon;

	@Column(name = "action_date")
	private ZonedDateTime actionDate;

	@Column(name = "action")
	private String action;

	public TwitterAudit() {
	}

	public TwitterAudit(final MarathonEntity marathon, final String action) {
		this.marathon = marathon;
		this.action = action;
		this.actionDate = ZonedDateTime.now();
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public MarathonEntity getMarathon() {
		return this.marathon;
	}

	public void setMarathon(final MarathonEntity marathon) {
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
