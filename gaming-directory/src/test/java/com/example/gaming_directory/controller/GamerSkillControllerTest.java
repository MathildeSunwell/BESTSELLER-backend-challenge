package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GamerSkillDTO;
import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.entity.GamerSkill;
import com.example.gaming_directory.enums.Level;
import com.example.gaming_directory.service.GamerSkillService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GamerSkillController.class)
public class GamerSkillControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GamerSkillService gamerSkillService; // Mock service instead of repositories
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Gamer testGamer;
    private Game testGame;
    private GamerSkill testGamerSkill;
    private GamerSkillDTO testGamerSkillDTO;
    
    @BeforeEach
    void setUp() {
        testGamer = new Gamer("Joey", "USA");
        testGamer.setId(1L);
        
        testGame = new Game("Counter-Strike");
        testGame.setId(1L);
        
        testGamerSkill = new GamerSkill(testGamer, testGame, Level.PRO);
        testGamerSkill.setId(1L);
        
        testGamerSkillDTO = new GamerSkillDTO("Joey", "Counter-Strike", Level.PRO);
    }

// --- Test cases for POST /api/gamer-skills endpoint ---

    @Test
    void linkGamerToGame_WithValidData_ShouldCreateLink() throws Exception {
        when(gamerSkillService.linkGamerToGame("Joey", "Counter-Strike", Level.PRO))
                .thenReturn(testGamerSkill);
        
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGamerSkillDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.level").value("PRO"))
                .andExpect(jsonPath("$.gamerName").value("Joey"))
                .andExpect(jsonPath("$.gameName").value("Counter-Strike"));
        
        verify(gamerSkillService, times(1)).linkGamerToGame("Joey", "Counter-Strike", Level.PRO);
    }

    @Test
    void linkGamerToGame_WithNonExistentGamer_ShouldReturn400() throws Exception {
        // Service throws IllegalArgumentException for non-existent gamer
        when(gamerSkillService.linkGamerToGame("NonExistent", "Counter-Strike", Level.PRO))
                .thenThrow(new IllegalArgumentException("Gamer not found with username: NonExistent"));
        
        GamerSkillDTO invalidDTO = new GamerSkillDTO("NonExistent", "Counter-Strike", Level.PRO);
        
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Gamer not found with username: NonExistent"));
        
        verify(gamerSkillService, times(1)).linkGamerToGame("NonExistent", "Counter-Strike", Level.PRO);
    }

    @Test
    void linkGamerToGame_WithNonExistentGame_ShouldReturn400() throws Exception {
        // Service throws IllegalArgumentException for non-existent game
        when(gamerSkillService.linkGamerToGame("Joey", "NonExistentGame", Level.PRO))
                .thenThrow(new IllegalArgumentException("Game not found with name: NonExistentGame"));
        
        GamerSkillDTO invalidDTO = new GamerSkillDTO("Joey", "NonExistentGame", Level.PRO);
        
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Game not found with name: NonExistentGame"));
        
        verify(gamerSkillService, times(1)).linkGamerToGame("Joey", "NonExistentGame", Level.PRO);
    }

    @Test
    void linkGamerToGame_WithEmptyUsername_ShouldReturn400() throws Exception {
        GamerSkillDTO invalidDTO = new GamerSkillDTO("", "Counter-Strike", Level.PRO);
        
        // Bean validation should catch this before reaching service
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
        
        // Service should never be called due to validation failure
        verify(gamerSkillService, never()).linkGamerToGame(any(), any(), any());
    }

    @Test
    void linkGamerToGame_WithNullLevel_ShouldReturn400() throws Exception {
        GamerSkillDTO invalidDTO = new GamerSkillDTO("Joey", "Counter-Strike", null);
        
        // Bean validation should catch this before reaching service
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
        
        // Service should never be called due to validation failure
        verify(gamerSkillService, never()).linkGamerToGame(any(), any(), any());
    }

// --- Test cases for GET /api/gamer-skills/by-level endpoint ---

    @Test
    void getGamersByLevelAndGame_WithValidData_ShouldReturnGamers() throws Exception {
        List<GamerSkill> gamerSkills = Arrays.asList(testGamerSkill);
        when(gamerSkillService.getGamersByLevelAndGame("Counter-Strike", Level.PRO))
                .thenReturn(gamerSkills);
        
        mockMvc.perform(get("/api/gamer-skills/by-level")
                .param("gameName", "Counter-Strike")
                .param("level", "PRO"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].gamerName").value("Joey"))
                .andExpect(jsonPath("$[0].gameName").value("Counter-Strike"))
                .andExpect(jsonPath("$[0].skillLevel").value("PRO"));
        
        verify(gamerSkillService, times(1)).getGamersByLevelAndGame("Counter-Strike", Level.PRO);
    }

    @Test
    void getGamersByLevelAndGame_WithNoResults_ShouldReturn404() throws Exception {
        when(gamerSkillService.getGamersByLevelAndGame("Counter-Strike", Level.INVINCIBLE))
                .thenReturn(Arrays.asList()); // Empty list
        
        mockMvc.perform(get("/api/gamer-skills/by-level")
                .param("gameName", "Counter-Strike")
                .param("level", "INVINCIBLE"))
                .andExpect(status().isNotFound());
        
        verify(gamerSkillService, times(1)).getGamersByLevelAndGame("Counter-Strike", Level.INVINCIBLE);
    }

    @Test
    void getGamersByLevelAndGame_WithEmptyGameName_ShouldReturn400() throws Exception {
        // Service throws IllegalArgumentException for empty game name
        when(gamerSkillService.getGamersByLevelAndGame("", Level.PRO))
                .thenThrow(new IllegalArgumentException("Game name is required"));
        
        mockMvc.perform(get("/api/gamer-skills/by-level")
                .param("gameName", "")
                .param("level", "PRO"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Game name is required"));
        
        verify(gamerSkillService, times(1)).getGamersByLevelAndGame("", Level.PRO);
    }

// --- Test cases for GET /api/gamer-skills/search endpoint ---

    @Test
    void searchGamers_WithAllParameters_ShouldReturnFilteredResults() throws Exception {
        List<GamerSkill> gamerSkills = Arrays.asList(testGamerSkill);
        when(gamerSkillService.searchGamers(Level.PRO, "Counter-Strike", "USA"))
                .thenReturn(gamerSkills);
        
        mockMvc.perform(get("/api/gamer-skills/search")
                .param("level", "PRO")
                .param("gameName", "Counter-Strike")
                .param("country", "USA"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
        
        verify(gamerSkillService, times(1)).searchGamers(Level.PRO, "Counter-Strike", "USA");
    }

    @Test
    void searchGamers_WithNoParameters_ShouldReturnAllGamers() throws Exception {
        List<GamerSkill> gamerSkills = Arrays.asList(testGamerSkill);
        when(gamerSkillService.searchGamers(null, null, null))
                .thenReturn(gamerSkills);
        
        mockMvc.perform(get("/api/gamer-skills/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
        
        verify(gamerSkillService, times(1)).searchGamers(null, null, null);
    }

    @Test
    void searchGamers_WithNoResults_ShouldReturn404() throws Exception {
        when(gamerSkillService.searchGamers(Level.INVINCIBLE, "NonExistentGame", "Mars"))
                .thenReturn(Arrays.asList()); // Empty list
        
        mockMvc.perform(get("/api/gamer-skills/search")
                .param("level", "INVINCIBLE")
                .param("gameName", "NonExistentGame")
                .param("country", "Mars"))
                .andExpect(status().isNotFound());
        
        verify(gamerSkillService, times(1)).searchGamers(Level.INVINCIBLE, "NonExistentGame", "Mars");
    }
}