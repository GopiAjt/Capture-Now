package com.capturenow.module;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
public class PhotographerKycDetails {

    @Id
    private String kycId;

    private Long bankAccountNumber;

    private String ifscCode;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id_proof_image")
    private byte[] idProofImage;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "studio_licence")
    private byte[] studioLicence;

    private Boolean kycStatus;

    @OneToOne
    private Photographer photographer;

    public PhotographerKycDetails(){
        this.kycId = generateCustomId();
    }

    private String generateCustomId() {
        // Implement your custom ID generation logic here
        // Example: return UUID.randomUUID().toString();
        // You can use any logic to create a unique identifier
        return "CN-P" + UUID.randomUUID();
    }

}
