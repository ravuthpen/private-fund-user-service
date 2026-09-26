package com.fund.server.accountservice.util;

import com.fund.server.accountservice.exception.InvalidPhoneNumberException;
import org.springframework.stereotype.Component;

@Component
public class PhoneNormalizer {

    public String normalizeCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            throw new InvalidPhoneNumberException("Country code is required");
        }

        String value = countryCode.trim();

        return value.startsWith("+") ? value : "+" + value;
    }

    public String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new InvalidPhoneNumberException("Phone number is required");
        }

        String value = phoneNumber.trim().replaceAll("\\s+", "");

        while (value.startsWith("0")) {
            value = value.substring(1);
        }

        if (!value.matches("^[0-9]{6,15}$")) {
            throw new InvalidPhoneNumberException("Invalid phone number");
        }

        return value;
    }

    public String toUsername(String countryCode, String phoneNumber) {
        return normalizeCountryCode(countryCode) + normalizePhoneNumber(phoneNumber);
    }
}