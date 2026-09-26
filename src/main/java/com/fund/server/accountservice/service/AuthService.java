package com.fund.server.accountservice.service;

import com.fund.server.accountservice.dto.LoginRequest;
import com.fund.server.accountservice.dto.LoginResponse;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<LoginResponse> login(LoginRequest request);

}
