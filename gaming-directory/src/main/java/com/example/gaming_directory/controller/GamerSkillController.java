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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    // Endpoint to link a gamer to a game with a skill level
    @Operation(summary = "Link gamer to game", description = "Create or update a gamer's skill level for a specific game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Gamer linked to game successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Gamer or game not found")
    })
    @PostMapping
    public ResponseEntity<Object> linkGamerToGame(@Valid @RequestBody GamerSkillDTO gamerSkillDTO) {
        try {
            // Validate input
            String username = gamerSkillDTO.getUsername();
            String gameName = gamerSkillDTO.getGameName();
            
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username is required");
            }
            
            if (gameName == null || gameName.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Game name is required");
            }
            
            if (gamerSkillDTO.getLevel() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Level is required");
            }
            
            // Trim and validate inputs
            username = username.trim();
            gameName = gameName.trim();
            
            // Validate gamer exists by username
            Optional<Gamer> gamerOpt = gamerRepository.findByUsername(username);
            if (!gamerOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Gamer not found with username: " + username);
            }
            
            // Validate game exists by name
            Optional<Game> gameOpt = gameRepository.findByName(gameName);
            if (!gameOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Game not found with name: " + gameName);
            }
            
            Gamer gamer = gamerOpt.get();
            Game game = gameOpt.get();
            
            // Check if gamer-game combination already exists
            Optional<GamerSkill> existingSkill = gamerSkillRepository
                .findByGamerIdAndGameId(gamer.getId(), game.getId());
            
            GamerSkill gamerSkill;
            String action;
            if (existingSkill.isPresent()) {
                // Update existing skill level
                gamerSkill = existingSkill.get();
                gamerSkill.setLevel(gamerSkillDTO.getLevel());
                action = "updated";
            } else {
                // Create new gamer-game link
                gamerSkill = new GamerSkill(gamer, game, gamerSkillDTO.getLevel());
                action = "created";
            }
            
            GamerSkill savedGamerSkill = gamerSkillRepository.save(gamerSkill);
            
            // Create clean response using Map
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedGamerSkill.getId());
            response.put("username", gamer.getUsername());
            response.put("country", gamer.getCountry());
            response.put("gameName", game.getName());
            response.put("level", gamerSkillDTO.getLevel());
            response.put("message", "Gamer skill " + action + " successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error linking gamer to game: " + e.getMessage());
        }
    }
    
    // Endpoint to get gamers by level and game name
    @Operation(summary = "Get gamers by level and game", description = "Retrieve gamers at a specific level for a specific game (by game name)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved gamers"),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
        @ApiResponse(responseCode = "404", description = "No gamers found matching the criteria")
    })
    @GetMapping("/by-level")
    public ResponseEntity<Object> getGamersByLevelAndGame(
            @Parameter(description = "Game name (e.g., 'Counter-Strike')") @RequestParam String gameName,
            @Parameter(description = "Skill level") @RequestParam Level level) {
        
        try {
            // Basic input validation
            if (gameName == null || gameName.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Game name is required");
            }
            
            if (level == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Level parameter is required");
            }
            
            List<GamerSkill> gamerSkills = gamerSkillRepository.findByGame_NameAndLevel(gameName, level);
            
            if (gamerSkills.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No gamers found with " + level + " level in " + gameName);
            }
            
            return ResponseEntity.ok(gamerSkills);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error searching gamers: " + e.getMessage());
        }
    }
    
    // Endpoint to search gamers based on level, game name, and geography
    @Operation(summary = "Search gamers for matching", description = "Search gamers based on level, game name, and geography")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching gamers"),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
        @ApiResponse(responseCode = "404", description = "No gamers found matching the criteria")
    })
    @GetMapping("/search")
    public ResponseEntity<Object> searchGamers(
            @Parameter(description = "Skill level (optional)") @RequestParam(required = false) Level level,
            @Parameter(description = "Game name (optional, e.g., 'Counter-Strike')") @RequestParam(required = false) String gameName,
            @Parameter(description = "Country (optional)") @RequestParam(required = false) String country) {
        
        try {
            
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
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message.toString());
            }
            
            return ResponseEntity.ok(matchingGamers);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error in gamer search: " + e.getMessage());
        }
    }
}