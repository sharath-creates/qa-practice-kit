package com.qapractice.pages;

import com.qapractice.config.Config;
import com.qapractice.driver.DriverManager;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Shared interaction layer. Given to you complete; read it before Level 2.
 *
 * <p>Three rules are enforced here rather than left to each page:
 * no {@code Thread.sleep}, stale elements are retried once rather than
 * rethrown, and elements are scrolled into view before interaction.
 *
 * <p>No PageFactory or {@code @FindBy}. Plain {@link By} constants stay
 * greppable and a failure names the selector that missed.
 */
public abstract class BasePage {

    protected static final Logger LOG = LogManager.getLogger(BasePage.class);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Config.explicitWait(), Config.polling());
    }

    // ------------------------------------------------------------- navigation

    protected void openUrl(String absoluteUrl) {
        LOG.info("Navigating to {}", absoluteUrl);
        driver.get(absoluteUrl);
    }

    public String currentUrl() { return driver.getCurrentUrl(); }
    public String pageTitle()  { return driver.getTitle(); }

    public boolean urlContains(String fragment) {
        try {
            return wait.until(ExpectedConditions.urlContains(fragment));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---------------------------------------------------------------- finding

    protected WebElement visible(By locator) {
        return wait.withMessage("Element not visible: " + locator)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement present(By locator) {
        return wait.withMessage("Element not present in DOM: " + locator)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.withMessage("Element never became clickable: " + locator)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected List<WebElement> allVisible(By locator) {
        return wait.withMessage("No visible elements matched: " + locator)
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected List<WebElement> findAll(By locator) { return driver.findElements(locator); }

    protected int countOf(By locator) { return driver.findElements(locator).size(); }

    // ---------------------------------------------------------- state queries

    /** Non-throwing, with its own deadline. A negative check must not cost the full timeout. */
    public boolean isDisplayed(By locator, Duration timeout) {
        try {
            shortWait(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public boolean isDisplayed(By locator) { return isDisplayed(locator, Config.explicitWait()); }

    public boolean isAbsent(By locator, Duration timeout) {
        try {
            return shortWait(timeout).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected String textOf(By locator) {
        return retrying(() -> visible(locator).getText().trim());
    }

    /** The HTML attribute as authored. */
    protected String domAttribute(By locator, String attribute) {
        return retrying(() -> present(locator).getDomAttribute(attribute));
    }

    /** The live JS property. This is what a form field currently contains. */
    protected String domProperty(By locator, String property) {
        return retrying(() -> present(locator).getDomProperty(property));
    }

    // ----------------------------------------------------------- interactions

    protected void click(By locator) {
        retrying(() -> {
            WebElement element = clickable(locator);
            scrollIntoView(element);
            try {
                element.click();
            } catch (ElementNotInteractableException e) {
                // Covers ElementClickInterceptedException too, which is a subclass.
                LOG.debug("Native click blocked on {} - falling back to JS click", locator);
                javascript().executeScript("arguments[0].click();", element);
            }
            return null;
        });
    }

    protected void click(WebElement element) {
        retrying(() -> {
            scrollIntoView(element);
            try {
                element.click();
            } catch (ElementNotInteractableException e) {
                javascript().executeScript("arguments[0].click();", element);
            }
            return null;
        });
    }

    protected void type(By locator, String text) {
        retrying(() -> {
            WebElement element = visible(locator);
            scrollIntoView(element);
            element.clear();
            element.sendKeys(text);
            return null;
        });
    }

    protected void selectByVisibleText(By locator, String text) {
        retrying(() -> { new Select(visible(locator)).selectByVisibleText(text); return null; });
    }

    protected void selectByValue(By locator, String value) {
        retrying(() -> { new Select(visible(locator)).selectByValue(value); return null; });
    }

    protected String selectedOptionText(By locator) {
        return retrying(() -> new Select(visible(locator)).getFirstSelectedOption().getText().trim());
    }

    protected void hover(WebElement element) {
        scrollIntoView(element);
        actions().moveToElement(element).perform();
    }

    protected void hover(By locator) { hover(visible(locator)); }

    /** sendKeys on the file input. Clicking it opens an OS dialog WebDriver cannot drive. */
    protected void uploadFile(By locator, String absolutePath) {
        present(locator).sendKeys(absolutePath);
    }

    protected Actions actions() { return new Actions(driver); }

    // ------------------------------------------------------------ frames etc.

    protected void switchToFrame(By locator) { driver.switchTo().frame(present(locator)); }
    protected void switchToFrame(String nameOrId) { driver.switchTo().frame(nameOrId); }
    protected void switchToDefaultContent() { driver.switchTo().defaultContent(); }
    protected void switchToParentFrame() { driver.switchTo().parentFrame(); }

    public String currentWindow() { return driver.getWindowHandle(); }
    public Set<String> allWindows() { return driver.getWindowHandles(); }
    protected void switchToWindow(String handle) { driver.switchTo().window(handle); }

    /** Waits until the browser has more than {@code before} windows, then returns the new handle. */
    protected String waitForNewWindow(Set<String> before) {
        wait.withMessage("No new window or tab opened").until(d -> d.getWindowHandles().size() > before.size());
        return driver.getWindowHandles().stream()
                .filter(h -> !before.contains(h))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("New window appeared and then vanished"));
    }

    protected Alert waitForAlert() {
        return wait.withMessage("No alert appeared").until(ExpectedConditions.alertIsPresent());
    }

    // -------------------------------------------------------------- scrolling

    protected void scrollIntoView(WebElement element) {
        javascript().executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    protected void scrollToBottom() {
        javascript().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    // --------------------------------------------------------------- plumbing

    protected JavascriptExecutor javascript() { return (JavascriptExecutor) driver; }

    protected Wait<WebDriver> shortWait(Duration timeout) {
        return new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(Config.polling())
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Runs an action, retrying once on a stale element.
     *
     * <p>One retry absorbs the AJAX re-render race without hiding a genuine
     * defect, which an unbounded retry would.
     */
    protected <T> T retrying(java.util.function.Supplier<T> action) {
        try {
            return action.get();
        } catch (StaleElementReferenceException e) {
            LOG.debug("Element went stale mid-interaction - retrying once");
            return action.get();
        }
    }
}
