package com.qapractice.solutions;

import static org.assertj.core.api.Assertions.assertThat;

import com.qapractice.BaseTest;
import com.qapractice.config.Config;
import com.qapractice.driver.DriverManager;
import com.qapractice.katas.PriceParser;
import com.qapractice.support.Downloads;
import com.qapractice.support.Text;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Worked answers for Level 2, driving the browser directly as the tasks ask.
 *
 * <p>Notice how much noise the locators add here compared with Level 3. That
 * contrast is the argument for page objects, and it is easier to feel than to
 * be told.
 */
public class Level2Solutions extends BaseTest {

    private WebDriver driver() { return DriverManager.getDriver(); }

    private WebDriverWait waitFor() {
        return new WebDriverWait(driver(), Config.explicitWait(), Config.polling());
    }

    private void open(String path) { driver().get(Config.url(Config.internetUrl(), path)); }

    // ------------------------------------------------------------------ 2.1

    @Test(description = "2.1 - Locate the same controls four different ways")
    public void locatorStrategies() {
        open("/login");

        WebElement username = driver().findElement(By.id("username"));
        WebElement password = driver().findElement(By.name("password"));
        WebElement submit   = driver().findElement(By.cssSelector("#login button[type='submit']"));
        WebElement heading  = driver().findElement(By.xpath("//div[@class='example']/h2"));

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(driver().getTitle()).as("page title").isEqualTo("The Internet");
        soft.assertThat(username.isDisplayed()).as("username field, located by id").isTrue();
        soft.assertThat(password.isDisplayed()).as("password field, located by name").isTrue();
        soft.assertThat(submit.isDisplayed()).as("submit button, located by css").isTrue();
        soft.assertThat(Text.squash(heading.getText())).as("heading, located by xpath").isEqualTo("Login Page");
        soft.assertAll();

        // In real work: the data-qa or id first, structural CSS next, and XPath
        // on visible text only as a last resort. Ids are unique and survive
        // restyling; an XPath anchored on copy breaks when marketing edits it.
        open("/");
        driver().findElement(By.linkText("Form Authentication")).click();
        assertThat(driver().getCurrentUrl()).as("linkText navigation").endsWith("/login");
    }

    // ------------------------------------------------------------------ 2.2

    @DataProvider(name = "credentials")
    public Object[][] credentials() {
        return new Object[][] {
                {"tomsmith", "SuperSecretPassword!", true,  "You logged into a secure area!"},
                {"tomsmith", "wrong",                false, "Your password is invalid!"},
                {"nobody",   "SuperSecretPassword!", false, "Your username is invalid!"},
        };
    }

    @Test(dataProvider = "credentials", description = "2.2 - Log in with valid and invalid credentials")
    public void formAuthentication(String user, String pass, boolean shouldSucceed, String expectedFlash) {
        open("/login");
        driver().findElement(By.id("username")).sendKeys(user);
        driver().findElement(By.id("password")).sendKeys(pass);
        driver().findElement(By.cssSelector("#login button[type='submit']")).click();

        String flash = Text.squash(
                waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.id("flash"))).getText());

        SoftAssertions soft = new SoftAssertions();
        // The flash text carries a trailing multiplication sign from the close
        // button, so contains is honest here and equality would be brittle.
        soft.assertThat(flash).as("flash message for '%s'", user).contains(expectedFlash);
        soft.assertThat(driver().getCurrentUrl().endsWith("/secure"))
                .as("reached the secure area").isEqualTo(shouldSucceed);
        soft.assertAll();
    }

    // ------------------------------------------------------------------ 2.3

    @Test(description = "2.3 - Checkbox state and the Select helper")
    public void checkboxesAndDropdown() {
        open("/checkboxes");
        List<WebElement> boxes = driver().findElements(By.cssSelector("#checkboxes input[type='checkbox']"));

        SoftAssertions initial = new SoftAssertions();
        initial.assertThat(boxes).as("two checkboxes are present").hasSize(2);
        initial.assertThat(boxes.get(0).isSelected()).as("first starts unchecked").isFalse();
        initial.assertThat(boxes.get(1).isSelected()).as("second starts checked").isTrue();
        initial.assertAll();

        boxes.forEach(WebElement::click);

        SoftAssertions toggled = new SoftAssertions();
        // isSelected() reads the live property. The `checked` attribute is what
        // the markup said at load time, which is why the two disagree here.
        toggled.assertThat(boxes.get(0).isSelected()).as("first is now checked").isTrue();
        toggled.assertThat(boxes.get(1).isSelected()).as("second is now unchecked").isFalse();
        toggled.assertAll();

        open("/dropdown");
        Select dropdown = new Select(driver().findElement(By.id("dropdown")));
        dropdown.selectByVisibleText("Option 2");
        assertThat(dropdown.getFirstSelectedOption().getText())
                .as("selected option").isEqualTo("Option 2");
    }

    // ------------------------------------------------------------------ 2.4

    @Test(description = "2.4a - Element present but hidden: wait for visibility")
    public void waitForHiddenElementToBecomeVisible() {
        open("/dynamic_loading/1");
        driver().findElement(By.cssSelector("#start button")).click();

        // presenceOfElementLocated would return immediately here, because
        // #finish is already in the DOM with display:none, and getText() on a
        // hidden element returns an empty string.
        WebElement finish = waitFor()
                .withMessage("#finish never became visible")
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("finish")));

        assertThat(Text.squash(finish.getText())).as("loaded text").isEqualTo("Hello World!");
    }

    @Test(description = "2.4b - Element absent until AJAX returns: wait for it to exist")
    public void waitForElementToBeAdded() {
        open("/dynamic_loading/2");
        driver().findElement(By.cssSelector("#start button")).click();

        // Here #finish does not exist at all until the response lands, so
        // presence is the condition that describes what we are waiting for.
        // Visibility also works, and additionally guarantees it is rendered.
        WebElement finish = waitFor()
                .withMessage("#finish was never added to the DOM")
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("finish")));

        assertThat(Text.squash(finish.getText())).as("loaded text").isEqualTo("Hello World!");
    }

    // ------------------------------------------------------------------ 2.5

    @Test(description = "2.5 - Alert, confirm and prompt")
    public void javaScriptAlerts() {
        open("/javascript_alerts");
        By result = By.id("result");
        List<WebElement> buttons = driver().findElements(By.cssSelector("ul li button"));

        buttons.get(0).click();
        waitFor().until(ExpectedConditions.alertIsPresent()).accept();
        assertThat(Text.squash(driver().findElement(result).getText()))
                .as("after accepting the alert").isEqualTo("You successfully clicked an alert");

        driver().findElements(By.cssSelector("ul li button")).get(1).click();
        waitFor().until(ExpectedConditions.alertIsPresent()).dismiss();
        assertThat(Text.squash(driver().findElement(result).getText()))
                .as("after dismissing the confirm").isEqualTo("You clicked: Cancel");

        driver().findElements(By.cssSelector("ul li button")).get(2).click();
        Alert prompt = waitFor().until(ExpectedConditions.alertIsPresent());
        prompt.sendKeys("QA practice");
        prompt.accept();
        assertThat(Text.squash(driver().findElement(result).getText()))
                .as("after typing into the prompt").isEqualTo("You entered: QA practice");
    }

    // ------------------------------------------------------------------ 2.6

    @Test(description = "2.6 - Nested frames, and switching back out")
    public void frames() {
        open("/nested_frames");

        driver().switchTo().frame("frame-top");
        driver().switchTo().frame("frame-left");
        assertThat(Text.squash(driver().findElement(By.tagName("body")).getText()))
                .as("left frame content").isEqualTo("LEFT");

        // parentFrame goes up one level, to frame-top. defaultContent goes all
        // the way out to the top-level document.
        driver().switchTo().parentFrame();
        driver().switchTo().frame("frame-middle");
        assertThat(Text.squash(driver().findElement(By.id("content")).getText()))
                .as("middle frame content").isEqualTo("MIDDLE");

        driver().switchTo().defaultContent();
        driver().switchTo().frame("frame-bottom");
        assertThat(Text.squash(driver().findElement(By.tagName("body")).getText()))
                .as("bottom frame content").isEqualTo("BOTTOM");

        driver().switchTo().defaultContent();
    }

    // ------------------------------------------------------------------ 2.7

    @Test(description = "2.7 - Handle a second browser window")
    public void windowsAndTabs() {
        open("/windows");
        String original = driver().getWindowHandle();
        Set<String> before = driver().getWindowHandles();

        driver().findElement(By.linkText("Click Here")).click();

        waitFor().withMessage("no second window opened").until(d -> d.getWindowHandles().size() > before.size());
        String opened = driver().getWindowHandles().stream()
                .filter(h -> !before.contains(h))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("new window vanished"));

        driver().switchTo().window(opened);
        assertThat(driver().getTitle()).as("new window title").isEqualTo("New Window");

        driver().close();
        driver().switchTo().window(original);
        assertThat(driver().getTitle()).as("back on the original window").isEqualTo("The Internet");
    }

    // ------------------------------------------------------------------ 2.8

    @Test(description = "2.8a - Hover reveals a caption hidden by CSS")
    public void hover() {
        open("/hovers");
        WebElement firstAvatar = driver().findElements(By.cssSelector(".figure")).get(0);
        new Actions(driver()).moveToElement(firstAvatar).perform();

        // Scope the caption lookup to the tile that was hovered, rather than
        // relying on an nth-of-type index that a layout change would shift.
        WebElement caption = firstAvatar.findElement(By.cssSelector(".figcaption h5"));
        waitFor().until(ExpectedConditions.visibilityOf(caption));
        assertThat(Text.squash(caption.getText())).as("revealed caption").isEqualTo("name: user1");
    }

    @Test(description = "2.8b - Right-click triggers a native alert")
    public void contextClick() {
        open("/context_menu");
        new Actions(driver()).contextClick(driver().findElement(By.id("hot-spot"))).perform();
        Alert alert = waitFor().until(ExpectedConditions.alertIsPresent());
        assertThat(alert.getText()).as("context menu alert").isEqualTo("You selected a context menu");
        alert.accept();
    }

    @Test(description = "2.8c - Key presses are reported by name")
    public void keyPresses() {
        open("/key_presses");
        driver().findElement(By.id("target")).sendKeys(Keys.ENTER);
        assertThat(Text.squash(driver().findElement(By.id("result")).getText()))
                .as("the page names the key it received").isEqualTo("You entered: ENTER");
    }

    /**
     * The trap. This page uses the HTML5 drag-and-drop API, whose
     * dragstart/dragover/drop events are not produced by WebDriver's native
     * mouse emulation, so Actions.dragAndDrop silently does nothing. Dispatching
     * the events through the JS executor is the standard workaround.
     */
    @Test(description = "2.8d - HTML5 drag and drop needs synthesised events")
    public void html5DragAndDrop() {
        open("/drag_and_drop");
        By columnA = By.id("column-a");
        By columnB = By.id("column-b");

        assertThat(Text.squash(driver().findElement(columnA).getText())).as("before").isEqualTo("A");

        simulateHtml5DragAndDrop(driver().findElement(columnA), driver().findElement(columnB));

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(Text.squash(driver().findElement(columnA).getText())).as("column A after").isEqualTo("B");
        soft.assertThat(Text.squash(driver().findElement(columnB).getText())).as("column B after").isEqualTo("A");
        soft.assertAll();
    }

    private void simulateHtml5DragAndDrop(WebElement source, WebElement target) {
        String script =
                "var src = arguments[0], tgt = arguments[1];"
              + "var dt = new DataTransfer();"
              + "['dragstart','dragenter','dragover','drop','dragend'].forEach(function (type) {"
              + "  var node = (type === 'dragstart' || type === 'dragend') ? src : tgt;"
              + "  var evt = new DragEvent(type, {bubbles: true, cancelable: true, dataTransfer: dt});"
              + "  node.dispatchEvent(evt);"
              + "});";
        ((JavascriptExecutor) driver()).executeScript(script, source, target);
    }

    // ------------------------------------------------------------------ 2.9

    /** One table row, parsed once so assertions read as data rather than as DOM. */
    private static final class Row {
        final String lastName;
        final String email;
        final long dueCents;

        Row(String lastName, String email, long dueCents) {
            this.lastName = lastName;
            this.email = email;
            this.dueCents = dueCents;
        }
    }

    @Test(description = "2.9 - Parse a table and assert it sorts correctly")
    public void sortableTable() {
        open("/tables");

        List<Row> rows = readTable();
        Row smith = rows.stream()
                .filter(r -> r.lastName.equals("Smith"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No row for Smith"));

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(smith.email).as("Smith's email").isEqualTo("jsmith@gmail.com");
        soft.assertThat(smith.dueCents).as("Smith's due amount in cents").isEqualTo(5000L);
        soft.assertAll();

        driver().findElement(By.xpath("//table[@id='table1']//th[span[text()='Due']]")).click();

        List<Long> due = readTable().stream().map(r -> r.dueCents).collect(Collectors.toList());
        // Compare parsed numbers, not strings: "$100.00" sorts before "$50.00"
        // lexicographically, and that bug is easy to ship.
        assertThat(due).as("Due column after sorting ascending").isSortedAccordingTo(Comparator.naturalOrder());
    }

    private List<Row> readTable() {
        List<Row> rows = new ArrayList<>();
        for (WebElement tr : driver().findElements(By.cssSelector("#table1 tbody tr"))) {
            List<WebElement> cells = tr.findElements(By.tagName("td"));
            rows.add(new Row(
                    Text.squash(cells.get(0).getText()),
                    Text.squash(cells.get(2).getText()),
                    PriceParser.toCents(cells.get(3).getText())));
        }
        return rows;
    }

    // ----------------------------------------------------------------- 2.10

    @Test(description = "2.10a - Upload a file through the file input")
    public void fileUpload() {
        open("/upload");
        Path file = Paths.get("src/test/resources/testdata/upload-me.txt").toAbsolutePath();

        // sendKeys on the input is the supported path. Clicking it opens a
        // native OS dialog that WebDriver cannot drive.
        driver().findElement(By.id("file-upload")).sendKeys(file.toString());
        driver().findElement(By.id("file-submit")).click();

        WebElement uploaded = waitFor().until(
                ExpectedConditions.visibilityOfElementLocated(By.id("uploaded-files")));
        assertThat(Text.squash(uploaded.getText())).as("uploaded filename").isEqualTo("upload-me.txt");
    }

    @Test(description = "2.10b - Click a download link and wait for the file to land")
    public void fileDownload() {
        Downloads.clear();
        open("/download");

        WebElement firstLink = waitFor().until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".example a")));
        String fileName = Text.squash(firstLink.getText());
        firstLink.click();

        // The download is asynchronous and outside the page lifecycle, so there
        // is no element to wait on. Wait on the filesystem instead.
        Path downloaded = Downloads.waitForFile(fileName, Duration.ofSeconds(30));

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(downloaded).as("the file exists on disk").exists();
        soft.assertThat(downloaded.toFile().length()).as("and is not empty").isPositive();
        soft.assertAll();
    }
}
