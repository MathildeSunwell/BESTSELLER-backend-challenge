package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GamerDTO;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.service.GamerService;
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
@RequestMapping("/api/gamers")
@Tag(name = "Gamer Management", description = "API for managing gamers in the gaming directory")
public class GamerController {
    
    @Autowired
    private GamerService gamerService;
    
    // Endpoint to get all gamers
    @Operation(summary = "Get all gamers")
    @GetMapping
    public ResponseEntity<List<Gamer>> getAllGamers() {
        List<Gamer> gamers = gamerService.getAllGamers();
        return ResponseEntity.ok(gamers);
    }

    // Endpoint to create a new gamer
    @Operation(summary = "Create a new gamer")
    @PostMapping
    public ResponseEntity<Gamer> createGamer(@Valid @RequestBody GamerDTO gamerDTO) {

        Gamer savedGamer = gamerService.createGamer(
            gamerDTO.getUsername(),
            gamerDTO.getCountry()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGamer);
    }
    
    // Endpoint to check if a gamer exists by username
    @Operation(summary = "Get gamer by ID")
    @GetMapping("/{id}") 
    public ResponseEntity<Gamer> getGamerById(@PathVariable Long id) {

        Optional<Gamer> gamer = gamerService.getGamerById(id);
        
        // For 404 cases
        return gamer.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}