# QA Practice Kit

A set of **26 small exercises** that teach you how to write automated tests for websites.

You do not need to know anything about testing to start. You do need to be comfortable
reading and writing a little Java.

---

## What is an automated test?

A program that opens a web browser, clicks around a website the way a person would, and
then checks that the right thing happened.

If it did, the test **passes**. If it did not, the test **fails** and tells you what it
expected versus what it actually saw.

That is the whole idea. Everything else is detail.

---

## What you need installed

| Thing | What it does | Check it works |
|---|---|---|
| **Java** (version 11 or newer) | The language the tests are written in | `java -version` |
| **Maven** | Downloads the libraries and runs everything | `mvn -version` |
| **Google Chrome** | The browser the tests will drive | Open it |

You do **not** need to download a browser driver. The project handles that by itself.

> **The most common setup problem.** `mvn -version` prints its own Java version on the
> last line, and it is often a different one from `java -version`. Maven is the one that
> matters. If it says 1.8, point `JAVA_HOME` at a newer Java and open a new terminal.
> The build will stop in two seconds with a clear message if this is wrong, so you will
> not waste time guessing.

---

## Run it once, right now

```bash
cd qa-practice-kit
mvn clean test
```

The first run takes a few minutes because Maven is downloading libraries. Later runs take
seconds.

**You should see something like this:**

```
Tests run: 24, Failures: 0, Errors: 0, Skipped: 24
BUILD SUCCESS
```

### "Skipped" is correct. Nothing is broken.

Every exercise starts as an empty stub that says *"not done yet"*. Skipped means
*"you have not written this one"*, not *"this is broken"*.

So your progress looks like this:

- **Day one:** 24 skipped, 0 passed
- **Later:** 18 skipped, 6 passed
- **Finished:** 0 skipped, 24 passed

Turning skips into passes is the entire assignment.

---

## The three tools, one sentence each

You will keep meeting these three names. Here is all you need at the start.

- **Maven** fetches the libraries your code needs, compiles it, and runs it.
  The file it reads is `pom.xml`.
- **TestNG** decides which tests to run and reports pass, fail or skip.
  Anything marked `@Test` is a test.
- **Selenium** is what actually drives the browser. Click this, type that, read that text.

They stack: Maven starts TestNG, TestNG runs your test, your test uses Selenium, Selenium
drives Chrome.

---

## What all these folders are

```
qa-practice-kit/
├── pom.xml                  The shopping list and the build instructions
└── src/
    ├── main/java/...        Machinery, already written for you
    │   ├── config/            Where the website addresses and settings live
    │   ├── driver/            Starts and stops the browser
    │   ├── pages/             BasePage: the waiting and clicking helpers
    │   └── katas/             Two tiny Java classes you test in Level 1
    └── test/
        ├── java/.../tasks/       YOUR HOMEWORK. Start here.
        ├── java/.../solutions/   The answers. Read them after trying.
        └── resources/
            ├── config/           Settings you can change
            ├── suites/           Lists of which tests to run
            └── testdata/         Files the tests use
```

**You will only edit things under `tasks/`** and, from Level 3 onward, add a few new files
under `pages/`.

---

## How to do one exercise

1. Open `src/test/java/com/qapractice/tasks/Level1TestNgBasics.java`.
2. Read the comment block above the first method. **That comment is the instructions.**
   It tells you what the test must prove, not how to write it.
3. Delete the line `throw Todo.task("1.1");` and write your test instead.
4. Run just that one test:
   ```bash
   mvn test -Dtest=Level1TestNgBasics#firstTest -DsuiteFile=
   ```
5. Keep going until it passes.
6. Then break it on purpose. Change an expected value, run it again, and check it fails
   with a message that makes sense. Change it back.

Step 6 sounds like a waste of time and is not. A test that has only ever passed might not
be checking anything at all. Ten seconds of proof is worth it.

---

## The order to work through

| Level | What it teaches | Browser? |
|---|---|---|
| **1** | How TestNG works, using two tiny Java classes | No |
| **2** | Selenium itself: clicking, typing, waiting, alerts, tables | Yes |
| **3** | How to organise tests so they stay maintainable | Yes |
| **4** | The supporting machinery: parallel runs, screenshots on failure | Yes |
| **5** | Reports and running tests automatically on GitHub | No |

Level 1 has no browser on purpose. Learning the test runner without a browser in the way
is much faster, and it means a failure can only be your test, never a flaky website.

---

## Commands you will actually use

```bash
mvn clean test                  # run everything
mvn clean test -Pheaded         # same, but watch the browser do it
mvn clean test -Psolutions      # run the answers instead of your work

# run one class
mvn test -Dtest=Level2SeleniumCore

# run one single test
mvn test -Dtest=Level2SeleniumCore#formAuthentication

mvn -version                    # which Java is Maven using
```

`-Pheaded` is worth using whenever something confuses you. Watching the browser click
through your test explains most problems in about five seconds.

---

## The websites you are testing

Two free practice sites. No signup, nothing to install.

- **[the-internet.herokuapp.com](https://the-internet.herokuapp.com)** has one page per
  feature: pop-up alerts, frames, tables, file uploads. Used in Level 2.
- **[saucedemo.com](https://www.saucedemo.com)** is a small pretend shop with a login and
  a checkout. Used in Level 3. The password for every user is `secret_sauce`.

The exercises tell you what the site does, not which buttons to click. Finding those
yourself is part of the job, and the real job works the same way.

---

## When something goes wrong

| What you see | What it usually means |
|---|---|
| `Skipped: 24` | Normal. You have not done those exercises yet. |
| `class file has wrong version` | Maven is using an old Java. Check `mvn -version`. |
| `NoSuchElementException` | Selenium could not find something on the page. Your selector is wrong, or you looked before the page finished loading. |
| `TimeoutException` | You waited for something that never happened. Run with `-Pheaded` and watch. |
| `StaleElementReferenceException` | The page reloaded part of itself after you found an element. Find it again. |
| `BUILD FAILURE` with no tests run | Your code does not compile. Scroll up to the first error. |

Stuck on a test for more than twenty minutes? Open the **Hint** on that task in the
assignment page. Still stuck after another twenty? Open the solution, read it, close it,
then write the test again from memory.

---

## Words you will meet

**Assertion.** The line that says what must be true. If it is not true, the test fails.

**Locator.** How you tell Selenium which thing on the page you mean, usually a CSS
selector or an id.

**Wait.** Telling Selenium to pause until something appears, rather than guessing how
long the page will take. Never use a fixed sleep.

**Page object.** A class that holds all the clicking and typing for one screen, so your
tests read like sentences instead of selectors. Level 3 is about this.

**Flaky.** A test that passes sometimes and fails other times without the code changing.
The most annoying thing in testing, and most of Level 2 is about avoiding it.

**Headless.** Running the browser invisibly. Faster, and the default here. Use
`-Pheaded` to see it.

**Suite.** A list of which tests to run, kept in `src/test/resources/suites/`.

---

## The one rule

**Try every exercise before opening the solution.**

Reading a worked answer feels like learning and mostly is not. You will nod along, close
the file, and find you cannot reproduce it. Struggle first. The solutions are there to
check yourself against, not to copy.

---

## Licence

MIT. Do whatever you like with it.
