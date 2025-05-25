package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GameDTO;
import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Game Management")
public class GameController {
    
    @Autowired
    private GameService gameService;

    // Endpoint to get all games
    @Operation(summary = "Get all games")
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }
    
    // Endpoint to create a new game
    @Operation(summary = "Create a new game")
    @PostMapping
    public ResponseEntity<Game> createGame(@Valid @RequestBody GameDTO gameDTO) {

        Game savedGame = gameService.createGame(gameDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGame);
    }
    
    // Endpoint to check if a game exists by name
    @Operation(summary = "Get game by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {

        Optional<Game> game = gameService.getGameById(id);
        return game.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
}