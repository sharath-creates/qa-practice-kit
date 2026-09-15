package com.qapractice.driver;

import com.qapractice.exceptions.FrameworkException;
import org.openqa.selenium.WebDriver;

/**
 * One WebDriver per thread.
 *
 * <p>A static shared driver is the single most common cause of a flaky parallel
 * suite. Binding to the thread is what lets TestNG run methods in parallel.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() { }

    public static void startDriver() {
        if (DRIVER.get() != null) { quitDriver(); }
        DRIVER.set(DriverFactory.create());
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new FrameworkException("No WebDriver bound to thread '"
                    + Thread.currentThread().getName() + "'. Did the test extend BaseTest?");
        }
        return driver;
    }

    public static boolean hasDriver() { return DRIVER.get() != null; }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();   // unbind even if quit() throws, or the thread leaks a dead session
            }
        }
    }
}
