package com.fund.server.accountservice.util;

import java.time.LocalDate;
import java.time.Period;

public class AgeCalculator {

    public static int calculate(LocalDate dateOfBirth) {

        if (dateOfBirth == null) {
            return 0;
        }

        return Period
                .between(dateOfBirth, LocalDate.now())
                .getYears();
    }
}