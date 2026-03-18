package com.cami.cami_springboot.api.user.entity;

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
@Table(name = "pet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "pet_type", nullable = false, length = 20)
    private String petType;

    @Column(length = 50)
    private String breed;

    @Column(name = "body_type", length = 20)
    private String bodyType;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Pet(String userId, String name, String petType, String breed, String bodyType) {
        this.userId = userId;
        this.name = name;
        this.petType = petType;
        this.breed = breed;
        this.bodyType = bodyType;
    }

    public void update(String name, String petType, String breed, String bodyType) {
        if (name != null) this.name = name;
        if (petType != null) this.petType = petType;
        if (breed != null) this.breed = breed;
        if (bodyType != null) this.bodyType = bodyType;
    }
}
