package com.qapractice.tasks;

import com.qapractice.BaseTest;
import org.testng.annotations.Test;

/**
 * LEVEL 3 - Page objects and a real flow, against https://www.saucedemo.com
 *
 * <p>Level 2 put locators in tests. That is fine for ten tests and unmanageable
 * at a hundred. Here you build the pattern that fixes it.
 *
 * <p>Create your page objects under
 * {@code src/main/java/com/qapractice/pages/sauce/}, extending
 * {@code BasePage}. Three rules, and a reviewer will check all three:
 * <ol>
 *   <li>Locators live only in page objects. A {@code By} in this file is a defect.</li>
 *   <li>Page objects never assert. They expose state; the test judges.</li>
 *   <li>A method returns the page you end up on, so the flow is typed.</li>
 * </ol>
 *
 * <p>Credentials: password {@code secret_sauce} for every user. Valid usernames
 * are {@code standard_user}, {@code locked_out_user}, {@code problem_user},
 * {@code performance_glitch_user}, {@code error_user}, {@code visual_user}.
 *
 * <p>The site marks everything with {@code data-test} attributes. Use them:
 * they exist for automation and nobody will restyle them away.
 */
public class Level3PageObjects extends BaseTest {

    /**
     * TASK 3.1 - Your first page objects.
     *
     * <p>Build {@code SauceLoginPage} and {@code SauceInventoryPage}.
     *
     * <p>{@code SauceLoginPage} needs: {@code open()}, {@code login(user, pass)}
     * returning {@code SauceInventoryPage}, {@code loginExpectingFailure(...)}
     * staying on the login page, and {@code errorMessage()}.
     * The controls are {@code #user-name}, {@code #password} and
     * {@code [data-test="login-button"]}; the error banner is
     * {@code [data-test="error"]}.
     *
     * <p>{@code SauceInventoryPage} needs {@code isLoaded()},
     * {@code productCount()} and {@code productNames()}. Items are
     * {@code [data-test="inventory-item"]}, names
     * {@code [data-test="inventory-item-name"]}, prices
     * {@code [data-test="inventory-item-price"]}.
     *
     * <p>Then, in this test: log in as {@code standard_user}, assert the URL
     * contains {@code /inventory.html} and that six products are listed.
     *
     * <p>Covers: the Page Object Model, fluent returns, separation of concerns.
     */
    @Test(description = "3.1 - Build the login and inventory page objects")
    public void buildPageObjectsAndLogIn() {
        throw Todo.task("3.1");
    }

    /**
     * TASK 3.2 - Data-driven login across every user.
     *
     * <p>{@code src/test/resources/testdata/sauce-users.csv} holds six rows:
     * username, password, whether login should succeed, and the expected error
     * text when it should not.
     *
     * <p>Read that file in a {@code @DataProvider} and drive one test per row.
     * Two of them matter most:
     * <ul>
     *   <li>{@code locked_out_user} authenticates correctly and is then refused
     *       with {@code "Epic sadface: Sorry, this user has been locked out."}</li>
     *   <li>a wrong password gives
     *       {@code "Epic sadface: Username and password do not match any user in this service"}</li>
     * </ul>
     *
     * <p>Reading the CSV is deliberately your problem. Test data belongs outside
     * the code, and doing it once teaches why.
     *
     * <p>Covers: external test data, data providers, negative paths.
     */
    @Test(description = "3.2 - Drive login from a CSV of six users")
    public void dataDrivenLogin() {
        throw Todo.task("3.2");
    }

    /**
     * TASK 3.3 - Sorting.
     *
     * <p>Log in, then use the {@code [data-test="product-sort-container"]}
     * select to choose "Price (low to high)". Assert the displayed prices are in
     * ascending order.
     *
     * <p>Assert the relationship, not the contents. Hardcoding the six expected
     * prices produces a test that passes when sorting is broken and the catalogue
     * happens to be in that order, and that fails when someone adds a product.
     * Read the prices, parse them, and assert the list is sorted.
     *
     * <p>Then repeat for "Name (Z to A)" and convince yourself the same test
     * shape works for both.
     *
     * <p>Covers: Select, parsing displayed values, ordering assertions.
     */
    @Test(description = "3.3 - Sort by price and assert the ordering")
    public void sortProductsByPrice() {
        throw Todo.task("3.3");
    }

    /**
     * TASK 3.4 - Cart state.
     *
     * <p>Add two named products to the cart. The add buttons are keyed by a slug,
     * for example {@code [data-test="add-to-cart-sauce-labs-backpack"]}, and
     * turn into {@code [data-test="remove-sauce-labs-backpack"]} once added.
     *
     * <p>Assert:
     * <ul>
     *   <li>the badge {@code [data-test="shopping-cart-badge"]} reads 2</li>
     *   <li>the cart page lists exactly those two products, by name and price
     *       captured from the inventory page before navigating</li>
     *   <li>removing one drops the badge to 1 and leaves the other untouched</li>
     *   <li>removing the last one makes the badge disappear entirely, which is
     *       not the same as reading zero</li>
     * </ul>
     *
     * <p>That last assertion needs a non-throwing absence check, not a
     * {@code getText()} in a try/catch.
     *
     * <p>Covers: dynamic locators, state transitions, asserting absence.
     */
    @Test(description = "3.4 - Add, verify and remove cart items")
    public void cartAddRemoveAndBadge() {
        throw Todo.task("3.4");
    }

    /**
     * TASK 3.5 - Checkout, end to end, including the arithmetic.
     *
     * <p>Add two products, go to the cart, check out. The form fields are
     * {@code [data-test="firstName"]}, {@code [data-test="lastName"]} and
     * {@code [data-test="postalCode"]}; then {@code [data-test="continue"]} and
     * {@code [data-test="finish"]}.
     *
     * <p>On the summary page assert:
     * <ul>
     *   <li>{@code [data-test="subtotal-label"]} equals the sum of the two prices
     *       you captured</li>
     *   <li>{@code [data-test="tax-label"]} equals 8% of that subtotal, rounded
     *       to the cent</li>
     *   <li>{@code [data-test="total-label"]} equals subtotal plus that rounded
     *       tax, which is not the same as 108% of the subtotal</li>
     *   <li>after finishing, {@code [data-test="complete-header"]} reads
     *       {@code "Thank you for your order!"}</li>
     * </ul>
     *
     * <p>{@code com.qapractice.katas.Basket} already implements exactly this
     * rounding rule. Use it as the oracle rather than recomputing inline, and
     * notice that Level 1 was quietly preparing you for this.
     *
     * <p>Do the money in integer cents. Asserting {@code 29.99 + 9.99 == 39.98}
     * in doubles is how you get a test that fails one time in a hundred.
     *
     * <p>Covers: multi-page flows, computed assertions, floating-point traps.
     */
    @Test(description = "3.5 - Full checkout with verified totals")
    public void fullCheckoutWithTaxMaths() {
        throw Todo.task("3.5");
    }
}
