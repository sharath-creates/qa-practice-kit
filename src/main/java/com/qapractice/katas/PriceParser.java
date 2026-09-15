package com.qapractice.katas;

/**
 * A tiny piece of plain Java for Level 1, so you can learn TestNG without a
 * browser in the way. It is correct as written: your job is to prove it.
 */
public final class PriceParser {

    private PriceParser() { }

    /**
     * Parses a displayed price into cents.
     *
     * <p>{@code "$29.99"} becomes {@code 2999}. Currency symbols, thousands
     * separators and surrounding text are ignored.
     *
     * @throws IllegalArgumentException if the input is null, blank, or contains no digits
     */
    public static long toCents(String displayed) {
        if (displayed == null || displayed.trim().isEmpty()) {
            throw new IllegalArgumentException("Price must not be blank");
        }
        String cleaned = displayed.replaceAll("[^0-9.]", "");
        if (cleaned.isEmpty() || cleaned.equals(".")) {
            throw new IllegalArgumentException("No digits in price: " + displayed);
        }
        String[] parts = cleaned.split("\\.", -1);
        long whole = parts[0].isEmpty() ? 0L : Long.parseLong(parts[0]);
        long fraction = 0L;
        if (parts.length > 1 && !parts[1].isEmpty()) {
            String frac = (parts[1] + "00").substring(0, 2);
            fraction = Long.parseLong(frac);
        }
        return whole * 100 + fraction;
    }

    /** 2999 becomes "$29.99". */
    public static String format(long cents) {
        return String.format("$%d.%02d", cents / 100, Math.abs(cents % 100));
    }
}
