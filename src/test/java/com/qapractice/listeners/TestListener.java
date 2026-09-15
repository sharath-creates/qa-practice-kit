package com.qapractice.listeners;

import com.qapractice.driver.DriverManager;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Worked answer to task 4.3.
 *
 * <p>Turns a failure into something diagnosable without re-running it: a
 * screenshot, the URL at the moment of failure, and the page source.
 *
 * <p>Every hook is defensive. A listener that throws replaces the real failure
 * with its own, which is the fastest way to make a suite undebuggable. The
 * browser session may also already be gone by the time this runs.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        LOG.info("=== Suite '{}' starting with {} thread(s) ===",
                context.getName(), context.getCurrentXmlTest().getThreadCount());
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info(">>> {}.{}", simpleName(result), result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("<<< PASSED {}.{} in {} ms", simpleName(result), result.getMethod().getMethodName(), duration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("<<< FAILED {}.{} in {} ms",
                simpleName(result), result.getMethod().getMethodName(), duration(result), result.getThrowable());
        attachDiagnostics();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("<<< SKIPPED {}.{}", simpleName(result), result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOG.info("=== Suite '{}' finished: {} passed, {} failed, {} skipped ===",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    private void attachDiagnostics() {
        if (!DriverManager.hasDriver()) { return; }
        try {
            byte[] png = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
            if (png.length > 0) {
                Allure.addAttachment("screenshot-on-failure", "image/png", new ByteArrayInputStream(png), ".png");
            }
        } catch (RuntimeException e) {
            LOG.warn("Could not capture screenshot: {}", e.getMessage());
        }
        try {
            Allure.addAttachment("url-at-failure", DriverManager.getDriver().getCurrentUrl());
            String source = DriverManager.getDriver().getPageSource();
            if (source != null && !source.isEmpty()) {
                Allure.addAttachment("page-source", "text/html",
                        new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), ".html");
            }
        } catch (RuntimeException e) {
            LOG.warn("Could not capture page state: {}", e.getMessage());
        }
    }

    private static String simpleName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName();
    }

    private static long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
