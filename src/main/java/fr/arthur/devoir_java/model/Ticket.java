package fr.arthur.devoir_java.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ticket {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;


    @NotBlank
    @Column(unique = true, nullable = false)
    protected String title;


    protected String description;


    @Column(nullable = false)
    protected boolean resolved;

    @ManyToOne(optional = false)
    @JoinColumn(name = "priority_id", nullable = false)
    protected Priority priority;

    @ManyToMany
    @JoinTable(name = "ticket_category",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    protected List<Category> categories;

    @ManyToOne
    @JoinColumn(name = "submitting_user_id")
    protected User submittingUser;

    @ManyToOne
    @JoinColumn(name = "resolving_user_id")
    protected User resolvingUser;

    public interface save {
    }

    public interface update {
    }

}
