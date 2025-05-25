package com.example.gaming_directory.dto;

import com.example.gaming_directory.enums.Level;
import jakarta.validation.constraints.NotNull;

public class GamerSkillDTO {
    
    @NotNull(message = "Gamer ID is required")
    private Long gamerId;
    
    @NotNull(message = "Game ID is required")
    private Long gameId;
    
    @NotNull(message = "Level is required")
    private Level level;

    // Constructors
    public GamerSkillDTO() {}

    public GamerSkillDTO(Long gamerId, Long gameId, Level level) {
        this.gamerId = gamerId;
        this.gameId = gameId;
        this.level = level;
    }

    // Getters and Setters
    public Long getGamerId() {
        return gamerId;
    }

    public void setGamerId(Long gamerId) {
        this.gamerId = gamerId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
