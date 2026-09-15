package com.qapractice.driver;

import com.qapractice.exceptions.FrameworkException;
import java.util.Arrays;

public enum BrowserType {
    CHROME, FIREFOX, EDGE;

    public static BrowserType from(String value) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new FrameworkException(
                        "Unsupported browser '" + value + "'. Supported: " + Arrays.toString(values())));
    }
}
