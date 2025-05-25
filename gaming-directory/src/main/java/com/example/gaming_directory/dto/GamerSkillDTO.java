package com.example.gaming_directory.dto;

import com.example.gaming_directory.enums.Level;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class GamerSkillDTO {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Game name is required")
    private String gameName;
    
    @NotNull(message = "Level is required")
    private Level level;

    // Constructors
    public GamerSkillDTO() {}

    public GamerSkillDTO(String username, String gameName, Level level) {
        this.username = username;
        this.gameName = gameName;
        this.level = level;
    }

    // Get and Set
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName != null ? gameName.trim() : null;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}