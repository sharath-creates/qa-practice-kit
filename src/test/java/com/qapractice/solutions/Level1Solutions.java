package com.qapractice.solutions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.qapractice.katas.Basket;
import com.qapractice.katas.PriceParser;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/** Worked answers for Level 1. Read these only after attempting the tasks. */
public class Level1Solutions {

    private final List<String> lifecycle = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void beforeClass() { lifecycle.add("beforeClass"); }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() { lifecycle.add("beforeMethod"); }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() { lifecycle.add("afterMethod"); }

    @AfterClass(alwaysRun = true)
    public void afterClass() { lifecycle.add("afterClass"); }

    // ------------------------------------------------------------------ 1.1

    @Test(groups = {"fast", "level1"}, description = "1.1 - A first test and a readable assertion")
    public void firstTest() {
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(PriceParser.toCents("$29.99")).as("simple price").isEqualTo(2999L);
        soft.assertThat(PriceParser.toCents("$1,299.00")).as("price with a thousands separator").isEqualTo(129900L);
        soft.assertThat(PriceParser.format(2999L)).as("formatting is the inverse of parsing").isEqualTo("$29.99");
        soft.assertAll();
    }

    // ------------------------------------------------------------------ 1.2

    /**
     * Three true facts, and one tempting assertion that is wrong.
     *
     * <p>The @AfterMethod entry is never visible from inside a test, because
     * teardown has not run yet. @BeforeClass fires exactly once no matter how
     * many tests the class holds. And the entry immediately before this one is
     * always this test's own @BeforeMethod.
     *
     * <p>What you must not assert is that the whole list equals
     * {@code [beforeClass, beforeMethod]}. That holds only if this happens to
     * be the first test method TestNG picks, and method order within a class is
     * not guaranteed. An assertion that depends on running first is a flaky
     * test wearing a disguise.
     */
    @Test(groups = {"fast", "level1"}, description = "1.2 - Prove the fixture execution order")
    public void fixtureOrder() {
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(lifecycle).as("something ran before the test body").isNotEmpty();
        soft.assertThat(lifecycle.get(0)).as("class setup runs first of all").isEqualTo("beforeClass");
        soft.assertThat(java.util.Collections.frequency(lifecycle, "beforeClass"))
                .as("class setup runs exactly once for the whole class").isEqualTo(1);
        soft.assertThat(lifecycle.get(lifecycle.size() - 1))
                .as("the last thing to run before the test body is its own method setup")
                .isEqualTo("beforeMethod");
        soft.assertThat(lifecycle).as("teardown has not run yet for this test")
                .doesNotContain("afterClass");
        soft.assertAll();
    }

    // ------------------------------------------------------------------ 1.3

    @DataProvider(name = "prices")
    public Object[][] prices() {
        return new Object[][] {
                {"$0.50", 50L},
                {"$29.99", 2999L},
                {"$1,299.00", 129900L},
                {"$7", 700L},
                {"Total: $58.29", 5829L},
        };
    }

    @Test(groups = {"fast", "level1"}, dataProvider = "prices",
            description = "1.3 - Parameterise a test with a data provider")
    public void dataProviderOverPrices(String displayed, long expectedCents) {
        assertThat(PriceParser.toCents(displayed))
                .as("parsing '%s'", displayed)
                .isEqualTo(expectedCents);
    }

    // ------------------------------------------------------------------ 1.4

    @Test(groups = {"fast", "level1"},
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*blank.*",
            description = "1.4a - A blank price is rejected")
    public void blankPriceIsRejected() {
        PriceParser.toCents("   ");
    }

    /**
     * expectedExceptions passes when the right type is thrown from anywhere in
     * the method, including a line you did not mean to test. AssertJ's
     * assertThatThrownBy scopes it to one call, which is usually what you want.
     */
    @Test(groups = {"fast", "level1"}, description = "1.4b - A scoped exception assertion")
    public void priceWithNoDigitsIsRejected() {
        assertThatThrownBy(() -> PriceParser.toCents("$"))
                .as("a currency symbol alone is not a price")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No digits");
    }

    @Test(groups = {"fast", "level1"}, timeOut = 2000,
            description = "1.4c - Ten thousand items still totals within the deadline")
    public void basketIsFastEnough() {
        Basket basket = new Basket();
        for (int i = 0; i < 10_000; i++) { basket.add(999L); }
        assertThat(basket.subtotalCents()).as("subtotal of 10,000 items").isEqualTo(9_990_000L);
    }

    @Test(groups = {"fast", "level1"}, invocationCount = 5,
            description = "1.4d - The tax rounding is stable across runs")
    public void taxRoundingIsStable() {
        Basket basket = new Basket().add("$29.99").add("$9.99").add("$15.99");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(basket.subtotalCents()).as("subtotal").isEqualTo(5597L);
        soft.assertThat(basket.taxCents()).as("8% of 5597 rounded half-up").isEqualTo(448L);
        soft.assertThat(basket.totalCents()).as("subtotal plus rounded tax").isEqualTo(6045L);
        soft.assertAll();
    }

    // ------------------------------------------------------------------ 1.5

    @Test(groups = {"fast", "level1"}, description = "1.5 - Read a parameter from the suite XML")
    @Parameters("expectedCurrency")
    public void groupsAndSuiteParameters(String expectedCurrency) {
        String symbol = "USD".equals(expectedCurrency) ? "$" : "?";
        assertThat(PriceParser.format(100L))
                .as("formatted price uses the symbol for %s", expectedCurrency)
                .startsWith(symbol);
    }
}
