package com.mtsolutions.domain.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class UserDocument {

    private String cnhUrl;
    private String cpfUrl;
    private String rgUrl;
    private String proofOfResidenceUrl;
    private String criminalRecordUrl;
    private String passportUrl;
}
