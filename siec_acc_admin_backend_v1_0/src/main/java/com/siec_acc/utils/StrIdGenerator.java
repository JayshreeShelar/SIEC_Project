package com.siec_acc.utils;

public class StrIdGenerator {

    public static String generate(String prefix, Long primeId) {
        return prefix + "-" + (1000 + primeId);
    }
}