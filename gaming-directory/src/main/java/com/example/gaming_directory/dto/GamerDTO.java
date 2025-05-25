package com.example.gaming_directory.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public class GamerDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 60, message = "Country must be between 2 and 60 characters")
    private String country;

    // Constructors
    public GamerDTO() {}

    public GamerDTO(String username, String country) {
        this.username = username;
        this.country = country;
    }

    // Get and Set
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}