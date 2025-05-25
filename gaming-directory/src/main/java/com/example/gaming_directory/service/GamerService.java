package com.example.gaming_directory.service;

import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.repository.GamerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GamerService {
    
    @Autowired
    private GamerRepository gamerRepository;
    
    // Get all gamers
    public List<Gamer> getAllGamers() {
        return gamerRepository.findAll();
    }
    
    // Get gamer by ID
    public Optional<Gamer> getGamerById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID. ID must be a positive number");
        }
        return gamerRepository.findById(id);
    }
    
    // Create a new gamer
    public Gamer createGamer(String username, String country) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
        
        // Trim whitespace
        username = username.trim();
        country = country.trim();
        
        // Check if username already exists
        if (gamerRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Create and save gamer
        Gamer gamer = new Gamer(username, country);
        return gamerRepository.save(gamer);
    }
    
    // Check if gamer exists by username
    public boolean existsByUsername(String username) {
        return gamerRepository.findByUsername(username).isPresent();
    }
    
    // Find gamer by username
    public Optional<Gamer> findByUsername(String username) {
        return gamerRepository.findByUsername(username);
    }
}