package ru.dozen.mephi.meta.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.dozen.mephi.meta.domain.enums.ProjectState;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ProjectState projectState;

    @ManyToOne
    @JoinColumn(name = "director_id")
    private User director;

    @OneToMany(mappedBy = "project")
    private List<RoleRecord> roleRecords;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;
}
