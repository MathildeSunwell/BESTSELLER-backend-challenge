package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GamerDTO;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.service.GamerService;
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

@WebMvcTest(GamerController.class)
public class GamerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GamerService gamerService; // Mock service instead of repository
    
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
    
    @Test 
    void getAllGamers_ShouldReturnListOfGamers() throws Exception {
        List<Gamer> gamers = Arrays.asList(
            new Gamer("Joey", "USA"),
            new Gamer("Chandler", "USA"),
            new Gamer("Ross", "Canada")
        );
        when(gamerService.getAllGamers()).thenReturn(gamers);

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
        
        verify(gamerService, times(1)).getAllGamers();
    }

// --- Test cases for GET /api/gamers/{id} endpoint ---

    @Test 
    void getGamerById_WhenGamerExists_ShouldReturnGamer() throws Exception {
        when(gamerService.getGamerById(1L)).thenReturn(Optional.of(testGamer));
        
        mockMvc.perform(get("/api/gamers/1"))
                .andExpect(status().isOk()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("TestGamer1"))
                .andExpect(jsonPath("$.country").value("USA"));
        
        verify(gamerService, times(1)).getGamerById(1L);
    }

    @Test 
    void getGamerById_WhenGamerNotExists_ShouldReturn404() throws Exception {
        when(gamerService.getGamerById(999L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/gamers/999"))
                .andExpect(status().isNotFound()); 
        
        verify(gamerService, times(1)).getGamerById(999L);
    }

    @Test 
    void getGamerById_WithInvalidId_ShouldReturn400() throws Exception {
        // Service throws IllegalArgumentException for invalid ID
        when(gamerService.getGamerById(0L)).thenThrow(new IllegalArgumentException("Invalid ID. ID must be a positive number"));
        
        mockMvc.perform(get("/api/gamers/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ID. ID must be a positive number"));
        
        verify(gamerService, times(1)).getGamerById(0L);
    }

// --- Test cases for POST /api/gamers endpoint ---

    @Test 
    void createGamer_WithValidData_ShouldCreateGamer() throws Exception {
        when(gamerService.createGamer("TestGamer1", "USA")).thenReturn(testGamer);
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGamerDTO)))
                .andExpect(status().isCreated()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("TestGamer1"))
                .andExpect(jsonPath("$.country").value("USA"));
        
        verify(gamerService, times(1)).createGamer("TestGamer1", "USA");
    }

    @Test 
    void createGamer_WithExistingUsername_ShouldReturn400() throws Exception {
        // Service throws IllegalArgumentException for duplicate username
        when(gamerService.createGamer("TestGamer1", "USA"))
                .thenThrow(new IllegalArgumentException("Username already exists"));
        
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGamerDTO)))
                .andExpect(status().isBadRequest()) 
                .andExpect(content().string("Username already exists"));
        
        verify(gamerService, times(1)).createGamer("TestGamer1", "USA");
    }

    @Test 
    void createGamer_WithInvalidUsername_ShouldReturn400() throws Exception {
        GamerDTO invalidGamerDTO = new GamerDTO("ab", "USA"); // Too short
        
        // Bean validation should catch this before reaching service
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidGamerDTO)))
                .andExpect(status().isBadRequest()); 

        // Service should never be called due to validation failure
        verify(gamerService, never()).createGamer(any(), any());
    }

    @Test 
    void createGamer_WithEmptyUsername_ShouldReturn400() throws Exception {
        GamerDTO emptyUsernameDTO = new GamerDTO("", "USA"); 
        
        // Bean validation should catch this before reaching service
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyUsernameDTO)))
                .andExpect(status().isBadRequest()); 
        
        verify(gamerService, never()).createGamer(any(), any());
    }

    @Test 
    void createGamer_WithEmptyCountry_ShouldReturn400() throws Exception {
        GamerDTO emptyCountryDTO = new GamerDTO("ValidUsername", ""); 
        
        // Bean validation should catch this before reaching service
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyCountryDTO)))
                .andExpect(status().isBadRequest()); 
        
        verify(gamerService, never()).createGamer(any(), any());
    }

    @Test 
    void createGamer_WithInvalidCountry_ShouldReturn400() throws Exception {
        GamerDTO invalidCountryDTO = new GamerDTO("ValidUser", "A"); // Too short
        
        // Bean validation should catch this before reaching service
        mockMvc.perform(post("/api/gamers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCountryDTO)))
                .andExpect(status().isBadRequest()); 
        
        verify(gamerService, never()).createGamer(any(), any());
    }
}