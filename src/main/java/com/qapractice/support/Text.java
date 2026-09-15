package com.qapractice.support;

import java.util.Locale;

/** Normalisation helpers so assertions survive stray whitespace and CSS casing. */
public final class Text {

    private Text() { }

    /** Collapses runs of whitespace and trims. */
    public static String squash(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    /** Whitespace-squashed and upper-cased, for headings the stylesheet capitalises. */
    public static String normalise(String value) {
        return squash(value).toUpperCase(Locale.ROOT);
    }

    /** Parses "$29.99" or "Total: $58.29" into 29.99 / 58.29. */
    public static double money(String value) {
        String digits = squash(value).replaceAll("[^0-9.]", "");
        return digits.isEmpty() ? 0d : Double.parseDouble(digits);
    }
}
