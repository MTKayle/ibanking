package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePhoneRequest {
    @NotBlank(message = "Phone number is required")
    private String phone;

    public String getPhone() {
        return phone;
    }
}

