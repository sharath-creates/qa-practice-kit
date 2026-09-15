package com.qapractice.solutions.pages;

/** One inventory tile, with its price already parsed into cents. */
public final class SauceProduct {

    private final String name;
    private final long priceCents;

    public SauceProduct(String name, long priceCents) {
        this.name = name;
        this.priceCents = priceCents;
    }

    public String name() { return name; }

    public long priceCents() { return priceCents; }

    /** "Sauce Labs Backpack" becomes "sauce-labs-backpack", the slug used in data-test attributes. */
    public String slug() {
        return name.toLowerCase().replace(" ", "-");
    }

    @Override
    public String toString() { return name + " (" + priceCents + "c)"; }
}
