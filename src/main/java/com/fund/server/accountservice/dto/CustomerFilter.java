package com.fund.server.accountservice.dto;

import com.fund.server.accountservice.domain.emuns.Gender;
import com.fund.server.accountservice.domain.emuns.ReferralCode;
import com.fund.server.accountservice.domain.emuns.UserStatus;
import com.fund.server.accountservice.domain.emuns.UserType;
import lombok.Builder;

@Builder
public record CustomerFilter(
        String phoneNumber,
        String name,
        String age,
        Gender gender,
        UserStatus status,
        ReferralCode referralCode,
        UserType userType,
        Integer page,
        Integer size
) {
}
