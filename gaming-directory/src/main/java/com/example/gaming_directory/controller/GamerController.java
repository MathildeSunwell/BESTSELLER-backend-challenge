package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GamerDTO;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.repository.GamerRepository;
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
@RequestMapping("/api/gamers")
@Tag(name = "Gamer Management", description = "API for managing gamers in the gaming directory")
public class GamerController {
    
    @Autowired
    private GamerRepository gamerRepository;
    
    // GET endpoint /api/gamers - Get all gamers
    @Operation(summary = "Get all gamers", description = "Retrieve a list of all registered gamers")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of gamers")
    @GetMapping
    public ResponseEntity<List<Gamer>> getAllGamers() {
        List<Gamer> gamers = gamerRepository.findAll();
        return ResponseEntity.ok(gamers);
    }

    
    // POST endpoint /api/gamers - Create a new gamer
    @Operation(summary = "Create a new gamer", description = "Register a new gamer in the system")
    @ApiResponse(responseCode = "201", description = "Gamer created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping
    public ResponseEntity<Object> createGamer(@Valid @RequestBody GamerDTO gamerDTO) {
        try {
            // Check if username already exists
            if (gamerRepository.findByUsername(gamerDTO.getUsername()).isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Username already exists");
            }

            // Convert DTO to Entity 
            Gamer gamer = new Gamer(gamerDTO.getUsername(), gamerDTO.getCountry());

            Gamer savedGamer = gamerRepository.save(gamer);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGamer);

    } catch (Exception e) {
        return ResponseEntity.badRequest()
            .body("Error creating gamer: " + e.getMessage());
    }
    }
    
    // GET endpoint /api/gamers/{id} - Get gamer by ID
    @Operation(summary = "Get gamer by ID", description = "Retrieve a specific gamer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gamer found successfully"),
        @ApiResponse(responseCode = "404", description = "Gamer not found")
    })
    @GetMapping("/{id}") 
    public ResponseEntity<Gamer> getGamerById(
            @Parameter(description = "ID of the gamer to retrieve") 
            @PathVariable Long id) {
        
        // Search for gamer by ID - returns Optional<Gamer>
        Optional<Gamer> gamer = gamerRepository.findById(id);
        
        if (gamer.isPresent()) {
            return ResponseEntity.ok(gamer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}