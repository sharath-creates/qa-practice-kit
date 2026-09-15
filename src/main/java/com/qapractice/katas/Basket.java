package com.qapractice.katas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mirrors the arithmetic the SauceDemo checkout performs, so Level 1 teaches
 * you the maths you will assert against a real page in Level 3.
 *
 * <p>Tax is 8% of the subtotal, rounded half-up to the cent. The grand total is
 * the subtotal plus that rounded tax, which is not the same as 108% of the
 * subtotal once rounding is involved.
 */
public final class Basket {

    private static final double TAX_RATE = 0.08;

    private final List<Long> itemsInCents = new ArrayList<>();

    public Basket add(long priceInCents) {
        if (priceInCents < 0) { throw new IllegalArgumentException("Price cannot be negative"); }
        itemsInCents.add(priceInCents);
        return this;
    }

    public Basket add(String displayedPrice) { return add(PriceParser.toCents(displayedPrice)); }

    public int size() { return itemsInCents.size(); }

    public boolean isEmpty() { return itemsInCents.isEmpty(); }

    public List<Long> items() { return Collections.unmodifiableList(itemsInCents); }

    public long subtotalCents() { return itemsInCents.stream().mapToLong(Long::longValue).sum(); }

    public long taxCents() { return Math.round(subtotalCents() * TAX_RATE); }

    public long totalCents() { return subtotalCents() + taxCents(); }
}
