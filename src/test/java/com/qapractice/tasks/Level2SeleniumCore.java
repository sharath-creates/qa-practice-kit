package com.qapractice.tasks;

import com.qapractice.BaseTest;
import org.testng.annotations.Test;

/**
 * LEVEL 2 - Selenium core, against https://the-internet.herokuapp.com
 *
 * <p>No page objects yet. Drive the browser directly from the test so the
 * mechanics are visible, then feel the pain of it, which is what makes Level 3
 * land. Use the helpers on {@code BasePage}? You cannot: you are in a test, not
 * a page. Use {@code DriverManager.getDriver()} and Selenium's own API.
 *
 * <p>Read {@code src/main/java/com/qapractice/pages/BasePage.java} first anyway.
 * Everything you are about to write by hand is in there, and seeing why it is
 * written that way is half the lesson.
 *
 * <p>Every URL is {@code Config.url(Config.internetUrl(), "/path")}.
 */
public class Level2SeleniumCore extends BaseTest {

    /**
     * TASK 2.1 - Locator strategies.
     *
     * <p>Open {@code /login} and locate the same three controls four different
     * ways: the username field by {@code By.id}, the password field by
     * {@code By.name}, the submit button by {@code By.cssSelector}, and the
     * page heading by {@code By.xpath}. Assert the page title is
     * {@code "The Internet"} and that all four elements are displayed.
     *
     * <p>Then open the home page and click the "Form Authentication" link with
     * {@code By.linkText}. Assert you land back on {@code /login}.
     *
     * <p>Finish by writing down, in a comment, which of the four you would
     * choose in real work and why.
     *
     * <p>Covers: By.id, By.name, By.cssSelector, By.xpath, By.linkText, navigation.
     */
    @Test(description = "2.1 - Locate the same controls four different ways")
    public void locatorStrategies() {
        throw Todo.task("2.1");
    }

    /**
     * TASK 2.2 - Form authentication, positive and negative.
     *
     * <p>Write a {@code @DataProvider} with three rows and drive {@code /login}
     * with each. The site's real behaviour:
     * <ul>
     *   <li>{@code tomsmith} / {@code SuperSecretPassword!} lands on
     *       {@code /secure} with the flash message
     *       {@code "You logged into a secure area!"}</li>
     *   <li>a valid username with a wrong password stays on {@code /login} with
     *       {@code "Your password is invalid!"}</li>
     *   <li>an unknown username gives {@code "Your username is invalid!"}</li>
     * </ul>
     *
     * <p>The flash element is {@code #flash}. Its text includes a trailing
     * {@code ×} from the close button, so assert with {@code contains}, not
     * equality, or strip it. Deciding that is part of the task.
     *
     * <p>Covers: data providers, form submission, negative tests, text assertions.
     */
    @Test(description = "2.2 - Log in with valid and invalid credentials")
    public void formAuthentication() {
        throw Todo.task("2.2");
    }

    /**
     * TASK 2.3 - Checkboxes and a real select dropdown.
     *
     * <p>On {@code /checkboxes} there are two checkboxes: the first is
     * unchecked, the second is checked. Assert that starting state, click both,
     * and assert the state inverted. Use {@code isSelected()}, not the
     * {@code checked} attribute, and work out why they differ here.
     *
     * <p>On {@code /dropdown}, use {@code org.openqa.selenium.support.ui.Select}
     * to choose "Option 2" by visible text, then assert
     * {@code getFirstSelectedOption()} reports it. Then try selecting the
     * "Please select an option" entry and observe what happens: it is
     * {@code disabled}.
     *
     * <p>Covers: isSelected, Select, option state.
     */
    @Test(description = "2.3 - Checkbox state and the Select helper")
    public void checkboxesAndDropdown() {
        throw Todo.task("2.3");
    }

    /**
     * TASK 2.4 - The two shapes of waiting.
     *
     * <p>The most important task in this level. Two pages that look identical
     * and need different conditions:
     * <ul>
     *   <li>{@code /dynamic_loading/1} - {@code #finish} is in the DOM from the
     *       start but hidden. The right condition is
     *       {@code visibilityOfElementLocated}.</li>
     *   <li>{@code /dynamic_loading/2} - {@code #finish} does not exist until
     *       the AJAX call returns. {@code presenceOfElementLocated} is what
     *       finds it; visibility works too, and you should understand why.</li>
     * </ul>
     *
     * <p>On both, click {@code #start button}, wait, and assert the text
     * {@code "Hello World!"}. The site takes about five seconds on purpose.
     *
     * <p>Now the part that matters: try example 1 with
     * {@code presenceOfElementLocated} and assert on the text. It will return an
     * element immediately and give you an empty string. Write down why.
     *
     * <p>Covers: WebDriverWait, ExpectedConditions, presence versus visibility.
     */
    @Test(description = "2.4 - Explicit waits: presence versus visibility")
    public void waitingForDynamicContent() {
        throw Todo.task("2.4");
    }

    /**
     * TASK 2.5 - Native JavaScript dialogs.
     *
     * <p>On {@code /javascript_alerts}, exercise all three buttons:
     * <ul>
     *   <li>Alert: accept it, then assert {@code #result} reads
     *       {@code "You successfully clicked an alert"}</li>
     *   <li>Confirm: dismiss it, then assert {@code #result} reads
     *       {@code "You clicked: Cancel"}</li>
     *   <li>Prompt: type your name into it, accept, then assert
     *       {@code #result} reads {@code "You entered: <your name>"}</li>
     * </ul>
     *
     * <p>Wait for each dialog with {@code ExpectedConditions.alertIsPresent()},
     * never a sleep. A dialog left unanswered blocks the renderer and every
     * later call times out, which is a failure mode worth causing once
     * deliberately so you recognise it later.
     *
     * <p>Covers: driver.switchTo().alert(), accept, dismiss, sendKeys.
     */
    @Test(description = "2.5 - Alert, confirm and prompt")
    public void javaScriptAlerts() {
        throw Todo.task("2.5");
    }

    /**
     * TASK 2.6 - Frames.
     *
     * <p>{@code /nested_frames} is a real frameset: {@code frame-top} contains
     * {@code frame-left}, {@code frame-middle} and {@code frame-right};
     * {@code frame-bottom} sits below it.
     *
     * <p>Switch into top then left and assert the body text is {@code "LEFT"}.
     * Return to the top-level document, switch into {@code frame-bottom}, and
     * assert {@code "BOTTOM"}. Use {@code switchTo().parentFrame()} at least
     * once and {@code switchTo().defaultContent()} at least once, and note the
     * difference.
     *
     * <p>Forgetting to switch back is one of the most common Selenium bugs:
     * every later locator silently searches the wrong document.
     *
     * <p>Covers: switchTo().frame by name, parentFrame, defaultContent.
     */
    @Test(description = "2.6 - Nested frames, and switching back out")
    public void frames() {
        throw Todo.task("2.6");
    }

    /**
     * TASK 2.7 - Windows and tabs.
     *
     * <p>On {@code /windows}, capture {@code getWindowHandles()} before
     * clicking "Click Here". Click it, wait until the handle count grows, switch
     * to the new handle, and assert its title is {@code "New Window"}. Close it,
     * switch back to the original, and assert the title is
     * {@code "The Internet"}.
     *
     * <p>Two traps to meet: asserting the new title immediately after the click
     * without switching, and forgetting to switch back after closing, which
     * leaves the driver pointing at a dead window.
     *
     * <p>Stretch: open a tab with Selenium 4's
     * {@code driver.switchTo().newWindow(WindowType.TAB)} and compare.
     *
     * <p>Covers: window handles, switching, closing, WindowType.
     */
    @Test(description = "2.7 - Handle a second browser window")
    public void windowsAndTabs() {
        throw Todo.task("2.7");
    }

    /**
     * TASK 2.8 - The Actions API, and a genuine trap.
     *
     * <p>Four parts, on four pages:
     * <ul>
     *   <li>{@code /hovers} - hover the first avatar and assert the caption
     *       {@code "name: user1"} becomes visible. It is hidden by CSS until
     *       hover, so a plain {@code getText()} without hovering returns empty.</li>
     *   <li>{@code /context_menu} - right-click {@code #hot-spot} and accept the
     *       alert reading {@code "You selected a context menu"}.</li>
     *   <li>{@code /key_presses} - send {@code Keys.ENTER} to {@code #target}
     *       and assert {@code #result} reads {@code "You entered: ENTER"}.</li>
     *   <li>{@code /drag_and_drop} - drag {@code #column-a} onto
     *       {@code #column-b} and assert the headers swapped.</li>
     * </ul>
     *
     * <p>The last one will not work with {@code Actions.dragAndDrop}, and that
     * is the point. The page uses the HTML5 drag-and-drop API, which fires
     * {@code dragstart}/{@code drop} events that WebDriver's native mouse
     * emulation does not generate. Find that out, then solve it by dispatching
     * the events yourself through {@code JavascriptExecutor}. Getting stuck here
     * for twenty minutes is the intended experience.
     *
     * <p>Covers: Actions, moveToElement, contextClick, sendKeys, JS-driven events.
     */
    @Test(description = "2.8 - Hover, right-click, key presses, HTML5 drag and drop")
    public void actionsApi() {
        throw Todo.task("2.8");
    }

    /**
     * TASK 2.9 - Reading a table.
     *
     * <p>{@code /tables} shows {@code #table1} with columns Last Name, First
     * Name, Email, Due, Web Site, Action.
     *
     * <ul>
     *   <li>Read every row into a small Java record or class. Do not assert
     *       against raw cell text scattered through the test.</li>
     *   <li>Assert the row for {@code "Smith"} has email
     *       {@code "jsmith@gmail.com"} and due {@code "$50.00"}.</li>
     *   <li>Click the {@code "Due"} column header, then assert the Due values
     *       are now in ascending numeric order. Parse them; do not compare
     *       strings, or {@code "$100.00"} sorts before {@code "$50.00"}.</li>
     * </ul>
     *
     * <p>Covers: nested findElements, mapping DOM to objects, ordering assertions.
     */
    @Test(description = "2.9 - Parse a table and assert it sorts correctly")
    public void sortableTable() {
        throw Todo.task("2.9");
    }

    /**
     * TASK 2.10 - Upload and download.
     *
     * <p>Upload: on {@code /upload}, send the absolute path of
     * {@code src/test/resources/testdata/upload-me.txt} to {@code #file-upload},
     * click {@code #file-submit}, and assert {@code #uploaded-files} shows the
     * filename. Never click the file input: that opens an OS dialog WebDriver
     * cannot touch.
     *
     * <p>Download: on {@code /download}, click any link and wait for the file to
     * land in {@code Config.downloadDirectory()}. Use
     * {@code Downloads.clear()} first so you cannot pass on a previous run's
     * file, and {@code Downloads.waitForFile(...)} rather than a sleep. Read
     * that helper and understand why it waits for the size to stop changing.
     *
     * <p>Covers: file inputs, browser download preferences, filesystem waiting.
     */
    @Test(description = "2.10 - Upload a file and wait for a download")
    public void uploadAndDownload() {
        throw Todo.task("2.10");
    }
}
