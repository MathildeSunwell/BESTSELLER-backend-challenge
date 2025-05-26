package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GameDTO;
import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
public class GameControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService; // Mock service instead of repository
    
    @Autowired
    private ObjectMapper objectMapper; 
    
    private Game testGame;
    private GameDTO testGameDTO;
    
    @BeforeEach
    void setUp() {
        testGame = new Game("Counter-Strike");
        testGame.setId(1L);
        
        testGameDTO = new GameDTO("Counter-Strike");
    }

// --- Test cases for GET /api/games endpoint ---
    
    @Test 
    void getAllGames_ShouldReturnListOfGames() throws Exception {
        List<Game> games = Arrays.asList(
            new Game("Counter-Strike"),
            new Game("Diablo"),
            new Game("Fortnite")
        );
        when(gameService.getAllGames()).thenReturn(games);

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3)) 
                .andExpect(jsonPath("$[0].name").value("Counter-Strike"))
                .andExpect(jsonPath("$[1].name").value("Diablo"))
                .andExpect(jsonPath("$[2].name").value("Fortnite"));
        
        verify(gameService, times(1)).getAllGames();
    }

// --- Test cases for GET /api/games/{id} endpoint ---

    @Test 
    void getGameById_WhenGameExists_ShouldReturnGame() throws Exception {
        when(gameService.getGameById(1L)).thenReturn(Optional.of(testGame));
        
        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Counter-Strike"));
        
        verify(gameService, times(1)).getGameById(1L);
    }

    @Test 
    void getGameById_WhenGameNotExists_ShouldReturn404() throws Exception {
        when(gameService.getGameById(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound()); 
        
        verify(gameService, times(1)).getGameById(999L);
    }

    @Test 
    void getGameById_WithInvalidId_ShouldReturn400() throws Exception {
        // Service throws IllegalArgumentException for invalid ID
        when(gameService.getGameById(0L)).thenThrow(new IllegalArgumentException("Invalid ID. ID must be a positive number"));
        
        mockMvc.perform(get("/api/games/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ID. ID must be a positive number"));
        
        verify(gameService, times(1)).getGameById(0L);
    }

// --- Test cases for POST /api/games endpoint ---

    @Test 
    void createGame_WithValidData_ShouldCreateGame() throws Exception {
        when(gameService.createGame("Counter-Strike")).thenReturn(testGame);
        
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGameDTO)))
                .andExpect(status().isCreated()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Counter-Strike"));
        
        verify(gameService, times(1)).createGame("Counter-Strike");
    }

    @Test 
    void createGame_WithExistingName_ShouldReturn400() throws Exception {
        // Service throws IllegalArgumentException for duplicate name
        when(gameService.createGame("Counter-Strike"))
                .thenThrow(new IllegalArgumentException("Game name already exists"));
        
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGameDTO)))
                .andExpect(status().isBadRequest()) 
                .andExpect(content().string("Game name already exists"));
        
        verify(gameService, times(1)).createGame("Counter-Strike");
    }

    @Test 
    void createGame_WithEmptyName_ShouldReturn400() throws Exception {
        GameDTO emptyNameDTO = new GameDTO(""); 
        
        // Bean validation should catch this before reaching service
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDTO)))
                .andExpect(status().isBadRequest()); 
        
        // Service should never be called due to validation failure
        verify(gameService, never()).createGame(any());
    }

    @Test 
    void createGame_WithInvalidName_ShouldReturn400() throws Exception {
        GameDTO invalidNameDTO = new GameDTO("A"); // Too short
        
        // Bean validation should catch this before reaching service
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNameDTO)))
                .andExpect(status().isBadRequest()); 
        
        // Service should never be called due to validation failure
        verify(gameService, never()).createGame(any());
    }
}