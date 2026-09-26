package com.fund.server.accountservice.service;


import com.fund.server.accountservice.dto.NormalizedPhone;

public interface PhoneNumberService {

    NormalizedPhone normalize(String countryCode, String phoneNumber);
}