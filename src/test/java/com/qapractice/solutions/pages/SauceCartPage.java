package com.qapractice.solutions.pages;

import com.qapractice.katas.PriceParser;
import com.qapractice.pages.BasePage;
import com.qapractice.support.Text;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/** Worked answer for tasks 3.4 and 3.5. */
public class SauceCartPage extends BasePage {

    private static final By CONTAINER  = By.cssSelector("[data-test='cart-contents-container']");
    private static final By ITEM       = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME  = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By CHECKOUT   = By.cssSelector("[data-test='checkout']");

    public boolean isLoaded() {
        return isDisplayed(CONTAINER, Duration.ofSeconds(15)) && urlContains("/cart.html");
    }

    public int itemCount() { return countOf(ITEM); }

    public List<String> itemNames() {
        return findAll(ITEM_NAME).stream().map(e -> Text.squash(e.getText())).collect(Collectors.toList());
    }

    public List<SauceProduct> items() {
        List<SauceProduct> products = new ArrayList<>();
        for (WebElement item : findAll(ITEM)) {
            products.add(new SauceProduct(
                    Text.squash(item.findElement(ITEM_NAME).getText()),
                    PriceParser.toCents(item.findElement(ITEM_PRICE).getText())));
        }
        return products;
    }

    public SauceCheckoutPage checkout() {
        click(CHECKOUT);
        return new SauceCheckoutPage();
    }
}
