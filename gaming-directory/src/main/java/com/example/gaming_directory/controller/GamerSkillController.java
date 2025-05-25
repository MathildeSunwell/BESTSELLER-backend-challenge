package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GamerSkillDTO;
import com.example.gaming_directory.entity.GamerSkill;
import com.example.gaming_directory.enums.Level;
import com.example.gaming_directory.service.GamerSkillService;
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

import java.util.List;

@RestController
@RequestMapping("/api/gamer-skills")
@Tag(name = "Gamer Skills Management", description = "API for linking gamers to games with skill levels")
public class GamerSkillController {
    
    @Autowired
    private GamerSkillService gamerSkillService;
    
    // Endpoint to link a gamer to a game with a skill level
    @Operation(summary = "Link gamer to game", description = "Create or update a gamer's skill level for a specific game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Gamer linked to game successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Gamer or game not found")
    })
    @PostMapping
    public ResponseEntity<GamerSkill> linkGamerToGame(@Valid @RequestBody GamerSkillDTO gamerSkillDTO) {
        // Input validation is handled by @Valid and GlobalExceptionHandler
        // Business logic is handled by the service layer
        GamerSkill savedGamerSkill = gamerSkillService.linkGamerToGame(
            gamerSkillDTO.getUsername(),
            gamerSkillDTO.getGameName(),
            gamerSkillDTO.getLevel()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGamerSkill);
    }
    
    // Endpoint to get gamers by level and game name
    @Operation(summary = "Get gamers by level and game", description = "Retrieve gamers at a specific level for a specific game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved gamers"),
        @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
        @ApiResponse(responseCode = "404", description = "No gamers found matching the criteria")
    })
    @GetMapping("/by-level")
    public ResponseEntity<List<GamerSkill>> getGamersByLevelAndGame(
            @Parameter(description = "Game name") @RequestParam String gameName,
            @Parameter(description = "Skill level") @RequestParam Level level) {
        
        List<GamerSkill> gamerSkills = gamerSkillService.getGamersByLevelAndGame(gameName, level);
        
        if (gamerSkills.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(gamerSkills);
    }
    
    // Endpoint to search gamers based on level, game name, and geography
    @Operation(summary = "Search gamers for matching", description = "Search gamers based on level, game name, and geography")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching gamers"),
        @ApiResponse(responseCode = "404", description = "No gamers found matching the criteria")
    })
    @GetMapping("/search")
    public ResponseEntity<List<GamerSkill>> searchGamers(
            @Parameter(description = "Skill level (optional)") @RequestParam(required = false) Level level,
            @Parameter(description = "Game name (optional)") @RequestParam(required = false) String gameName,
            @Parameter(description = "Country (optional)") @RequestParam(required = false) String country) {
        
        List<GamerSkill> matchingGamers = gamerSkillService.searchGamers(level, gameName, country);
        
        if (matchingGamers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(matchingGamers);
    }
}