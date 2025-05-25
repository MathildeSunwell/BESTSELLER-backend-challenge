package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GamerDTO;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.service.GamerService;
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
    private GamerService gamerService;
    
    // Endpoint to get all gamers
    @Operation(summary = "Get all gamers", description = "Retrieve a list of all registered gamers")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of gamers")
    @GetMapping
    public ResponseEntity<List<Gamer>> getAllGamers() {
        List<Gamer> gamers = gamerService.getAllGamers();
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
    public ResponseEntity<Gamer> createGamer(@Valid @RequestBody GamerDTO gamerDTO) {
        // Input validation is handled by @Valid and GlobalExceptionHandler
        // Business logic is handled by the service layer
        Gamer savedGamer = gamerService.createGamer(
            gamerDTO.getUsername(),
            gamerDTO.getCountry()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGamer);
    }
    
    // Endpoint to get a gamer by ID
    @Operation(summary = "Get gamer by ID", description = "Retrieve a specific gamer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gamer found successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID format"),
        @ApiResponse(responseCode = "404", description = "Gamer not found")
    })
    @GetMapping("/{id}") 
    public ResponseEntity<Gamer> getGamerById(
            @Parameter(description = "ID of the gamer to retrieve") 
            @PathVariable Long id) {
        
        // Input validation and business logic handled by service
        Optional<Gamer> gamer = gamerService.getGamerById(id);
        
        if (gamer.isPresent()) {
            return ResponseEntity.ok(gamer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}