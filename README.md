# QA Practice Kit

26 graded exercises covering the commonly used features of **Maven**, **TestNG** and
**Selenium**, against two stable public practice sites. The scaffolding is done; the
testing is yours.

```bash
mvn clean test
```

A fresh clone reports **0 failed, 24 skipped**. Every unattempted task throws
`SkipException`, so the build stays green and the skip count is your worklist. Each task
you finish flips from skipped to passed.

---

## What you need

JDK 11 or newer, Maven 3.6.3+, and Chrome. Nothing else: Selenium Manager fetches the
driver at runtime.

```bash
mvn -version     # check the JDK line. It is often not the one on PATH.
```

The build fails in two seconds with a readable message on an older JDK rather than
surfacing it later as a bytecode version mismatch inside a Selenium jar.

## Systems under test

| Site | Used by | Why |
|---|---|---|
| [the-internet.herokuapp.com](https://the-internet.herokuapp.com) | Level 2 | One page per Selenium feature. Frames, alerts, waits, tables, uploads. |
| [saucedemo.com](https://www.saucedemo.com) | Level 3 | A real e-commerce flow with six user personas and `data-test` attributes throughout. |

Both are free, need no signup, and are the canonical practice targets. Credentials are in
the task javadoc.

## Layout

```
src/main/java/com/qapractice/
├── config/     Config          layered configuration          GIVEN
├── driver/     DriverFactory   browser options, Grid support  GIVEN
│               DriverManager   one driver per thread          GIVEN
├── pages/      BasePage        waits and interactions         GIVEN, read it first
│               sauce/          your page objects              LEVEL 3
├── support/    Text, Downloads normalisation, file waiting    GIVEN
└── katas/      PriceParser     plain Java to test in Level 1  GIVEN
                Basket

src/test/java/com/qapractice/
├── tasks/      Level1..Level4  24 stubs. This is the assignment.
├── solutions/  worked answers  Look after attempting, not before.
└── listeners/  worked answers to Level 4
```

## Running

```bash
mvn clean test                    # the task suite
mvn clean test -Pheaded           # watch the browser
mvn clean test -Psolutions        # the worked answers
mvn clean test -Pparallel         # four threads

mvn test -Dtest=Level2SeleniumCore                       # one class
mvn test -Dtest=Level2SeleniumCore#formAuthentication    # one method
mvn test -Dgroups=fast -DsuiteFile=                      # one group, no suite file

mvn allure:serve                  # the HTML report
```

## The levels

| Level | Tasks | Covers |
|---|---:|---|
| 1 — TestNG without a browser | 5 | `@Test`, fixtures, data providers, expectedExceptions, timeOut, invocationCount, groups, `@Parameters` |
| 2 — Selenium core | 10 | Locator strategies, explicit waits, alerts, frames, windows, Actions, tables, upload, download |
| 3 — Page objects and a real flow | 5 | POM, external test data, sorting, cart state, checkout arithmetic |
| 4 — Framework engineering | 4 | Config layering, ThreadLocal and parallelism, ITestListener, retry analyzer |
| 5 — Reporting and CI | 2 | Allure, GitHub Actions |

Levels 1 to 4 live as stubs in `src/test/java/com/qapractice/tasks/`. Read the javadoc
above each method: it is the specification, and it deliberately tells you the behaviour
rather than the selectors. Finding locators is part of the job.

Level 5 has no stubs; the acceptance criteria are in the assignment brief and the answers
are `.github/workflows/practice.yml` and the Allure configuration already in `pom.xml`.

## How to work through it

1. Read `BasePage` before Level 2. Everything you are about to write by hand is in there.
2. Attempt a task before opening the solution. Reading a solution feels like learning and is not.
3. After each task passes, break it on purpose and confirm it goes red with a message that
   explains why. A test that has only ever passed is unverified.
4. Keep a note of what surprised you. That list is worth more than the code afterwards.

## Rules a reviewer would apply

- No `Thread.sleep`, anywhere.
- No `By` locator in a Level 3 test class.
- No assertion inside a page object.
- Every assertion carries a `.as(...)` description.
- Each test passes when run alone with `-Dtest=Class#method`.
