package com.fund.server.accountservice.repository;

import com.fund.server.accountservice.domain.CustomerProfile;
import com.fund.server.accountservice.domain.emuns.Gender;
import com.fund.server.accountservice.dto.CustomerProfileDetail;
import com.fund.server.accountservice.dto.CustomerProfileResponse;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface CustomerProfileRepository extends ReactiveCrudRepository<CustomerProfile, UUID> {
    @Query("""
    SELECT
         ua.id AS user_account_id,
         ua.country_code,
         ua.phone_number,
         ua.referral_code,
         ua.user_status,
         ua.user_type,

         cp.id AS profile_id,
         cp.first_name,
         cp.last_name,
         cp.gender,
         cp.nationality_number,
         cp.passport_number,
         cp.date_of_birth,
         cp.email,
         cp.photo_profile,
         cp.photo_object_key,
         cp.created_at,
         cp.updated_at,

         ca.id AS address_id,
         ca.line_1 AS line1,
         ca.province_code,
         ca.district_code,
         ca.commune_code,
         ca.village_code

    FROM user_account ua
    INNER JOIN customer_profile cp
        ON ua.id = cp.user_account_id
    LEFT JOIN customer_address ca
        ON cp.id = ca.customer_profile_id
    WHERE ua.id = :userAccountId
    """)
    Flux<CustomerProfileDetail> findCustomerById(UUID customerId);

    @Query("""
    SELECT
         ua.id AS user_account_id,
         ua.country_code,
         ua.phone_number,
         ua.referral_code,
         ua.user_status,
         ua.user_type,

         cp.id AS profile_id,
         cp.first_name,
         cp.last_name,
         cp.gender,
         cp.nationality_number,
         cp.passport_number,
         cp.date_of_birth,
         cp.email,
         cp.photo_profile,
         cp.photo_object_key,
         cp.created_at,
         cp.updated_at,

         ca.id AS address_id,
         ca.line_1 AS line1,
         ca.province_code,
         ca.district_code,
         ca.commune_code,
         ca.village_code

    FROM user_account ua
    INNER JOIN customer_profile cp
        ON ua.id = cp.user_account_id
    LEFT JOIN customer_address ca
        ON cp.id = ca.customer_profile_id
    WHERE ua.id = :userAccountId
    """)
    Flux<CustomerProfileDetail> findCustomerByAccountId(UUID userAccountId);

    @Query("""
            SELECT
                ua.id AS user_account_id,
                ua.country_code,
                ua.phone_number,
                ua.referral_code,
                ua.user_status,
                ua.user_type,
            
                cp.id AS profile_id,
                cp.first_name,
                cp.last_name,
                cp.gender,
                cp.nationality_number,
                cp.passport_number,
                cp.date_of_birth,
                cp.email,
                cp.photo_profile,
                cp.photo_object_key,
                cp.created_at,
                cp.updated_at,
            
                ca.id AS address_id,
                ca.line_1,
                ca.province_code,
                ca.district_code,
                ca.commune_code,
                ca.village_code
            
            FROM user_account ua
            INNER JOIN customer_profile cp
                ON ua.id = cp.user_account_id
            LEFT JOIN customer_address ca
                ON cp.id = ca.customer_profile_id
            ORDER BY cp.created_at DESC
            LIMIT :size OFFSET :offset
            """)
    Flux<CustomerProfileDetail> findCustomerProfileByFilter(int size, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM user_account ua
            INNER JOIN customer_profile cp
                ON ua.id = cp.user_account_id
            LEFT JOIN customer_address ca
                ON cp.id = ca.customer_profile_id
            """)
    Mono<Long> countCustomerProfiles();

    @Modifying
    @Query("""
            UPDATE customer_profile
            SET
                first_name = :firstName,
                last_name = :lastName,
                gender = :gender,
                nationality_number = :nationalityNumber,
                passport_number = :passportNumber,
                date_of_birth = :dateOfBirth,
                email = :email,
                updated_at = NOW()
            WHERE user_account_id = :userId
            """)
    Mono<Integer> updateCustomerProfile(
            UUID userId,
            String firstName,
            String lastName,
            Gender gender,
            String nationalityNumber,
            String passportNumber,
            LocalDate dateOfBirth,
            String email
    );

    @Query("""
    SELECT
        ua.id AS user_account_id,
        ua.country_code,
        ua.phone_number,
        ua.referral_code,
        ua.user_status,
        ua.user_type,

        cp.id AS profile_id,
        cp.first_name,
        cp.last_name,
        cp.gender,
        cp.nationality_number,
        cp.passport_number,
        cp.date_of_birth,
        cp.email,
        cp.photo_profile,
        cp.photo_object_key,
        cp.created_at,
        cp.updated_at,

        ca.id AS address_id,
        ca.line_1,
        ca.province_code,
        ca.district_code,
        ca.commune_code,
        ca.village_code

    FROM user_account ua
    INNER JOIN customer_profile cp
        ON ua.id = cp.user_account_id
    LEFT JOIN customer_address ca
        ON cp.id = ca.customer_profile_id
    WHERE ua.id = :userId
    """)
    Mono<CustomerProfileResponse> findCustomerDetailByUserId(UUID userId);

    @Query("""
    SELECT
        ua.id AS user_account_id,
        ua.keycloak_user_id AS keycloak_user_id,
        ua.country_code,
        ua.phone_number,
        ua.referral_code,
        ua.user_status,
        ua.user_type,

        cp.id AS profile_id,
        cp.first_name,
        cp.last_name,
        cp.gender,
        cp.nationality_number,
        cp.passport_number,
        cp.date_of_birth,
        cp.email,
        cp.photo_profile,
        cp.photo_object_key,
        cp.created_at,
        cp.updated_at,

        ca.id AS address_id,
        ca.line_1 AS line1,
        ca.province_code,
        ca.district_code,
        ca.commune_code,
        ca.village_code

    FROM user_account ua

    INNER JOIN customer_profile cp
        ON ua.id = cp.user_account_id

    LEFT JOIN customer_address ca
        ON cp.id = ca.customer_profile_id

    WHERE ua.keycloak_user_id = :keycloakUserId
    """)
    Flux<CustomerProfileDetail> findByKeycloakId(String keycloakUserId);
}
