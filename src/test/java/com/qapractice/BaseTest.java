package com.qapractice;

import com.qapractice.config.Config;
import com.qapractice.driver.DriverManager;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Lifecycle for every browser-based test.
 *
 * <p>A browser per <em>method</em>, not per class. A session shared between
 * tests carries cookies and cart state from one into the next, and the suite
 * then passes only in one particular order.
 *
 * <p>Level 1 does not extend this on purpose: TestNG does not need a browser,
 * and starting one for a pure-Java test is a second wasted per test.
 */
public abstract class BaseTest {

    protected static final Logger LOG = LogManager.getLogger(BaseTest.class);

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        LOG.info("Starting {} [{}, headless={}]", method.getName(), Config.browser(), Config.headless());
        DriverManager.startDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
