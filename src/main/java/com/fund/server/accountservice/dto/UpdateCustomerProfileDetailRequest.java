package com.fund.server.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerProfileDetailRequest {
    private UserAccountRequest account;
    private UpdateCustomerRequest profile;
    private List<UpdateCustomerAddressRequest> address;
}
