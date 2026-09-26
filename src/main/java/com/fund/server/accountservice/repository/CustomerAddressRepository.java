package com.fund.server.accountservice.repository;

import com.fund.server.accountservice.domain.CustomerAddress;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerAddressRepository extends ReactiveCrudRepository<CustomerAddress, UUID> {

    /*@Modifying
    @Query("""
            UPDATE customer_address
            SET
                line_1 = :line1,
                province_code = :provinceCode,
                district_code = :districtCode,
                commune_code = :communeCode,
                village_code = :villageCode,
                updated_at = now()
            WHERE customer_profile_id = :customerId
    """)
    Mono<Integer> updateCustomerAddress(
            UUID customerId,
            String line1,
            String provinceCode,
            String districtCode,
            String communeCode,
            String villageCode
    );*/
    @Modifying
    @Query("""
    UPDATE customer_address ca
    SET
        line_1 = :line1,
        province_code = :provinceCode,
        district_code = :districtCode,
        commune_code = :communeCode,
        village_code = :villageCode
    FROM customer_profile cp
    WHERE ca.customer_profile_id = cp.id
      AND cp.user_account_id = :userId
    """)
    Mono<Integer> updateCustomerAddress(
            UUID userId,
            String line1,
            String provinceCode,
            String districtCode,
            String communeCode,
            String villageCode
    );

    @Query("""
    SELECT
        ca.id,
        ca.customer_profile_id,
        ca.line_1,
        ca.province_code,
        ca.district_code,
        ca.commune_code,
        ca.village_code

    FROM customer_address ca
    INNER JOIN customer_profile cp
        ON ca.customer_profile_id = cp.id
    WHERE cp.id = :customerProfileId
    """)
    Mono<CustomerAddress> findByCustomerProfileId(UUID customerProfileId);
}
