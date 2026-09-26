package com.fund.server.accountservice.mapper;

import com.fund.server.accountservice.domain.UserAccount;
import com.fund.server.accountservice.dto.UserAccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    @Mapping(target = "userAccountId", source = "userAccount.id")
   // @Mapping(target = "createdAt", source = "userAccount.createdAt")
    UserAccountResponse toResponse(UserAccount userAccount);
}
