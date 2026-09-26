package com.fund.server.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CustomerAddressResponse(
         UUID addressId,
         @JsonProperty("line1")
         String line1,
         String provinceCode,
         String districtCode,
         String communeCode,
         String villageCode
) {
}
