package com.fund.server.accountservice.repository;

import com.fund.server.accountservice.domain.UserAccount;
import com.fund.server.accountservice.domain.emuns.ReferralCode;
import com.fund.server.accountservice.domain.emuns.UserStatus;
import com.fund.server.accountservice.domain.emuns.UserType;
import com.fund.server.accountservice.dto.UserAccountRequest;
import com.fund.server.accountservice.dto.UserAccountResponse;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserAccountRepository extends ReactiveCrudRepository<UserAccount, UUID> {

    Mono<UserAccount> findByCountryCodeAndPhoneNumberAndUserType(String countryCode, String phoneNumber, UserType userType);

    @Modifying
    @Query("""
            UPDATE user_account
            SET
                country_code = :countryCode,
                phone_number = :phoneNumber,
                referral_code = :referralCode,
                user_status = :userStatus,
                user_type = :userType
            WHERE id = :userId
            """)
    Mono<Integer> updateUserAccount(
            UUID userId,
            String countryCode,
            String phoneNumber,
            ReferralCode referralCode,
            UserStatus userStatus,
            UserType userType
    );


}
