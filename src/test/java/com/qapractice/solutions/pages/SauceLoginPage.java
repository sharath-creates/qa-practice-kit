package com.qapractice.solutions.pages;

import com.qapractice.config.Config;
import com.qapractice.pages.BasePage;
import com.qapractice.support.Text;
import java.time.Duration;
import org.openqa.selenium.By;

/** Worked answer for task 3.1. Selectors read from the site's own source. */
public class SauceLoginPage extends BasePage {

    public static final String PATH = "/";

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By SUBMIT   = By.cssSelector("[data-test='login-button']");
    private static final By ERROR    = By.cssSelector("[data-test='error']");

    public SauceLoginPage open() {
        openUrl(Config.url(Config.sauceUrl(), PATH));
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(USERNAME, Duration.ofSeconds(15));
    }

    /** The happy path: the caller expects to land on the inventory. */
    public SauceInventoryPage login(String username, String password) {
        submit(username, password);
        return new SauceInventoryPage();
    }

    /** The caller expects to stay here with an error banner. */
    public SauceLoginPage loginExpectingFailure(String username, String password) {
        submit(username, password);
        return this;
    }

    public boolean isErrorVisible() {
        return isDisplayed(ERROR, Duration.ofSeconds(10));
    }

    public String errorMessage() {
        return Text.squash(textOf(ERROR));
    }

    private void submit(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(SUBMIT);
    }
}
