package app.oengus.adapter.jpa.entity;

import app.oengus.domain.marathon.FieldType;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "question")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "marathon_id")
    private MarathonEntity marathon;

    @Column(name = "label")
    @Size(max = 50)
    private String label;

    @Column(name = "field_type")
    @NotNull
    private FieldType fieldType;

    @Column(name = "required")
    private boolean required;

    @ElementCollection
    @CollectionTable(name = "select_option", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "question_option")
    private List<@Size(max = 50) String> options;

    @Column(name = "question_type")
    @NotBlank
    @Size(max = 10)
    private String questionType;

    @Column(name = "description")
    @Size(max = 1000)
    private String description;

    @Column(name = "position")
    private int position;
}
