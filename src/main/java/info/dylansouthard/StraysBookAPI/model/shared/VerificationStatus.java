package info.dylansouthard.StraysBookAPI.model.shared;

import info.dylansouthard.StraysBookAPI.model.enums.VerificationStatusType;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class VerificationStatus {
    private VerificationStatusType status = VerificationStatusType.UNVERIFIED;
    private String proofUrl;
//    @ManyToOne
//    private User verifiedBy;

}
