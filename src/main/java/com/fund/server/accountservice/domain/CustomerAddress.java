package com.fund.server.accountservice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("customer_address")
public class CustomerAddress {

    @Id
    private UUID id;

    @Column("customer_profile_id")
    private UUID customerProfileId;

    @Column("province_code")
    private String provinceCode;

    @Column("district_code")
    private String districtCode;

    @Column("commune_code")
    private String communeCode;

    @Column("village_code")
    private String villageCode;

    @Column("line_1")
    private String lin1;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
