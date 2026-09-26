package com.fund.server.accountservice.service.impl;

import com.fund.server.accountservice.dto.NormalizedPhone;
import com.fund.server.accountservice.service.PhoneNumberService;
import com.fund.server.accountservice.util.PhoneNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneNumberServiceImpl implements PhoneNumberService {

    private final PhoneNormalizer phoneNormalizer;

    @Override
    public NormalizedPhone normalize(String countryCode, String phoneNumber) {
        return new NormalizedPhone(
                phoneNormalizer.normalizeCountryCode(countryCode),
                phoneNormalizer.normalizePhoneNumber(phoneNumber)
        );
    }
}
