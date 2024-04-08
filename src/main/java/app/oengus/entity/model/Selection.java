package app.oengus.entity.model;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.helper.BeanHelper;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "selection")
public class Selection {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "marathon_id")
	private MarathonEntity marathon;

	@OneToOne
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	private CategoryEntity category;

	@Column(name = "status")
	private Status status;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public CategoryEntity getCategory() {
		return this.category;
	}

	public void setCategory(final CategoryEntity category) {
		this.category = category;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public MarathonEntity getMarathon() {
		return this.marathon;
	}

	public void setMarathon(final MarathonEntity marathon) {
		this.marathon = marathon;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Selection selection = (Selection) o;
        return id == selection.id && marathon.equals(selection.marathon) && status == selection.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, marathon, status);
    }

    public static Selection createDetached(Selection selection) {
        final Selection fresh = new Selection();
        Hibernate.initialize(selection.getCategory());
        Hibernate.initialize(selection.getCategory().getGame());
        Hibernate.initialize(selection.getCategory().getOpponents());
        Hibernate.initialize(selection.getMarathon());

        BeanHelper.copyProperties(selection, fresh);

        return fresh;
    }
}
