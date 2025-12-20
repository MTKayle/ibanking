package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCccdRequest {
    @NotBlank(message = "CCCD number is required")
    private String cccdNumber;

    public String getCccdNumber() {
        return cccdNumber;
    }
}

