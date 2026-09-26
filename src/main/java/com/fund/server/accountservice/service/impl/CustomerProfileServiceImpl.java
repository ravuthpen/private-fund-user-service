package com.fund.server.accountservice.service.impl;

import com.fund.server.accountservice.domain.CustomerAddress;
import com.fund.server.accountservice.dto.*;
import com.fund.server.accountservice.exception.NotFoundException;
import com.fund.server.accountservice.factory.CustomerAddressBuilder;
import com.fund.server.accountservice.mapper.CustomerProfileDetailMapper;
import com.fund.server.accountservice.repository.CustomerAddressRepository;
import com.fund.server.accountservice.repository.CustomerCustomRepository;
import com.fund.server.accountservice.repository.CustomerProfileRepository;
import com.fund.server.accountservice.repository.UserAccountRepository;
import com.fund.server.accountservice.service.CustomerProfileService;
import com.fund.server.accountservice.service.PhoneNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileServiceImpl implements CustomerProfileService {
    private final CustomerProfileRepository customerRepository;
    private final UserAccountRepository accountRepository;
    private final CustomerAddressRepository addressRepository;
    private final CustomerProfileDetailMapper customerProfileDetailMapper;
    private final CustomerAddressBuilder customerAddressBuilder;
    private final PhoneNumberService phoneNumberService;
    private final CustomerCustomRepository customerCustomRepository;


    @Override
    public Mono<CustomerProfileDetailResponse> findKeycloakId(String keycloakUserId) {
        return customerRepository.findByKeycloakId(keycloakUserId)
                .collectList()
                .filter(list -> !list.isEmpty())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("KeycloakId Not Found")
                ))
                .map(customerProfileDetailMapper::toResponse);
    }

    @Override
    public Mono<CustomerProfileDetailResponse> findCustomerByAccountId(UUID userAccountId) {
        return customerRepository.findCustomerByAccountId(userAccountId)
                .collectList()
                .map(customerProfileDetailMapper::toResponse);

    }

    @Override
    public Mono<PageResponse<CustomerProfileDetailResponse>> findCustomerProfileFilterByPagination(
            CustomerProfileDetailFilter filter) {

        final int page = filter.page() != null ? filter.page() : 0;
        final int size = filter.size() != null ? filter.size() : 20;

        long offset = (long) page * size;

        Mono<List<CustomerProfileDetailResponse>> content =
                customerRepository.findCustomerProfileByFilter(size, offset)
                        .collectMultimap(CustomerProfileDetail::profileId)
                        .map(map -> map.values()
                                .stream()
                                .map(rows -> customerProfileDetailMapper.toResponse(
                                        rows.stream().toList()
                                ))
                                .toList()
                        );
        Mono<Long> total = customerRepository.countCustomerProfiles();

        return Mono.zip(content, total)
                .map(tuple -> {

                    List<CustomerProfileDetailResponse> data = tuple.getT1();
                    long totalElements = tuple.getT2();

                    return new PageResponse<>(
                            data,
                            page,
                            size,
                            totalElements,
                            (int) Math.ceil((double) totalElements / size)
                            //page == 0,
                            //page >= Math.ceil((double) totalElements / size) - 1
                    );
                });
    }

    @Override
    public Mono<String> update(UUID userId, UpdateCustomerProfileDetailRequest request) {

        NormalizedPhone phone = phoneNumberService.normalize(
                request.getAccount().countryCode(),
                request.getAccount().phoneNumber()
        );

        return customerRepository.findCustomerByAccountId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("Customer not found")))
                .collectList()
                .flatMap(customer ->
                        accountRepository.updateUserAccount(
                                        userId,
                                        phone.countryCode(),
                                        phone.phoneNumber(),
                                        request.getAccount().referralCode(),
                                        request.getAccount().userStatus(),
                                        request.getAccount().userType()
                                )
                                .then(customerRepository.updateCustomerProfile(
                                        userId,
                                        request.getProfile().firstName(),
                                        request.getProfile().lastName(),
                                        request.getProfile().gender(),
                                        request.getProfile().nationalityNumber(),
                                        request.getProfile().passportNumber(),
                                        request.getProfile().dateOfBirth(),
                                        request.getProfile().email()
                                ))
                                .thenMany(
                                        Flux.fromIterable(request.getAddress())
                                                .concatMap(address -> addressRepository.findByCustomerProfileId(request.getProfile().profileId())
                                                                .flatMap(existing ->
                                                                                addressRepository.updateCustomerAddress(
                                                                                        userId,
                                                                                        address.line1(),
                                                                                        address.provinceCode(),
                                                                                        address.districtCode(),
                                                                                        address.communeCode(),
                                                                                        address.villageCode()
                                                                                )
                                                                        )
                                                        .switchIfEmpty(
                                                                Mono.defer(() ->{
                                                                    CustomerAddress newAddress = customerAddressBuilder.create(
                                                                            request.getProfile().profileId(),
                                                                            address
                                                                    );
                                                                    return addressRepository.save(newAddress)
                                                                            .thenReturn(1);
                                                                })
                                                        )


                                                )
                                )
                                .then(customerRepository.findCustomerDetailByUserId(userId))

                )
                .map(message -> "UPDATE SUCCESS.");


    }

    @Override
    public Mono<PageResponse<CustomerProfileDetailResponse>>
    getCustomerByFiltersPagination(CustomerFilter filter) {

        int page = filter != null && filter.page() != null
                ? Math.max(filter.page(), 0)
                : 0;

        int size = filter != null && filter.size() != null
                ? Math.min(Math.max(filter.size(), 1), 100)
                : 20;

        long offset = (long) page * size;

        Mono<List<CustomerProfileDetailResponse>> content =
                customerCustomRepository
                        .findCustomerProfileByFilter(
                                filter,
                                size,
                                offset
                        )
                        .collectMultimap(
                                CustomerProfileDetail::profileId
                        )
                        .map(map ->
                                map.values()
                                        .stream()
                                        .map(rows ->
                                                customerProfileDetailMapper
                                                        .toResponse(
                                                                rows.stream().toList()
                                                        )
                                        )
                                        .toList()
                        );

        Mono<Long> total =
                customerCustomRepository
                        .countCustomerProfiles(filter);

        return Mono.zip(content, total)
                .map(tuple -> {

                    List<CustomerProfileDetailResponse> data =
                            tuple.getT1();

                    long totalElements =
                            tuple.getT2();

                    int totalPages =
                            (int) Math.ceil(
                                    (double) totalElements / size
                            );

                    return new PageResponse<>(
                            data,
                            page,
                            size,
                            totalElements,
                            totalPages
                    );
                });
    }

}
