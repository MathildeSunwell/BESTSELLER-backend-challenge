package com.example.gaming_directory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GameDTO {
    
    @NotBlank(message = "Game name is required")
    @Size(min = 2, max = 100, message = "Game name must be between 2 and 100 characters")
    private String name;

    // Constructors
    public GameDTO() {}

    public GameDTO(String name) {
        this.name = name;
    }

    // Get and Set
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
