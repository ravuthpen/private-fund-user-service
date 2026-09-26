package com.fund.server.accountservice.dto;

public record UpdateCustomerAddressRequest(
       // UUID customerId,
         String line1,
         String provinceCode,
         String districtCode,
         String communeCode,
         String villageCode
) {
}
