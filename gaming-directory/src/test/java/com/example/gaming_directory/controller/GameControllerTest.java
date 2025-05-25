package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GameDTO;
import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.repository.GameRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
public class GameControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameRepository gameRepository; 
    
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
        when(gameRepository.findAll()).thenReturn(games);

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3)) 
                .andExpect(jsonPath("$[0].name").value("Counter-Strike"))
                .andExpect(jsonPath("$[1].name").value("Diablo"))
                .andExpect(jsonPath("$[2].name").value("Fortnite"));
        
        verify(gameRepository, times(1)).findAll();
    }

// --- Test cases for GET /api/games/{id} endpoint ---

    @Test 
    void getGameById_WhenGameExists_ShouldReturnGame() throws Exception {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        
        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Counter-Strike"));
        
        verify(gameRepository, times(1)).findById(1L);
    }

    @Test 
    void getGameById_WhenGameNotExists_ShouldReturn404() throws Exception {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/games/999"))
                .andExpect(status().isNotFound()); 
        
        verify(gameRepository, times(1)).findById(999L);
    }

    @Test 
    void getGameById_WithInvalidId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/games/0"))
                .andExpect(status().isBadRequest());
        
        verify(gameRepository, never()).findById(any());
    }

// --- Test cases for POST /api/games endpoint ---

    @Test 
    void createGame_WithValidData_ShouldCreateGame() throws Exception {
        when(gameRepository.findByName("Counter-Strike")).thenReturn(Optional.empty());
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);
        
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGameDTO)))
                .andExpect(status().isCreated()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Counter-Strike"));
        
        verify(gameRepository, times(1)).findByName("Counter-Strike");
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test 
    void createGame_WithExistingName_ShouldReturn409() throws Exception {
        when(gameRepository.findByName("Counter-Strike")).thenReturn(Optional.of(testGame));
        
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGameDTO)))
                .andExpect(status().isConflict()) 
                .andExpect(content().string("Game name already exists"));
        
        verify(gameRepository, times(1)).findByName("Counter-Strike");
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test 
    void createGame_WithEmptyName_ShouldReturn400() throws Exception {
        GameDTO emptyNameDTO = new GameDTO(""); 
        
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDTO)))
                .andExpect(status().isBadRequest()); 
        
        verify(gameRepository, never()).findByName(anyString());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test 
    void createGame_WithInvalidName_ShouldReturn400() throws Exception {
        GameDTO invalidNameDTO = new GameDTO("A"); // Too short
        
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNameDTO)))
                .andExpect(status().isBadRequest()); 
        
        verify(gameRepository, never()).findByName(anyString());
        verify(gameRepository, never()).save(any(Game.class));
    }
}