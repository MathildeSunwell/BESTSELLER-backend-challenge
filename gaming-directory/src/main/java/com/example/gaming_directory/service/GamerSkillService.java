package com.example.gaming_directory.service;

import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.entity.GamerSkill;
import com.example.gaming_directory.enums.Level;
import com.example.gaming_directory.repository.GameRepository;
import com.example.gaming_directory.repository.GamerRepository;
import com.example.gaming_directory.repository.GamerSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GamerSkillService {
    
    @Autowired
    private GamerSkillRepository gamerSkillRepository;
    
    @Autowired
    private GamerRepository gamerRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    // Create a new gamer-game link with skill level
    public GamerSkill linkGamerToGame(String username, String gameName, Level level) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new IllegalArgumentException("Game name is required");
        }
        
        if (level == null) {
            throw new IllegalArgumentException("Level is required");
        }
        
        // Trim inputs
        username = username.trim();
        gameName = gameName.trim();
        
        // Find gamer and game
        Optional<Gamer> gamerOpt = gamerRepository.findByUsername(username);
        Optional<Game> gameOpt = gameRepository.findByName(gameName);
        
        if (gamerOpt.isEmpty()) {
            throw new IllegalArgumentException("Gamer not found with username: " + username);
        }
        
        if (gameOpt.isEmpty()) {
            throw new IllegalArgumentException("Game not found with name: " + gameName);
        }
        
        Gamer gamer = gamerOpt.get();
        Game game = gameOpt.get();
        
        // Check if gamer-game combination already exists
        Optional<GamerSkill> existingSkill = gamerSkillRepository
            .findByGamerIdAndGameId(gamer.getId(), game.getId());
        
        GamerSkill gamerSkill;
        if (existingSkill.isPresent()) {
            // Update existing skill level
            gamerSkill = existingSkill.get();
            gamerSkill.setLevel(level);
        } else {
            // Create new gamer-game link
            gamerSkill = new GamerSkill(gamer, game, level);
        }
        
        return gamerSkillRepository.save(gamerSkill);
    }
    
    // Get gamers by level and game name
    public List<GamerSkill> getGamersByLevelAndGame(String gameName, Level level) {
        // Validate input
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new IllegalArgumentException("Game name is required");
        }
        
        if (level == null) {
            throw new IllegalArgumentException("Level parameter is required");
        }
        
        return gamerSkillRepository.findByGame_NameAndLevel(gameName, level);
    }
    
    // Search gamers based on level, game name, and geography
    public List<GamerSkill> searchGamers(Level level, String gameName, String country) {
        return gamerSkillRepository.findByLevelAndGameNameAndCountry(level, gameName, country);
    }
    
    // Get all gamer skills
    public List<GamerSkill> getAllGamerSkills() {
        return gamerSkillRepository.findAll();
    }
    
    // Check if gamer exists by username
    public boolean gamerExists(String username) {
        return gamerRepository.findByUsername(username).isPresent();
    }
    
    // Check if game exists by name
    public boolean gameExists(String gameName) {
        return gameRepository.findByName(gameName).isPresent();
    }

    // Get gamer skill by ID
    public Optional<GamerSkill> getGamerSkillById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID. ID must be a positive number");
        }
        return gamerSkillRepository.findById(id);
    }
}