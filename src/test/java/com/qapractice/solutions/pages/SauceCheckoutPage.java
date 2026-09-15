package com.qapractice.solutions.pages;

import com.qapractice.katas.PriceParser;
import com.qapractice.pages.BasePage;
import com.qapractice.support.Text;
import java.time.Duration;
import org.openqa.selenium.By;

/**
 * Worked answer for task 3.5.
 *
 * <p>Covers all three checkout screens. They are one flow with no branching, so
 * splitting them into three classes would add ceremony without adding clarity.
 */
public class SauceCheckoutPage extends BasePage {

    private static final By FIRST_NAME  = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME   = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE    = By.cssSelector("[data-test='continue']");
    private static final By FINISH      = By.cssSelector("[data-test='finish']");

    private static final By SUMMARY   = By.cssSelector("[data-test='checkout-summary-container']");
    private static final By SUBTOTAL  = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX       = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL     = By.cssSelector("[data-test='total-label']");
    private static final By COMPLETE  = By.cssSelector("[data-test='complete-header']");

    public boolean isInformationStepLoaded() {
        return isDisplayed(FIRST_NAME, Duration.ofSeconds(15));
    }

    public SauceCheckoutPage fillInformation(String firstName, String lastName, String postalCode) {
        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POSTAL_CODE, postalCode);
        click(CONTINUE);
        return this;
    }

    public boolean isSummaryLoaded() {
        return isDisplayed(SUMMARY, Duration.ofSeconds(15));
    }

    /** "Item total: $58.29" becomes 5829. */
    public long subtotalCents() { return PriceParser.toCents(textOf(SUBTOTAL)); }

    public long taxCents() { return PriceParser.toCents(textOf(TAX)); }

    public long totalCents() { return PriceParser.toCents(textOf(TOTAL)); }

    public SauceCheckoutPage finish() {
        click(FINISH);
        return this;
    }

    public boolean isOrderComplete() {
        return isDisplayed(COMPLETE, Duration.ofSeconds(15));
    }

    public String completeHeader() { return Text.squash(textOf(COMPLETE)); }
}
