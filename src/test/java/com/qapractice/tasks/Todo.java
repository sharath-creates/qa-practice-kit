package com.qapractice.tasks;

import org.testng.SkipException;

/**
 * Marks a task you have not done yet.
 *
 * <p>Throwing {@link SkipException} makes TestNG report the method as SKIPPED
 * rather than FAILED, so a fresh clone runs green with a worklist rather than
 * a wall of red. Delete the {@code throw} line when you implement a task.
 */
public final class Todo {

    private Todo() { }

    public static SkipException task(String id) {
        return new SkipException("TODO " + id + " - read the javadoc above this method, then implement it");
    }
}
