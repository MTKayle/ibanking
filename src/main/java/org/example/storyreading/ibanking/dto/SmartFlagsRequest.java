package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotNull;

public class SmartFlagsRequest {


    private Boolean smartEkycEnabled;


    private Boolean faceRecognitionEnabled;

    public Boolean getSmartEkycEnabled() {
        return smartEkycEnabled;
    }

    public void setSmartEkycEnabled(Boolean smartEkycEnabled) {
        this.smartEkycEnabled = smartEkycEnabled;
    }

    public Boolean getFaceRecognitionEnabled() {
        return faceRecognitionEnabled;
    }

    public void setFaceRecognitionEnabled(Boolean faceRecognitionEnabled) {
        this.faceRecognitionEnabled = faceRecognitionEnabled;
    }
}

