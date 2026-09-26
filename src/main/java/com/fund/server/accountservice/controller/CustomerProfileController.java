package com.fund.server.accountservice.controller;

import com.fund.server.accountservice.dto.*;
import com.fund.server.accountservice.service.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer-profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileService service;

    @GetMapping("/{userAccountId}")
    public Mono<CustomerProfileDetailResponse> getCustomerProfileDetail(@PathVariable UUID userAccountId){
        return service.findCustomerByAccountId(userAccountId);
    }

    @GetMapping("/my-profile")
    public Mono<CustomerProfileDetailResponse> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakUserId = jwt.getSubject();

        return service.findKeycloakId(keycloakUserId);
    }

    /*@GetMapping
    public Mono<PageResponse<CustomerProfileDetailResponse>> getProfileByFilterPagination(final CustomerProfileDetailFilter filter){
        return service.findCustomerProfileFilterByPagination(filter);

    }*/
    @GetMapping
    public Mono<PageResponse<CustomerProfileDetailResponse>> getCustomerByFilterPagination(final CustomerFilter filter){
        return service.getCustomerByFiltersPagination(filter);

    }

    @PatchMapping("/{userId}/update-profile")
    public Mono<String> updateCustomerProfile(@PathVariable UUID userId, @RequestBody UpdateCustomerProfileDetailRequest request){
         return service.update(userId,request);
    }


}
