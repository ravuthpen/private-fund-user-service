package com.fund.server.accountservice.util;

public final class LogMasker {

    private LogMasker() {}

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 6) {
            return "***";
        }
        return "***" + phone.substring(phone.length() - 4);
    }
}
