package info.dylansouthard.StraysBookAPI.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@MappedSuperclass
public class UserRegisteredDBEntity extends DBEntity {

    @Column
    private String notes;

//    @OneToOne
//    private User registeredBy;
}
