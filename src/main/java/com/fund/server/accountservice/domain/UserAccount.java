package com.fund.server.accountservice.domain;

import com.fund.server.accountservice.domain.emuns.ReferralCode;
import com.fund.server.accountservice.domain.emuns.UserStatus;
import com.fund.server.accountservice.domain.emuns.UserType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@Table("user_account")
public class UserAccount {
    @Id
    private UUID id;
    private String keycloakUserId;
    private String countryCode;
    private String phoneNumber;
    private ReferralCode referralCode;
    private UserStatus userStatus;
    private UserType userType;
    private Instant createdAt;
    private Instant updatedAt;
}
