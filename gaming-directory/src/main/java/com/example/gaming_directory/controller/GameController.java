package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GameDTO;
import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.repository.GameRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
@Tag(name = "Game Management", description = "API for managing games in the gaming directory")
public class GameController {
    
    @Autowired
    private GameRepository gameRepository;
    
    // Endpoint to get all games
    @Operation(summary = "Get all games", description = "Retrieve a list of all games")  
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of games")
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameRepository.findAll();
        return ResponseEntity.ok(games);
    }
    
    // Endpoint to create a new game
    @Operation(summary = "Create a new game", description = "Add a new game to the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Game created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Game name already exists")
    })
    @PostMapping
    public ResponseEntity<Object> createGame(@Valid @RequestBody GameDTO gameDTO) {
        try {
            // Basic input validation
            String gameName = gameDTO.getName();
            
            if (gameName == null || gameName.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Game name is required");
            }
            
            // Trim whitespace
            gameName = gameName.trim();
            
            // Check if game name already exists
            if (gameRepository.findByName(gameName).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Game name already exists");
            }

            // Create and save game
            Game game = new Game(gameName);
            Game savedGame = gameRepository.save(game);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGame);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating game: " + e.getMessage());
        }
    }
    
    // Endpoint to get a game by ID
    @Operation(summary = "Get game by ID", description = "Retrieve a specific game by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game found successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format"),
        @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getGameById(
            @Parameter(description = "ID of the game to retrieve") 
            @PathVariable Long id) {
        
        try {
            // Validate ID
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid ID. ID must be a positive number");
            }
            
            Optional<Game> game = gameRepository.findById(id);
            
            if (game.isPresent()) {
                return ResponseEntity.ok(game.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Game not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving game: " + e.getMessage());
        }
    }
}