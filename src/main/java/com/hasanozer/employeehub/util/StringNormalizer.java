package com.hasanozer.employeehub.util;

import java.util.Locale;

public final class StringNormalizer {

    private StringNormalizer() {
    }

    public static String required(String value) {
        return value.trim();
    }

    public static String nullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public static String email(String value) {
        return required(value).toLowerCase(Locale.ROOT);
    }
}
