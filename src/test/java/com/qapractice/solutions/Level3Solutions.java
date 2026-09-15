package com.qapractice.solutions;

import static org.assertj.core.api.Assertions.assertThat;

import com.qapractice.BaseTest;
import com.qapractice.exceptions.FrameworkException;
import com.qapractice.katas.Basket;
import com.qapractice.solutions.pages.SauceCartPage;
import com.qapractice.solutions.pages.SauceCheckoutPage;
import com.qapractice.solutions.pages.SauceInventoryPage;
import com.qapractice.solutions.pages.SauceLoginPage;
import com.qapractice.solutions.pages.SauceProduct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Worked answers for Level 3.
 *
 * <p>Compare the length of these tests with Level 2. The work did not vanish;
 * it moved into the page objects, where it is written once instead of once per
 * test. Each test now reads as a description of the behaviour.
 */
public class Level3Solutions extends BaseTest {

    private static final String PASSWORD = "secret_sauce";

    // ------------------------------------------------------------------ 3.1

    @Test(groups = {"smoke", "sauce"}, description = "3.1 - Log in as the standard user")
    public void buildPageObjectsAndLogIn() {
        SauceInventoryPage inventory = new SauceLoginPage().open().login("standard_user", PASSWORD);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(inventory.isLoaded()).as("inventory page loaded").isTrue();
        soft.assertThat(inventory.productCount()).as("catalogue size").isEqualTo(6);
        soft.assertThat(inventory.productNames()).as("every tile has a name")
                .allMatch(name -> !name.isEmpty());
        soft.assertAll();
    }

    // ------------------------------------------------------------------ 3.2

    /** Reads the CSV from the classpath, so it works from an IDE and from CI alike. */
    @DataProvider(name = "sauceUsers")
    public Object[][] sauceUsers() {
        List<Object[]> rows = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = loader.getResourceAsStream("testdata/sauce-users.csv")) {
            if (in == null) { throw new FrameworkException("Missing testdata/sauce-users.csv"); }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = reader.readLine();   // discard the header row
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) { continue; }
                // Only the first three commas separate fields; the error text
                // itself contains commas, so split with a limit.
                String[] parts = line.split(",", 4);
                rows.add(new Object[] {
                        parts[0].trim(),
                        parts[1].trim(),
                        Boolean.parseBoolean(parts[2].trim()),
                        parts.length > 3 ? parts[3].trim() : ""
                });
            }
        } catch (IOException e) {
            throw new FrameworkException("Could not read testdata/sauce-users.csv", e);
        }
        return rows.toArray(new Object[0][]);
    }

    @Test(groups = "sauce", dataProvider = "sauceUsers",
            description = "3.2 - Drive login from a CSV of six users")
    public void dataDrivenLogin(String username, String password, boolean shouldReachInventory, String expectedError) {
        SauceLoginPage login = new SauceLoginPage().open();

        if (shouldReachInventory) {
            SauceInventoryPage inventory = login.login(username, password);
            assertThat(inventory.isLoaded())
                    .as("'%s' should reach the inventory", username).isTrue();
        } else {
            login.loginExpectingFailure(username, password);
            SoftAssertions soft = new SoftAssertions();
            soft.assertThat(login.isErrorVisible()).as("'%s' is refused", username).isTrue();
            soft.assertThat(login.errorMessage()).as("error wording for '%s'", username).isEqualTo(expectedError);
            soft.assertAll();
        }
    }

    // ------------------------------------------------------------------ 3.3

    @Test(groups = "sauce", description = "3.3 - Sort by price and assert the ordering")
    public void sortProductsByPrice() {
        SauceInventoryPage inventory = new SauceLoginPage().open().login("standard_user", PASSWORD);
        assertThat(inventory.isLoaded()).as("inventory page loaded").isTrue();

        inventory.sortBy("Price (low to high)");
        // Assert the relationship, not the six specific prices. Hardcoding them
        // gives a test that passes when sorting is broken but the catalogue
        // happens to be in that order, and fails when a product is added.
        assertThat(inventory.displayedPricesInCents())
                .as("prices ascending").isSortedAccordingTo(Comparator.naturalOrder());

        inventory.sortBy("Name (Z to A)");
        assertThat(inventory.productNames())
                .as("names descending").isSortedAccordingTo(Comparator.reverseOrder());
    }

    // ------------------------------------------------------------------ 3.4

    @Test(groups = "sauce", description = "3.4 - Add, verify and remove cart items")
    public void cartAddRemoveAndBadge() {
        SauceInventoryPage inventory = new SauceLoginPage().open().login("standard_user", PASSWORD);

        // Capture from the catalogue before navigating, so the cart is compared
        // against the source of truth rather than against itself.
        List<SauceProduct> chosen = inventory.products().subList(0, 2);
        chosen.forEach(inventory::addToCart);

        assertThat(inventory.cartBadgeCount()).as("badge after adding two").isEqualTo(2);

        SauceCartPage cart = inventory.openCart();
        SoftAssertions inCart = new SoftAssertions();
        inCart.assertThat(cart.isLoaded()).as("cart page loaded").isTrue();
        inCart.assertThat(cart.itemCount()).as("two rows in the cart").isEqualTo(2);
        inCart.assertThat(cart.itemNames()).as("the two products we chose")
                .containsExactlyInAnyOrderElementsOf(
                        chosen.stream().map(SauceProduct::name).collect(Collectors.toList()));
        inCart.assertAll();

        SauceInventoryPage back = new SauceLoginPage().open().login("standard_user", PASSWORD);
        back.removeFromCart(chosen.get(0));

        SoftAssertions afterRemoval = new SoftAssertions();
        afterRemoval.assertThat(back.cartBadgeCount()).as("badge after removing one").isEqualTo(1);
        afterRemoval.assertAll();

        back.removeFromCart(chosen.get(1));
        // An empty cart removes the badge entirely, which is not the same as it
        // reading zero, so this needs a non-throwing absence check.
        assertThat(back.isCartBadgeAbsent()).as("badge disappears when the cart empties").isTrue();
    }

    // ------------------------------------------------------------------ 3.5

    @Test(groups = {"smoke", "sauce"}, description = "3.5 - Full checkout with verified totals")
    public void fullCheckoutWithTaxMaths() {
        SauceInventoryPage inventory = new SauceLoginPage().open().login("standard_user", PASSWORD);

        List<SauceProduct> chosen = inventory.products().subList(0, 2);
        chosen.forEach(inventory::addToCart);

        SauceCartPage cart = inventory.openCart();
        assertThat(cart.isLoaded()).as("cart page loaded").isTrue();

        SauceCheckoutPage checkout = cart.checkout();
        assertThat(checkout.isInformationStepLoaded()).as("checkout step one loaded").isTrue();

        checkout.fillInformation("Sharath", "QA", "560001");
        assertThat(checkout.isSummaryLoaded()).as("checkout summary loaded").isTrue();

        // Basket implements the site's rounding rule, so it acts as the oracle
        // rather than the maths being recomputed inline in the assertion.
        Basket expected = new Basket();
        chosen.forEach(p -> expected.add(p.priceCents()));

        SoftAssertions totals = new SoftAssertions();
        totals.assertThat(checkout.subtotalCents()).as("item total").isEqualTo(expected.subtotalCents());
        totals.assertThat(checkout.taxCents()).as("8% tax, rounded to the cent").isEqualTo(expected.taxCents());
        totals.assertThat(checkout.totalCents())
                .as("subtotal plus rounded tax, not 108% of subtotal").isEqualTo(expected.totalCents());
        totals.assertAll();

        checkout.finish();
        SoftAssertions done = new SoftAssertions();
        done.assertThat(checkout.isOrderComplete()).as("order confirmation shown").isTrue();
        done.assertThat(checkout.completeHeader()).as("confirmation wording")
                .isEqualTo("Thank you for your order!");
        done.assertAll();
    }
}
