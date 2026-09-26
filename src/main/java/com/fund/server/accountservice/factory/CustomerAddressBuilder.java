package com.fund.server.accountservice.factory;

import com.fund.server.accountservice.domain.CustomerAddress;
import com.fund.server.accountservice.dto.UpdateCustomerAddressRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerAddressBuilder {
    private final Clock clock;

    public CustomerAddress create( UUID customerProfileId, UpdateCustomerAddressRequest request) {
        Instant now = clock.instant();

        return CustomerAddress.builder()
                .customerProfileId(customerProfileId)
                .lin1(request.line1())
                .provinceCode(request.provinceCode())
                .districtCode(request.districtCode())
                .communeCode(request.communeCode())
                .villageCode(request.villageCode())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }


//    public CustomerAddress create(
//            UUID customerProfileId,
//            String lin1,
//            String provinceCode,
//            String districtCode,
//            String communeCode,
//            String villageCode
//    ) {
//
//
//        return CustomerAddress.builder()
//                .customerProfileId(customerProfileId)
//                .lin1(lin1)
//                .provinceCode(provinceCode)
//                .districtCode(districtCode)
//                .communeCode(communeCode)
//                .villageCode(villageCode)
//
//                .build();
//    }
}
