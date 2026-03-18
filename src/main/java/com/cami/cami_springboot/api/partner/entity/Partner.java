package com.cami.cami_springboot.api.partner.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "partner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Partner {

    @Id
    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "business_number", length = 20)
    private String businessNumber;

    @Column(name = "contact_name", length = 50)
    private String contactName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(length = 20)
    private String status = "PENDING";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Partner(String userId, String companyName, String businessNumber, String contactName, String contactPhone, String status) {
        this.userId = userId;
        this.companyName = companyName;
        this.businessNumber = businessNumber;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.status = status != null ? status : "PENDING";
    }

    public void update(String companyName, String contactName, String contactPhone) {
        if (companyName != null) this.companyName = companyName;
        if (contactName != null) this.contactName = contactName;
        if (contactPhone != null) this.contactPhone = contactPhone;
    }

    public void updateStatus(String status) {
        if (status != null) this.status = status;
    }
}
