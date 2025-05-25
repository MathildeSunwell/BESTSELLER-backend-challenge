package com.example.gaming_directory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "games")
public class Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Game name is required")
    @Column(unique = true)
    private String name;
    
    @JsonIgnore
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GamerSkill> gamerSkills = new HashSet<>();

    // Constructor
    public Game() {}

    public Game(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<GamerSkill> getGamerSkills() {
        return gamerSkills;
    }

    public void setGamerSkills(Set<GamerSkill> gamerSkills) {
        this.gamerSkills = gamerSkills;
    }
}