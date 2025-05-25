package com.example.gaming_directory.entity;

import com.example.gaming_directory.enums.Level;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "gamer_skills", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"gamer_id", "game_id"}))
public class GamerSkill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore  // Hide the full nested gamer object from JSON
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamer_id", nullable = false)
    @NotNull
    private Gamer gamer;
    
    @JsonIgnore  
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", nullable = false)
    @NotNull
    private Game game;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private Level level;

    // Constructors
    public GamerSkill() {}

    public GamerSkill(Gamer gamer, Game game, Level level) {
        this.gamer = gamer;
        this.game = game;
        this.level = level;
    }

    // Get and Set
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Gamer getGamer() {
        return gamer;
    }

    public void setGamer(Gamer gamer) {
        this.gamer = gamer;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    // Clean JSON representation - these will be the only fields shown in JSON
    public String getGamerName() {
        return gamer.getUsername();
    }

    public String getGamerCountry() {
        return gamer.getCountry();
    }

    public String getGameName() {
        return game.getName();
    }

    public String getSkillLevel() {
        return level.toString();
    }
}