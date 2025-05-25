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
    
    // Endpoint to get all gamers
    @Operation(summary = "Get all gamers", description = "Retrieve a list of all registered gamers")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of gamers")
    @GetMapping
    public ResponseEntity<List<Gamer>> getAllGamers() {
        List<Gamer> gamers = gamerRepository.findAll();
        return ResponseEntity.ok(gamers);
    }

    // Endpoint to create a new gamer
    @Operation(summary = "Create a new gamer", description = "Register a new gamer in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Gamer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping
    public ResponseEntity<Object> createGamer(@Valid @RequestBody GamerDTO gamerDTO) {
        try {
            // Basic input validation
            String username = gamerDTO.getUsername();
            String country = gamerDTO.getCountry();
            
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username is required");
            }
            
            if (country == null || country.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Country is required");
            }
            
            // Trim whitespace
            username = username.trim();
            country = country.trim();

            // Check if username already exists
            if (gamerRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
            }

            // Create and save gamer
            Gamer gamer = new Gamer(username, country);
            Gamer savedGamer = gamerRepository.save(gamer);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGamer);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating gamer: " + e.getMessage());
        }
    }
    
    // Endpoint to get a gamer by ID
    @Operation(summary = "Get gamer by ID", description = "Retrieve a specific gamer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gamer found successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format"),
        @ApiResponse(responseCode = "404", description = "Gamer not found")
    })
    @GetMapping("/{id}") 
    public ResponseEntity<Object> getGamerById(
            @Parameter(description = "ID of the gamer to retrieve") 
            @PathVariable Long id) {
        
        try {
            // Validate ID
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid ID. ID must be a positive number");
            }
            
            Optional<Gamer> gamer = gamerRepository.findById(id);
            
            if (gamer.isPresent()) {
                return ResponseEntity.ok(gamer.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Gamer not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving gamer: " + e.getMessage());
        }
    }
}