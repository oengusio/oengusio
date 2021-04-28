package app.oengus.entity.model;

import org.hibernate.Hibernate;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "selection")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Selection {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "marathon_id")
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Marathon marathon;

	@OneToOne
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Category category;

	@Column(name = "status")
	private Status status;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(final Category category) {
		this.category = category;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public Marathon getMarathon() {
		return this.marathon;
	}

	public void setMarathon(final Marathon marathon) {
		this.marathon = marathon;
	}

	public static void initialize(Selection selection) {
        Hibernate.initialize(selection.getCategory());
        Hibernate.initialize(selection.getMarathon());
        Hibernate.initialize(selection.getStatus());
    }
}
