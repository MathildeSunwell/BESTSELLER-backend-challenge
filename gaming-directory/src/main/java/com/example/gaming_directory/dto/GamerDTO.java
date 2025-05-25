package com.example.gaming_directory.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public class GamerDTO {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    // Constructors
    public GamerDTO() {}

    public GamerDTO(String username) {
        this.username = username;
    }

    // Get and Set
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
