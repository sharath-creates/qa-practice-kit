package com.qapractice.support;

import com.qapractice.config.Config;
import com.qapractice.exceptions.FrameworkException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Waiting on the filesystem.
 *
 * <p>A download is asynchronous and happens outside the page lifecycle, so there
 * is no element to wait on. The only honest signal that one finished is the file
 * existing, being non-empty, and not having grown between two reads.
 */
public final class Downloads {

    private Downloads() { }

    public static Path directory() {
        Path dir = Paths.get(Config.downloadDirectory()).toAbsolutePath();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new FrameworkException("Could not create download directory " + dir, e);
        }
        return dir;
    }

    /** Removes leftovers so an assertion can never pass on a previous run's file. */
    public static void clear() {
        try (Stream<Path> files = Files.list(directory())) {
            files.sorted(Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException ignored) { /* held open; names are unique */ }
            });
        } catch (IOException e) {
            throw new FrameworkException("Could not clean download directory", e);
        }
    }

    public static Path waitForFile(String fileName, Duration timeout) {
        return waitForFile(p -> p.getFileName().toString().equalsIgnoreCase(fileName),
                "named '" + fileName + "'", timeout);
    }

    public static Path waitForFile(Predicate<Path> matcher, String description, Duration timeout) {
        Path dir = directory();
        Instant deadline = Instant.now().plus(timeout);
        long lastSize = -1L;

        while (Instant.now().isBefore(deadline)) {
            Optional<Path> candidate = firstMatch(dir, matcher);
            if (candidate.isPresent()) {
                long size = sizeOf(candidate.get());
                if (size > 0 && size == lastSize) { return candidate.get(); }
                lastSize = size;
            }
            sleepQuietly();
        }
        throw new FrameworkException("No completed download " + description + " in " + dir
                + " within " + timeout + ". Present: " + names(dir));
    }

    private static Optional<Path> firstMatch(Path dir, Predicate<Path> matcher) {
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(".crdownload"))
                    .filter(p -> !p.getFileName().toString().endsWith(".part"))
                    .filter(matcher)
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static String names(Path dir) {
        try (Stream<Path> files = Files.list(dir)) {
            return files.map(p -> p.getFileName().toString()).collect(Collectors.toList()).toString();
        } catch (IOException e) {
            return "<unreadable>";
        }
    }

    private static long sizeOf(Path path) {
        try { return Files.size(path); } catch (IOException e) { return -1L; }
    }

    private static void sleepQuietly() {
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
