package com.qapractice.config;

import com.qapractice.exceptions.FrameworkException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Configuration, resolved once per JVM and immutable afterwards.
 *
 * <p>Resolution order, highest first:
 * <ol>
 *   <li>JVM system property, e.g. {@code -Dbrowser=firefox}</li>
 *   <li>Environment variable, upper-snake-cased, e.g. {@code BROWSER}</li>
 *   <li>{@code config/&lt;env&gt;.properties} when {@code -Denv=ci} is set</li>
 *   <li>{@code config/config.properties}</li>
 * </ol>
 *
 * <p>Task 4.1 asks you to extend this. Read it before you do.
 */
public final class Config {

    private static final Properties DEFAULTS = load("config/config.properties", true);
    private static final Properties ENV_FILE =
            load("config/" + System.getProperty("env", System.getenv().getOrDefault("ENV", "")) + ".properties", false);

    private Config() { }

    public static String internetUrl()  { return trimSlash(require("internet.url")); }
    public static String sauceUrl()     { return trimSlash(require("sauce.url")); }

    public static String browser()      { return get("browser", "chrome").toLowerCase(); }
    public static boolean headless()    { return Boolean.parseBoolean(get("headless", "true")); }
    public static String windowSize()   { return get("window.size", "1920,1080"); }
    public static String gridUrl()      { return get("grid.url", ""); }
    public static boolean isRemote()    { return !gridUrl().isEmpty(); }

    public static Duration explicitWait()    { return Duration.ofSeconds(getLong("timeout.explicit.seconds", 20)); }
    public static Duration pageLoadTimeout() { return Duration.ofSeconds(getLong("timeout.pageload.seconds", 45)); }
    public static Duration polling()         { return Duration.ofMillis(getLong("timeout.polling.millis", 250)); }

    public static String downloadDirectory() { return get("download.directory", "target/downloads"); }

    /** Absolute URL on the-internet, e.g. {@code url(internetUrl(), "/login")}. */
    public static String url(String base, String path) {
        return path.startsWith("/") ? base + path : base + "/" + path;
    }

    public static String get(String key, String fallback) {
        String v = resolve(key);
        return v == null || v.isEmpty() ? fallback : v;
    }

    public static String require(String key) {
        String v = resolve(key);
        if (v == null || v.trim().isEmpty()) {
            throw new FrameworkException("Required configuration key '" + key + "' is not set");
        }
        return v;
    }

    public static long getLong(String key, long fallback) {
        String v = resolve(key);
        if (v == null || v.trim().isEmpty()) { return fallback; }
        try {
            return Long.parseLong(v.trim());
        } catch (NumberFormatException e) {
            throw new FrameworkException("Configuration key '" + key + "' is not a number: " + v, e);
        }
    }

    private static String resolve(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isEmpty()) { return sys; }
        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        if (env != null && !env.isEmpty()) { return env; }
        String fromEnvFile = ENV_FILE.getProperty(key);
        if (fromEnvFile != null && !fromEnvFile.isEmpty()) { return fromEnvFile; }
        return DEFAULTS.getProperty(key);
    }

    private static Properties load(String resource, boolean mandatory) {
        Properties p = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = loader.getResourceAsStream(resource)) {
            if (in == null) {
                if (mandatory) { throw new FrameworkException("Missing classpath resource: " + resource); }
                return p;
            }
            p.load(in);
        } catch (IOException e) {
            throw new FrameworkException("Could not read " + resource, e);
        }
        return p;
    }

    private static String trimSlash(String v) {
        return v.endsWith("/") ? v.substring(0, v.length() - 1) : v;
    }
}
