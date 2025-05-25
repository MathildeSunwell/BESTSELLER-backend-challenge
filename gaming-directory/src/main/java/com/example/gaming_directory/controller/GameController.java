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
    
    @Operation(summary = "Get all games", description = "Retrieve a list of all games")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of games")
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameRepository.findAll();
        return ResponseEntity.ok(games);
    }
    
    @Operation(summary = "Create a new game", description = "Add a new game to the system")
    @ApiResponse(responseCode = "201", description = "Game created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping
    public ResponseEntity<Object> createGame(@Valid @RequestBody GameDTO gameDTO) {
        try {
            // Check if game name already exists
            if (gameRepository.findByName(gameDTO.getName()).isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Game name already exists");
            }

            Game game = new Game(gameDTO.getName());
            Game savedGame = gameRepository.save(game);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGame);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error creating game: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Get game by ID", description = "Retrieve a specific game by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game found successfully"),
        @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(
            @Parameter(description = "ID of the game to retrieve") 
            @PathVariable Long id) {
        
        Optional<Game> game = gameRepository.findById(id);
        
        if (game.isPresent()) {
            return ResponseEntity.ok(game.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
