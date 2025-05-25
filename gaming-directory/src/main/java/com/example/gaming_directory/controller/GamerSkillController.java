package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GamerSkillDTO;
import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.entity.GamerSkill;
import com.example.gaming_directory.enums.Level;
import com.example.gaming_directory.repository.GameRepository;
import com.example.gaming_directory.repository.GamerRepository;
import com.example.gaming_directory.repository.GamerSkillRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gamer-skills")
@Tag(name = "Gamer Skills Management", description = "API for linking gamers to games with skill levels")
public class GamerSkillController {
    
    @Autowired
    private GamerSkillRepository gamerSkillRepository;
    
    @Autowired
    private GamerRepository gamerRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Operation(summary = "Link gamer to game", description = "Create or update a gamer's skill level for a specific game")
    @ApiResponse(responseCode = "201", description = "Gamer linked to game successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping
    public ResponseEntity<Object> linkGamerToGame(@Valid @RequestBody GamerSkillDTO gamerSkillDTO) {
        try {
            // Validate gamer exists
            Optional<Gamer> gamerOpt = gamerRepository.findById(gamerSkillDTO.getGamerId());
            if (!gamerOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Gamer not found");
            }
            
            // Validate game exists
            Optional<Game> gameOpt = gameRepository.findById(gamerSkillDTO.getGameId());
            if (!gameOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Game not found");
            }
            
            Gamer gamer = gamerOpt.get();
            Game game = gameOpt.get();
            
            // Check if gamer-game combination already exists
            Optional<GamerSkill> existingSkill = gamerSkillRepository
                .findByGamerIdAndGameId(gamerSkillDTO.getGamerId(), gamerSkillDTO.getGameId());
            
            GamerSkill gamerSkill;
            if (existingSkill.isPresent()) {
                // Update existing skill level
                gamerSkill = existingSkill.get();
                gamerSkill.setLevel(gamerSkillDTO.getLevel());
            } else {
                // Create new gamer-game link
                gamerSkill = new GamerSkill(gamer, game, gamerSkillDTO.getLevel());
            }
            
            GamerSkill savedGamerSkill = gamerSkillRepository.save(gamerSkill);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGamerSkill);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error linking gamer to game: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Get gamers by level and game", description = "Retrieve gamers at a specific level for a specific game (by game name)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved gamers")
    @ApiResponse(responseCode = "404", description = "No gamers found matching the criteria")
    @GetMapping("/by-level")
    public ResponseEntity<Object> getGamersByLevelAndGame(
            @Parameter(description = "Game name (e.g., 'Counter-Strike')") @RequestParam String gameName,
            @Parameter(description = "Skill level") @RequestParam Level level) {
        
        List<GamerSkill> gamerSkills = gamerSkillRepository.findByGame_NameAndLevel(gameName, level);
        
        if (gamerSkills.isEmpty()) {
            return ResponseEntity.status(404)
                .body("No gamers found with " + level + " level in " + gameName);
        }
        
        return ResponseEntity.ok(gamerSkills);
    }
    
    @Operation(summary = "Search gamers for matching", description = "Search gamers based on level, game name, and geography")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching gamers")
    @ApiResponse(responseCode = "404", description = "No gamers found matching the criteria")
    @GetMapping("/search")
    public ResponseEntity<Object> searchGamers(
            @Parameter(description = "Skill level (optional)") @RequestParam(required = false) Level level,
            @Parameter(description = "Game name (optional, e.g., 'Counter-Strike')") @RequestParam(required = false) String gameName,
            @Parameter(description = "Country (optional)") @RequestParam(required = false) String country) {
        
        List<GamerSkill> matchingGamers = gamerSkillRepository
            .findByLevelAndGameNameAndCountry(level, gameName, country);
        
        if (matchingGamers.isEmpty()) {
            StringBuilder message = new StringBuilder("No gamers found");
            
            if (level != null || gameName != null || country != null) {
                message.append(" matching criteria:");
                if (level != null) message.append(" level=").append(level);
                if (gameName != null) message.append(" game=").append(gameName);
                if (country != null) message.append(" country=").append(country);
            }
            
            return ResponseEntity.status(404).body(message.toString());
        }
        
        return ResponseEntity.ok(matchingGamers);
    }
}