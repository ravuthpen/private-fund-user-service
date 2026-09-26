package com.fund.server.accountservice.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomerAddressRequest(
        UUID customerProfileId,
        String line1,
        String provinceCode,
        String districtCode,
        String communeCode,
        String villageCode,
        Instant createdAd,
        Instant updatedAt
) {
}
