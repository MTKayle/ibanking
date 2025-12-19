package org.example.storyreading.ibanking.dto;

public class FaceComparisonResponse {
    private boolean success;
    private String message;
    private double confidence;
    private boolean matched;

    public FaceComparisonResponse() {
    }

    public FaceComparisonResponse(boolean success, String message, double confidence, boolean matched) {
        this.success = success;
        this.message = message;
        this.confidence = confidence;
        this.matched = matched;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}

