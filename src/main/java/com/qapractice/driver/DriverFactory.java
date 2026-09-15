package com.qapractice.driver;

import com.qapractice.config.Config;
import com.qapractice.exceptions.FrameworkException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Builds a configured WebDriver.
 *
 * <p>Driver binaries are resolved by Selenium Manager, built into Selenium 4.6+.
 * Nothing is downloaded by the build and no driver path is checked in.
 */
public final class DriverFactory {

    private DriverFactory() { }

    public static WebDriver create() {
        BrowserType browser = BrowserType.from(Config.browser());
        MutableCapabilities options = optionsFor(browser);

        WebDriver driver = Config.isRemote() ? remote(Config.gridUrl(), options) : local(browser, options);

        driver.manage().timeouts().pageLoadTimeout(Config.pageLoadTimeout());
        // Deliberately zero. Mixing implicit and explicit waits makes every
        // timeout unpredictable. Every wait in this kit is explicit.
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        return driver;
    }

    private static WebDriver local(BrowserType browser, MutableCapabilities options) {
        switch (browser) {
            case CHROME:  return new ChromeDriver((ChromeOptions) options);
            case FIREFOX: return new FirefoxDriver((FirefoxOptions) options);
            case EDGE:    return new EdgeDriver((EdgeOptions) options);
            default: throw new FrameworkException("No local driver mapping for " + browser);
        }
    }

    private static WebDriver remote(String gridUrl, MutableCapabilities options) {
        try {
            return new RemoteWebDriver(new URL(gridUrl), options);
        } catch (MalformedURLException e) {
            throw new FrameworkException("grid.url is not a valid URL: " + gridUrl, e);
        }
    }

    private static MutableCapabilities optionsFor(BrowserType browser) {
        switch (browser) {
            case CHROME:  return chromeOptions();
            case FIREFOX: return firefoxOptions();
            case EDGE:    return edgeOptions();
            default: throw new FrameworkException("No options mapping for " + browser);
        }
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) { options.addArguments("--headless=new"); }
        options.addArguments(
                "--window-size=" + Config.windowSize(),
                "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage",
                "--disable-notifications", "--disable-popup-blocking",
                "--disable-extensions", "--remote-allow-origins=*");
        options.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});
        options.setExperimentalOption("prefs", downloadPrefs());
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (Config.headless()) { options.addArguments("-headless"); }
        String[] size = Config.windowSize().split(",");
        options.addArguments("--width=" + size[0].trim(), "--height=" + size[1].trim());
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.download.dir", absoluteDownloadDir());
        options.addPreference("browser.helperApps.neverAsk.saveToDisk",
                "text/plain,text/csv,image/jpeg,image/png,application/pdf,application/octet-stream");
        options.addPreference("pdfjs.disabled", true);
        return options;
    }

    private static EdgeOptions edgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (Config.headless()) { options.addArguments("--headless=new"); }
        options.addArguments("--window-size=" + Config.windowSize(), "--no-sandbox", "--disable-dev-shm-usage");
        options.setExperimentalOption("prefs", downloadPrefs());
        return options;
    }

    private static Map<String, Object> downloadPrefs() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", absoluteDownloadDir());
        prefs.put("download.prompt_for_download", false);
        prefs.put("plugins.always_open_pdf_externally", true);
        prefs.put("profile.default_content_settings.popups", 0);
        return prefs;
    }

    private static String absoluteDownloadDir() {
        return Paths.get(Config.downloadDirectory()).toAbsolutePath().toString();
    }
}
