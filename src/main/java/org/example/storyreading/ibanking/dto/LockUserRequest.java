package org.example.storyreading.ibanking.dto;

public class LockUserRequest {

    private Boolean locked;

    public LockUserRequest() {
    }

    public LockUserRequest(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}

