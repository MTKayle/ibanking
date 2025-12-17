package org.example.storyreading.ibanking.exception;

public class FaceAuthenticationFailedException extends RuntimeException {
    public FaceAuthenticationFailedException() { super(); }
    public FaceAuthenticationFailedException(String message) { super(message); }
    public FaceAuthenticationFailedException(String message, Throwable cause) { super(message, cause); }
}

