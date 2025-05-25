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
        testGamer = new Gamer("TestGamer1", "USA");
        testGamer.setId(1L);
        
        testGamerDTO = new GamerDTO("TestGamer1", "USA");
    }

// --- Test cases for GET /api/gamers endpoint ---
    
// Return all gamers
    @Test 
    void getAllGamers_ShouldReturnListOfGamers() throws Exception {
        List<Gamer> gamers = Arrays.asList(
            new Gamer("Joey", "USA"),
            new Gamer("Chandler", "USA"),
            new Gamer("Ross", "Canada")
        );
        when(gamerRepository.findAll()).thenReturn(gamers);

        mockMvc.perform(get("/api/gamers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3)) 
                .andExpect(jsonPath("$[0].username").value("Joey"))
                .andExpect(jsonPath("$[0].country").value("USA"))
                .andExpect(jsonPath("$[1].username").value("Chandler"))
                .andExpect(jsonPath("$[1].country").value("USA"))
                .andExpect(jsonPath("$[2].username").value("Ross"))
                .andExpect(jsonPath("$[2].country").value("Canada"));
        
        // Verify repository was called once
        verify(gamerRepository, times(1)).findAll();
    }

// --- Test cases for GET /api/gamers/{id} endpoint ---

// Return gamer by ID
    @Test 
    void getGamerById_WhenGamerExists_ShouldReturnGamer() throws Exception {
        when(gamerRepository.findById(1L)).thenReturn(Optional.of(testGamer));
        
        mockMvc.perform(get("/api/gamers/1"))
                .andExpect(status().isOk()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("TestGamer1"))
                .andExpect(jsonPath("$.country").value("USA"));
        
        verify(gamerRepository, times(1)).findById(1L);
    }

// Return 404 when gamer not found
    @Test 
    void getGamerById_WhenGamerNotExists_ShouldReturn404() throws Exception {
        when(gamerRepository.findById(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/gamers/999"))
                .andExpect(status().isNotFound()); 
        
        verify(gamerRepository, times(1)).findById(999L);
    }

// --- Test cases for POST /api/gamers endpoint ---

// Create a new gamer
    @Test 
    void createGamer_WithValidData_ShouldCreateGamer() throws Exception {
        when(gamerRepository.findByUsername("TestGamer1")).thenReturn(Optional.empty());
        when(gamerRepository.save(any(Gamer.class))).thenReturn(testGamer);
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGamerDTO)))
                .andExpect(status().isCreated()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("TestGamer1"))
                .andExpect(jsonPath("$.country").value("USA"));
        
        // Verify repository interactions
        verify(gamerRepository, times(1)).findByUsername("TestGamer1");
        verify(gamerRepository, times(1)).save(any(Gamer.class));
    }

// Return 400 when username already exists
    @Test 
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

// Return 400 when username is invalid (too short)
    @Test 
    void createGamer_WithInvalidUsername_ShouldReturn400() throws Exception {
        GamerDTO invalidGamerDTO = new GamerDTO("ab", "USA");
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidGamerDTO)))
                .andExpect(status().isBadRequest()); 

        // Repository should never be called due to validation failure
        verify(gamerRepository, never()).findByUsername(anyString());
        verify(gamerRepository, never()).save(any(Gamer.class));
    }

// Return 400 when username is empty
    @Test 
    void createGamer_WithEmptyUsername_ShouldReturn400() throws Exception {
        GamerDTO emptyUsernameDTO = new GamerDTO("", "USA"); 
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyUsernameDTO)))
                .andExpect(status().isBadRequest()); 
        
        verify(gamerRepository, never()).findByUsername(anyString());
        verify(gamerRepository, never()).save(any(Gamer.class));
    }

// Return 400 when country is empty
    @Test 
    void createGamer_WithEmptyCountry_ShouldReturn400() throws Exception {
        GamerDTO emptyCountryDTO = new GamerDTO("ValidUsername", ""); 
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyCountryDTO)))
                .andExpect(status().isBadRequest()); 
        
        verify(gamerRepository, never()).findByUsername(anyString());
        verify(gamerRepository, never()).save(any(Gamer.class));
    }

// Return 400 when country is too short
    @Test 
    void createGamer_WithInvalidCountry_ShouldReturn400() throws Exception {
        GamerDTO invalidCountryDTO = new GamerDTO("ValidUser", "A"); 
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCountryDTO)))
                .andExpect(status().isBadRequest()); 
        
        verify(gamerRepository, never()).findByUsername(anyString());
        verify(gamerRepository, never()).save(any(Gamer.class));
    }
}