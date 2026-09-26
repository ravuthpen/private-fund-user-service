package com.fund.server.accountservice.controller;

import com.fund.server.accountservice.dto.LoginRequest;
import com.fund.server.accountservice.dto.LoginResponse;
import com.fund.server.accountservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        return service.login(request);
    }


}
