package info.dylansouthard.StraysBookAPI.model.user;

import info.dylansouthard.StraysBookAPI.exception.DuplicateOAuthProviderException;
import info.dylansouthard.StraysBookAPI.model.Animal;
import info.dylansouthard.StraysBookAPI.model.CareEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="app_users")
@Where(clause = "is_deleted = false")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)
    private String displayName;

    @Column(unique = true)
    private String email;

    @Column
    private String intro;

    @Column
    private String profileImgUrl;

    @Column
    private Boolean isDeleted = false;

    @Column
    private LocalDateTime deletionRequestedAt;

    @ManyToMany
    @JoinTable(
            name = "watched_animals", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key for User
            inverseJoinColumns = @JoinColumn(name = "animal_id") // Foreign key for Animal
    )
    private List<Animal> watchedAnimals = new ArrayList<>();

    @ManyToMany
    private List<CareEvent> careEvents = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OAuthProvider> oAuthProviders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthToken> authTokens = new ArrayList<>();

//    @ManyToMany(fetch=FetchType.LAZY)
//    private List<Litter> watchedLitters = new ArrayList<>();

    public void addOAuthProvider(OAuthProvider provider) {
        if (this.oAuthProviders.stream().anyMatch(p->p.getProvider().equals(provider.getProvider()))){
            throw new DuplicateOAuthProviderException();
        }
        this.oAuthProviders.add(provider);
        provider.setUser(this);
    }

    public User(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }
}
