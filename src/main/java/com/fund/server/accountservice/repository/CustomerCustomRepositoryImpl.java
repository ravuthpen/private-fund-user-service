package com.fund.server.accountservice.repository;

import com.fund.server.accountservice.domain.emuns.Gender;
import com.fund.server.accountservice.domain.emuns.ReferralCode;
import com.fund.server.accountservice.domain.emuns.UserStatus;
import com.fund.server.accountservice.domain.emuns.UserType;
import com.fund.server.accountservice.dto.CustomerFilter;
import com.fund.server.accountservice.dto.CustomerProfileDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerCustomRepositoryImpl
        implements CustomerCustomRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Flux<CustomerProfileDetail> findCustomerProfileByFilter(
            CustomerFilter filter,
            int limit,
            long offset
    ) {

        StringBuilder sql = new StringBuilder("""
                SELECT
                    /* =========================
                       USER ACCOUNT
                       ========================= */

                    ua.id AS user_account_id,
                    ua.country_code,
                    ua.phone_number,
                    ua.referral_code,
                    ua.user_status,
                    ua.user_type,

                    /* =========================
                       CUSTOMER PROFILE
                       ========================= */

                    cp.id AS profile_id,
                    cp.first_name,
                    cp.last_name,
                    cp.gender,
                    cp.nationality_number,
                    cp.passport_number,
                    cp.date_of_birth,
                    cp.email,
                    cp.photo_object_key,

                    cp.created_at AS profile_created_at,
                    cp.updated_at AS profile_updated_at,

                    /* =========================
                       AGE
                       ========================= */

                    CASE
                        WHEN cp.date_of_birth IS NULL THEN NULL
                        ELSE EXTRACT(
                            YEAR FROM AGE(
                                CURRENT_DATE,
                                cp.date_of_birth
                            )
                        )::INTEGER
                    END AS age,

                    /* =========================
                       CUSTOMER ADDRESS
                       ========================= */

                    ca.id AS address_id,
                    ca.province_code,
                    ca.district_code,
                    ca.commune_code,
                    ca.village_code

                FROM user_account ua

                LEFT JOIN customer_profile cp
                    ON cp.user_account_id = ua.id

                LEFT JOIN customer_address ca
                    ON ca.customer_profile_id = cp.id

                WHERE 1 = 1
                """);

        List<QueryParameter> parameters = new ArrayList<>();

        addFilters(
                sql,
                parameters,
                filter
        );

        sql.append("""
                ORDER BY cp.created_at DESC NULLS LAST
                LIMIT :limit
                OFFSET :offset
                """);

        DatabaseClient.GenericExecuteSpec spec =
                databaseClient.sql(sql.toString());

        for (QueryParameter parameter : parameters) {
            spec = spec.bind(
                    parameter.name(),
                    parameter.value()
            );
        }

        spec = spec
                .bind("limit", limit)
                .bind("offset", offset);

        return spec
                .map((row, metadata) -> {

                    String referralCode =
                            row.get(
                                    "referral_code",
                                    String.class
                            );

                    String userStatus =
                            row.get(
                                    "user_status",
                                    String.class
                            );

                    String userType =
                            row.get(
                                    "user_type",
                                    String.class
                            );

                    String gender =
                            row.get(
                                    "gender",
                                    String.class
                            );

                    return CustomerProfileDetail.builder()

                            /* =========================
                               ACCOUNT
                               ========================= */

                            .userAccountId(
                                    row.get(
                                            "user_account_id",
                                            UUID.class
                                    )
                            )

                            .countryCode(
                                    row.get(
                                            "country_code",
                                            String.class
                                    )
                            )

                            .phoneNumber(
                                    row.get(
                                            "phone_number",
                                            String.class
                                    )
                            )

                            .referralCode(
                                    referralCode != null
                                            ? ReferralCode.valueOf(
                                            referralCode
                                    )
                                            : null
                            )

                            .userStatus(
                                    userStatus != null
                                            ? UserStatus.valueOf(
                                            userStatus
                                    )
                                            : null
                            )

                            .userType(
                                    userType != null
                                            ? UserType.valueOf(
                                            userType
                                    )
                                            : null
                            )

                            /* =========================
                               PROFILE
                               ========================= */

                            .profileId(
                                    row.get(
                                            "profile_id",
                                            UUID.class
                                    )
                            )

                            .firstName(
                                    row.get(
                                            "first_name",
                                            String.class
                                    )
                            )

                            .lastName(
                                    row.get(
                                            "last_name",
                                            String.class
                                    )
                            )

                            .gender(
                                    gender != null
                                            ? Gender.valueOf(gender)
                                            : null
                            )

                            .nationalityNumber(
                                    row.get(
                                            "nationality_number",
                                            String.class
                                    )
                            )

                            .passportNumber(
                                    row.get(
                                            "passport_number",
                                            String.class
                                    )
                            )

                            .dateOfBirth(
                                    row.get(
                                            "date_of_birth",
                                            LocalDate.class
                                    )
                            )

                            .age(
                                    row.get(
                                            "age",
                                            Integer.class
                                    )
                            )

                            .email(
                                    row.get(
                                            "email",
                                            String.class
                                    )
                            )

                            .photoObjectKey(
                                    getPhotoObjectKeys(
                                            row.get(
                                                    "photo_object_key",
                                                    String[].class
                                            )
                                    )
                            )

                            .createdAt(
                                    row.get(
                                            "profile_created_at",
                                            Instant.class
                                    )
                            )

                            .updatedAt(
                                    row.get(
                                            "profile_updated_at",
                                            Instant.class
                                    )
                            )

                            /* =========================
                               ADDRESS
                               ========================= */

                            .addressId(
                                    row.get(
                                            "address_id",
                                            UUID.class
                                    )
                            )

                            .provinceCode(
                                    row.get(
                                            "province_code",
                                            String.class
                                    )
                            )

                            .districtCode(
                                    row.get(
                                            "district_code",
                                            String.class
                                    )
                            )

                            .communeCode(
                                    row.get(
                                            "commune_code",
                                            String.class
                                    )
                            )

                            .villageCode(
                                    row.get(
                                            "village_code",
                                            String.class
                                    )
                            )

                            .build();
                })
                .all();
    }

    @Override
    public Mono<Long> countCustomerProfiles(
            CustomerFilter filter
    ) {

        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(DISTINCT cp.id)

                FROM user_account ua

                LEFT JOIN customer_profile cp
                    ON cp.user_account_id = ua.id

                LEFT JOIN customer_address ca
                    ON ca.customer_profile_id = cp.id

                WHERE 1 = 1
                """);

        List<QueryParameter> parameters = new ArrayList<>();

        addFilters(
                sql,
                parameters,
                filter
        );

        DatabaseClient.GenericExecuteSpec spec =
                databaseClient.sql(sql.toString());

        for (QueryParameter parameter : parameters) {
            spec = spec.bind(
                    parameter.name(),
                    parameter.value()
            );
        }

        return spec
                .map((row, metadata) ->
                        Objects.requireNonNull(row.get(
                                0,
                                Long.class
                        ))
                )
                .one()
                .defaultIfEmpty(0L);
    }

    private void addFilters(
            StringBuilder sql,
            List<QueryParameter> parameters,
            CustomerFilter filter
    ) {

        if (filter == null) {
            return;
        }

        /* =========================
           NAME
           firstName OR lastName
           ========================= */
        if (hasText(filter.name())) {

            sql.append("""
            AND CONCAT_WS(
                ' ',
                cp.first_name,
                cp.last_name
            ) ILIKE :name
            """);

            parameters.add(
                    new QueryParameter(
                            "name",
                            "%" + filter.name().trim() + "%"
                    )
            );
        }

        /* =========================
           AGE
           ========================= */

        if (hasText(filter.age())) {

            int age;

            try {
                age = Integer.parseInt(
                        filter.age().trim()
                );
            } catch (NumberFormatException ex) {

                throw new IllegalArgumentException(
                        "Invalid age: " + filter.age()
                );
            }

            sql.append("""
                    AND cp.date_of_birth IS NOT NULL
                    AND EXTRACT(
                        YEAR FROM AGE(
                            CURRENT_DATE,
                            cp.date_of_birth
                        )
                    )::INTEGER = :age
                    """);

            parameters.add(
                    new QueryParameter(
                            "age",
                            age
                    )
            );
        }

        /* =========================
           PHONE
           ========================= */

        if (hasText(filter.phoneNumber())) {

            sql.append("""
                    AND (
                        ua.phone_number ILIKE :phone
                        OR CONCAT(
                            COALESCE(ua.country_code, ''),
                            COALESCE(ua.phone_number, '')
                        ) ILIKE :phone
                    )
                    """);

            parameters.add(
                    new QueryParameter(
                            "phone",
                            "%" + filter.phoneNumber().trim() + "%"
                    )
            );
        }

        /* =========================
           GENDER
           ========================= */

        if (filter.gender() != null) {

            sql.append("""
                    AND cp.gender = :gender
                    """);

            parameters.add(
                    new QueryParameter(
                            "gender",
                            filter.gender().name()
                    )
            );
        }

        /* =========================
           STATUS
           ========================= */

        if (filter.status() != null) {

            sql.append("""
                    AND ua.user_status = :status
                    """);

            parameters.add(
                    new QueryParameter(
                            "status",
                            filter.status().name()
                    )
            );
        }

        /* =========================
           REFERRAL CODE
           ========================= */

        if (filter.referralCode() != null) {

            sql.append("""
                    AND ua.referral_code = :referralCode
                    """);

            parameters.add(
                    new QueryParameter(
                            "referralCode",
                            filter.referralCode().name()
                    )
            );
        }

        /* =========================
           USER TYPE
           ========================= */

        if (filter.userType() != null) {

            sql.append("""
                    AND ua.user_type = :userType
                    """);

            parameters.add(
                    new QueryParameter(
                            "userType",
                            filter.userType().name()
                    )
            );
        }
    }

    private List<String> getPhotoObjectKeys(
            String[] photoObjectKeys
    ) {

        if (photoObjectKeys == null
                || photoObjectKeys.length == 0) {

            return List.of();
        }

        return List.of(photoObjectKeys);
    }

    private boolean hasText(String value) {

        return value != null
                && !value.trim().isEmpty();
    }

    private record QueryParameter(
            String name,
            Object value
    ) {
    }
}
