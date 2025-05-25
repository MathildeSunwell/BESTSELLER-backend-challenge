package com.example.gaming_directory.service;

import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    
    @Autowired
    private GameRepository gameRepository;
    
    // Get all games
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    // Get game by ID
    public Optional<Game> getGameById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID. ID must be a positive number");
        }
        return gameRepository.findById(id);
    }
    
    // Create a new game
    public Game createGame(String gameName) {
        // Validate input
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new IllegalArgumentException("Game name is required");
        }
        
        // Trim whitespace
        gameName = gameName.trim();
        
        // Check if game name already exists
        if (gameRepository.findByName(gameName).isPresent()) {
            throw new IllegalArgumentException("Game name already exists");
        }
        
        // Create and save game
        Game game = new Game(gameName);
        return gameRepository.save(game);
    }
    
    // Check if game exists by name
    public boolean existsByName(String gameName) {
        return gameRepository.findByName(gameName).isPresent();
    }

    // Find game by name
    public Optional<Game> findByName(String gameName) {
        return gameRepository.findByName(gameName);
    }
}