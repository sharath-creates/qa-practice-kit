package com.qapractice.exceptions;

/** Signals an infrastructure fault rather than a product defect. */
public class FrameworkException extends RuntimeException {
    public FrameworkException(String message) { super(message); }
    public FrameworkException(String message, Throwable cause) { super(message, cause); }
}
