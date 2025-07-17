package fr.arthur.devoir_java.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import fr.arthur.devoir_java.view.UserView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(UserView.class)
    protected Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank(groups = {add.class})
    @JsonView(UserView.class)
    protected String pseudo;

    @Column(nullable = false)
    @NotBlank(groups = {add.class})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String password;

    @Column(nullable = false)
    @JsonView(UserView.class)
    protected boolean admin = false;

    @OneToMany(mappedBy = "submittingUser", cascade = CascadeType.ALL)
    @JsonIgnore
    protected List<Ticket> submittedTickets;

    @OneToMany(mappedBy = "resolvingUser", cascade = CascadeType.ALL)
    @JsonIgnore
    protected List<Ticket> resolvedTickets;

    public interface add {
    }

    public interface update {
    }

}
