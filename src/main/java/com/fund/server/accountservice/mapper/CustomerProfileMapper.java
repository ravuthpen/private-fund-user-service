package com.fund.server.accountservice.mapper;


import com.fund.server.accountservice.domain.CustomerProfile;
import com.fund.server.accountservice.dto.CustomerProfileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerProfileMapper {
    CustomerProfileResponse toResponse(CustomerProfile customerProfile);
}
