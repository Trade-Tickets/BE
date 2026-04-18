package com.royce.hackerhouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profiles", schema = "public")
public class ProfileEntity {

    @Id
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String username;

    @Column(name = "full_name", columnDefinition = "TEXT")
    private String fullName;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "sui_wallet_address", columnDefinition = "TEXT")
    private String suiWalletAddress;
}
