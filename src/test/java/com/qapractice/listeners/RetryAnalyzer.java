package com.qapractice.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Worked answer to part of task 4.4.
 *
 * <p>Re-runs a failed test a bounded number of times. Retries buy tolerance for
 * a genuinely flaky network against a shared public site. They also hide real
 * intermittent bugs, so the limit is one and every retry is logged: a test that
 * only ever passes on the second attempt is then visible rather than quietly
 * green.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRIES =
            Integer.parseInt(System.getProperty("retry.count", System.getenv().getOrDefault("RETRY_COUNT", "1")));

    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < MAX_RETRIES) {
            attempts++;
            LOG.warn("Retrying {}.{} (attempt {} of {}) after: {}",
                    result.getTestClass().getRealClass().getSimpleName(),
                    result.getMethod().getMethodName(),
                    attempts + 1,
                    MAX_RETRIES + 1,
                    rootCause(result));
            return true;
        }
        return false;
    }

    private static String rootCause(ITestResult result) {
        Throwable t = result.getThrowable();
        if (t == null) { return "no throwable"; }
        String message = t.getMessage() == null ? "" : t.getMessage();
        int newline = message.indexOf('\n');
        return t.getClass().getSimpleName() + ": " + (newline < 0 ? message : message.substring(0, newline));
    }
}
