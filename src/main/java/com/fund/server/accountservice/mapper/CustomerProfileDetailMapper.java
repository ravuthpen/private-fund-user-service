package com.fund.server.accountservice.mapper;

import com.fund.server.accountservice.dto.*;
import com.fund.server.accountservice.util.AgeCalculator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerProfileDetailMapper {

    /**
     * Folds the N joined rows belonging to ONE customer into a single response.
     * Every row carries the same account+profile columns; each carries at most one address.
     */
    public CustomerProfileDetailResponse toResponse(List<CustomerProfileDetail> rows) {
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("rows must not be empty");
        }
        CustomerProfileDetail first = rows.get(0);

        return new CustomerProfileDetailResponse(
                toAccount(first),
                toProfile(first),
                toAddresses(rows));
    }

    private UserAccountResponse toAccount(CustomerProfileDetail r) {
        return new UserAccountResponse(
                r.userAccountId(),
                r.keycloakUserId(),
                r.countryCode(),
                r.phoneNumber(),
                r.referralCode(),
                r.userStatus(),
                r.userType());
    }

    private CustomerProfileResponse toProfile(CustomerProfileDetail r) {
        return new CustomerProfileResponse(
                r.profileId(),
                r.firstName(),
                r.lastName(),
                r.gender(),
                r.nationalityNumber(),
                r.passportNumber(),
                r.dateOfBirth(),
                AgeCalculator.calculate(r.dateOfBirth()),
                r.email(),
                r.photoProfile(),
                r.photoObjectKey(),
                r.createdAt(),
                r.updatedAt());
    }

    private List<CustomerAddressResponse> toAddresses(List<CustomerProfileDetail> rows) {
        return rows.stream()
                .filter(r -> r.addressId() != null)   // LEFT JOIN miss → no address
                .map(this::toAddress)
                .toList();
    }

    private CustomerAddressResponse toAddress(CustomerProfileDetail r) {
        return new CustomerAddressResponse(
                r.addressId(),
                r.line1(),
                r.provinceCode(),
                r.districtCode(),
                r.communeCode(),
                r.villageCode());
    }
}