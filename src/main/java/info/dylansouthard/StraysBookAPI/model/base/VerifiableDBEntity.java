package info.dylansouthard.StraysBookAPI.model.base;

import info.dylansouthard.StraysBookAPI.model.shared.VerificationStatus;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class VerifiableDBEntity extends UserRegisteredDBEntity {
    @Embedded
    private VerificationStatus verificationStatus;


    @PrePersist
    @PreUpdate
    private void initializeVerificationStatus() {
        if (verificationStatus == null) {
            verificationStatus = new VerificationStatus();
        }
    }
}
