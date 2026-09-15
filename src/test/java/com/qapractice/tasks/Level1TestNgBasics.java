package com.qapractice.tasks;

import org.testng.annotations.Test;

/**
 * LEVEL 1 - TestNG without a browser.
 *
 * <p>Deliberately no {@code extends BaseTest}: TestNG is a test runner, not a
 * browser tool, and learning it without Selenium in the way is faster. The code
 * under test is {@code com.qapractice.katas.PriceParser} and
 * {@code com.qapractice.katas.Basket}. Both are correct as written; your job is
 * to prove it, and to learn the annotations while doing so.
 *
 * <p>Run just this level:
 * <pre>mvn test -Dtest=Level1TestNgBasics -DsuiteFile=</pre>
 */
public class Level1TestNgBasics {

    /**
     * TASK 1.1 - A first test and a readable assertion.
     *
     * <p>Using AssertJ ({@code import static org.assertj.core.api.Assertions.assertThat}),
     * prove that:
     * <ul>
     *   <li>{@code PriceParser.toCents("$29.99")} is 2999</li>
     *   <li>{@code PriceParser.toCents("$1,299.00")} is 129900</li>
     *   <li>{@code PriceParser.format(2999)} is {@code "$29.99"}</li>
     * </ul>
     *
     * <p>Every assertion must carry a {@code .as("...")} description. Prove to
     * yourself it works: break one expected value, run it, read the message.
     *
     * <p>Covers: {@code @Test}, AssertJ, assertion descriptions.
     */
    @Test(description = "1.1 - A first test and a readable assertion")
    public void firstTest() {
        throw Todo.task("1.1");
    }

    /**
     * TASK 1.2 - Fixture order.
     *
     * <p>Add {@code @BeforeClass}, {@code @BeforeMethod}, {@code @AfterMethod}
     * and {@code @AfterClass} methods to this class, each appending its own name
     * to a {@code List<String>} field. Then assert, inside this test, that:
     * <ul>
     *   <li>the first entry is {@code beforeClass}</li>
     *   <li>{@code beforeClass} appears exactly once</li>
     *   <li>the most recent entry is {@code beforeMethod}</li>
     * </ul>
     *
     * <p>Now the interesting part. Asserting that the list equals exactly
     * {@code [beforeClass, beforeMethod]} is the obvious thing to write and it
     * is wrong. Work out why before you look at the answer. The clue: this class
     * has five test methods and TestNG does not promise the order it runs them in.
     *
     * <p>Also answer: why is the {@code @AfterMethod} entry never visible from
     * inside a test?
     *
     * <p>Covers: the four lifecycle annotations, when each fires, and how an
     * assertion can quietly depend on execution order.
     */
    @Test(description = "1.2 - Prove the fixture execution order")
    public void fixtureOrder() {
        throw Todo.task("1.2");
    }

    /**
     * TASK 1.3 - Data providers.
     *
     * <p>Write a {@code @DataProvider(name = "prices")} returning at least five
     * rows of {@code {displayedPrice, expectedCents}}, including
     * {@code "$0.50"}, {@code "$1,299.00"} and a value with no decimal part.
     * Point this test at it and assert each row.
     *
     * <p>Check the output: TestNG reports one result per row, not one for the
     * method. That is the whole reason to use a provider rather than a loop.
     *
     * <p>Covers: {@code @DataProvider}, parameterised tests.
     */
    @Test(description = "1.3 - Parameterise a test with a data provider")
    public void dataProviderOverPrices() {
        throw Todo.task("1.3");
    }

    /**
     * TASK 1.4 - Expected exceptions, timeouts and repetition.
     *
     * <p>Split this into three tests:
     * <ol>
     *   <li>{@code @Test(expectedExceptions = IllegalArgumentException.class,
     *       expectedExceptionsMessageRegExp = ".*blank.*")} calling
     *       {@code PriceParser.toCents("")}</li>
     *   <li>{@code @Test(timeOut = 500)} around a {@link com.qapractice.katas.Basket}
     *       that adds 10,000 items and checks the total</li>
     *   <li>{@code @Test(invocationCount = 5)} asserting the tax maths is stable</li>
     * </ol>
     *
     * <p>Then check the trap: does {@code expectedExceptions} pass if the method
     * throws the right exception from the wrong line? Decide whether you care.
     *
     * <p>Covers: expectedExceptions, timeOut, invocationCount.
     */
    @Test(description = "1.4 - expectedExceptions, timeOut and invocationCount")
    public void exceptionsTimeoutsAndRepetition() {
        throw Todo.task("1.4");
    }

    /**
     * TASK 1.5 - Groups and suite parameters.
     *
     * <p>Two things:
     * <ul>
     *   <li>Tag this test {@code groups = {"fast", "level1"}} and run only that
     *       group from the command line. Work out the flag yourself.</li>
     *   <li>Read the {@code expectedCurrency} parameter declared in
     *       {@code src/test/resources/suites/tasks.xml} using
     *       {@code @Parameters("expectedCurrency")} and a method argument, then
     *       assert {@code PriceParser.format(100)} starts with the right symbol
     *       for it.</li>
     * </ul>
     *
     * <p>Note what happens if you run this method with {@code -Dtest=} and no
     * suite file. That failure mode is worth meeting once.
     *
     * <p>Covers: groups, {@code @Parameters}, suite-level configuration.
     */
    @Test(description = "1.5 - Groups and a parameter from the suite XML")
    public void groupsAndSuiteParameters() {
        throw Todo.task("1.5");
    }
}
