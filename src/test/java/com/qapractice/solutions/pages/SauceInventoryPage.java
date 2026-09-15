package com.qapractice.solutions.pages;

import com.qapractice.pages.BasePage;
import com.qapractice.support.Text;
import com.qapractice.katas.PriceParser;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/** Worked answer for tasks 3.1, 3.3 and 3.4. */
public class SauceInventoryPage extends BasePage {

    private static final By CONTAINER  = By.cssSelector("[data-test='inventory-container']");
    private static final By ITEM       = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME  = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By SORT       = By.cssSelector("[data-test='product-sort-container']");
    private static final By CART_LINK  = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");

    public boolean isLoaded() {
        return isDisplayed(CONTAINER, Duration.ofSeconds(15)) && urlContains("/inventory.html");
    }

    public int productCount() { return countOf(ITEM); }

    public List<String> productNames() {
        return allVisible(ITEM_NAME).stream().map(e -> Text.squash(e.getText())).collect(Collectors.toList());
    }

    /** Names and prices together, captured before navigating away. */
    public List<SauceProduct> products() {
        List<SauceProduct> products = new ArrayList<>();
        for (WebElement item : allVisible(ITEM)) {
            String name = Text.squash(item.findElement(ITEM_NAME).getText());
            long cents = PriceParser.toCents(item.findElement(ITEM_PRICE).getText());
            products.add(new SauceProduct(name, cents));
        }
        return products;
    }

    public List<Long> displayedPricesInCents() {
        return allVisible(ITEM_PRICE).stream()
                .map(e -> PriceParser.toCents(e.getText()))
                .collect(Collectors.toList());
    }

    public SauceInventoryPage sortBy(String visibleText) {
        selectByVisibleText(SORT, visibleText);
        return this;
    }

    public SauceInventoryPage addToCart(SauceProduct product) {
        click(By.cssSelector("[data-test='add-to-cart-" + product.slug() + "']"));
        return this;
    }

    public SauceInventoryPage removeFromCart(SauceProduct product) {
        click(By.cssSelector("[data-test='remove-" + product.slug() + "']"));
        return this;
    }

    /** Zero when the badge is absent, which is how the site renders an empty cart. */
    public int cartBadgeCount() {
        if (!isDisplayed(CART_BADGE, Duration.ofSeconds(3))) { return 0; }
        return Integer.parseInt(Text.squash(textOf(CART_BADGE)));
    }

    public boolean isCartBadgeAbsent() {
        return isAbsent(CART_BADGE, Duration.ofSeconds(5));
    }

    public SauceCartPage openCart() {
        click(CART_LINK);
        return new SauceCartPage();
    }
}
