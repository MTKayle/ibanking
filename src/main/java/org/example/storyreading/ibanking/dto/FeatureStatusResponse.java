package org.example.storyreading.ibanking.dto;

public class FeatureStatusResponse {
    private boolean enabled;

    public FeatureStatusResponse() {}

    public FeatureStatusResponse(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

