package com.fund.server.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfileDetailResponse {
    private UserAccountResponse account;
    private CustomerProfileResponse profile;
    private List<CustomerAddressResponse> address;

}
