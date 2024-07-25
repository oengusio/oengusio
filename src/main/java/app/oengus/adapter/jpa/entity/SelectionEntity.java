package app.oengus.adapter.jpa.entity;

import app.oengus.domain.submission.Status;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "selection")
public class SelectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "marathon_id")
    private MarathonEntity marathon;

    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CategoryEntity category;

    @Column(name = "status")
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectionEntity selection = (SelectionEntity) o;
        return Objects.equals(id, selection.id) && marathon.equals(selection.marathon) && status == selection.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, marathon, status);
    }
}
