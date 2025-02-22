package info.dylansouthard.StraysBookAPI.model.base;

import info.dylansouthard.StraysBookAPI.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class UserRegisteredDBEntity extends DBEntity {

    @Column
    protected String notes;

    @ManyToOne
    protected User registeredBy;

    public UserRegisteredDBEntity(User registeredBy) {
        this.registeredBy = registeredBy;
    }
}
