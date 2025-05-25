package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GamerDTO;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.repository.GamerRepository;
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

@WebMvcTest(GamerController.class)
public class GamerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GamerRepository gamerRepository; 
    
    @Autowired
    private ObjectMapper objectMapper; 
    
    private Gamer testGamer;
    private GamerDTO testGamerDTO;
    
    @BeforeEach
    void setUp() {
 
        testGamer = new Gamer("TestGamer1");
        testGamer.setId(1L);
        
        testGamerDTO = new GamerDTO("TestGamer1");
    }

// --- Test cases for GET /api/gamers endpoint ---
    
    @Test // Return all gamers
    void getAllGamers_ShouldReturnListOfGamers() throws Exception {
        List<Gamer> gamers = Arrays.asList(
            new Gamer("Joey"),
            new Gamer("Chandler"),
            new Gamer("Ross")
        );
        when(gamerRepository.findAll()).thenReturn(gamers);

        mockMvc.perform(get("/api/gamers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3)) 
                .andExpect(jsonPath("$[0].username").value("Joey"))
                .andExpect(jsonPath("$[1].username").value("Chandler"))
                .andExpect(jsonPath("$[2].username").value("Ross"));
        
        // Verify repository was called once
        verify(gamerRepository, times(1)).findAll();
    }

// --- Test cases for GET /api/gamers/{id} endpoint ---
    
    @Test // Return gamer by ID
    void getGamerById_WhenGamerExists_ShouldReturnGamer() throws Exception {
        when(gamerRepository.findById(1L)).thenReturn(Optional.of(testGamer));
        
        mockMvc.perform(get("/api/gamers/1"))
                .andExpect(status().isOk()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("TestGamer1"));
        
        verify(gamerRepository, times(1)).findById(1L);
    }

    @Test // Return 404 when gamer not found
    void getGamerById_WhenGamerNotExists_ShouldReturn404() throws Exception {
        when(gamerRepository.findById(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/gamers/999"))
                .andExpect(status().isNotFound()); 
        
        verify(gamerRepository, times(1)).findById(999L);
    }


// --- Test cases for POST /api/gamers endpoint ---
    
    @Test // Create a new gamer
    void createGamer_WithValidData_ShouldCreateGamer() throws Exception {
        when(gamerRepository.findByUsername("TestGamer1")).thenReturn(Optional.empty());
        when(gamerRepository.save(any(Gamer.class))).thenReturn(testGamer);
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGamerDTO)))
                .andExpect(status().isCreated()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("TestGamer1"));
        
        // Verify repository interactions
        verify(gamerRepository, times(1)).findByUsername("TestGamer1");
        verify(gamerRepository, times(1)).save(any(Gamer.class));
    }
    
    @Test // Return 400 when username already exists
    void createGamer_WithExistingUsername_ShouldReturn400() throws Exception {
        when(gamerRepository.findByUsername("TestGamer1")).thenReturn(Optional.of(testGamer));
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGamerDTO)))
                .andExpect(status().isBadRequest()) 
                .andExpect(content().string("Username already exists"));
        
        // Verify repository was checked for username, but save was never called
        verify(gamerRepository, times(1)).findByUsername("TestGamer1");
        verify(gamerRepository, never()).save(any(Gamer.class));
    }
    
    @Test // Return 400 when username is invalid
    void createGamer_WithInvalidUsername_ShouldReturn400() throws Exception {
        GamerDTO invalidGamerDTO = new GamerDTO("ab"); 
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidGamerDTO)))
                .andExpect(status().isBadRequest()); 

        // Repository should never be called due to validation failure
        verify(gamerRepository, never()).findByUsername(anyString());
        verify(gamerRepository, never()).save(any(Gamer.class));
    }
    
    @Test // Return 400 when username is empty
    void createGamer_WithEmptyUsername_ShouldReturn400() throws Exception {
        GamerDTO emptyGamerDTO = new GamerDTO(""); 
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyGamerDTO)))
                .andExpect(status().isBadRequest()); 
        
        // Repository should never be called due to validation failure
        verify(gamerRepository, never()).findByUsername(anyString());
        verify(gamerRepository, never()).save(any(Gamer.class));
    }
}
