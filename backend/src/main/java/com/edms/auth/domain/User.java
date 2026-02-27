package com.edms.auth.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_auth_user", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @Column(name = "user_uuid")
    private UUID userUuid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String position;

    @Column(length = 20)
    private String phone;

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @PrePersist
    void prePersist() {
        if (this.userUuid == null) this.userUuid = UUID.randomUUID();
        if (this.activeFlag == null) this.activeFlag = true;
        if (this.provider == null) this.provider = "LOCAL";
    }

    @Builder
    public User(String email, String name, String department, String position,
                String phone, String provider, String providerId) {
        this.email = email;
        this.name = name;
        this.department = department;
        this.position = position;
        this.phone = phone;
        this.provider = provider != null ? provider : "LOCAL";
        this.providerId = providerId;
    }

    public void updateProfile(String name, String department, String position, String phone) {
        if (name != null) this.name = name;
        if (department != null) this.department = department;
        if (position != null) this.position = position;
        if (phone != null) this.phone = phone;
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }

    public void deactivate() {
        this.activeFlag = false;
    }

    public void activate() {
        this.activeFlag = true;
    }
}
