package com.example.gaming_directory.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "gamers")
public class Gamer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String username;


    @NotBlank(message = "Country is required")
    private String country;

    // "Don't load the skills until I ask for them" - lazy loading 
    @JsonIgnore
    @OneToMany(mappedBy = "gamer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GamerSkill> gamerSkills = new HashSet<>();


    // Constructor
    public Gamer() {}

    public Gamer(String username, String country) {
        this.username = username;
        this.country = country;
    }

    // Get and Set
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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

    public Set<GamerSkill> getGamerSkills() {
        return gamerSkills;
    }

    public void setGamerSkills(Set<GamerSkill> gamerSkills) {
        this.gamerSkills = gamerSkills;
    }

    // Custom JSON field for clean game skills display
    public List<String> getGames() {
        return gamerSkills.stream()
            .map(skill -> skill.getGame().getName() + " (" + skill.getLevel() + ")")
            .collect(Collectors.toList());
}
}
