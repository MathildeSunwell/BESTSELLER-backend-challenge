package com.example.gaming_directory.controller;

import com.example.gaming_directory.dto.GamerSkillDTO;
import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.entity.GamerSkill;
import com.example.gaming_directory.enums.Level;
import com.example.gaming_directory.repository.GameRepository;
import com.example.gaming_directory.repository.GamerRepository;
import com.example.gaming_directory.repository.GamerSkillRepository;
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

@WebMvcTest(GamerSkillController.class)
public class GamerSkillControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GamerSkillRepository gamerSkillRepository;
    
    @MockitoBean
    private GamerRepository gamerRepository;
    
    @MockitoBean
    private GameRepository gameRepository;
    
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
        when(gamerRepository.findByUsername("Joey")).thenReturn(Optional.of(testGamer));
        when(gameRepository.findByName("Counter-Strike")).thenReturn(Optional.of(testGame));
        when(gamerSkillRepository.findByGamerIdAndGameId(1L, 1L)).thenReturn(Optional.empty());
        when(gamerSkillRepository.save(any(GamerSkill.class))).thenReturn(testGamerSkill);
        
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGamerSkillDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Joey"))
                .andExpect(jsonPath("$.gameName").value("Counter-Strike"))
                .andExpect(jsonPath("$.level").value("PRO"));
        
        verify(gamerRepository, times(1)).findByUsername("Joey");
        verify(gameRepository, times(1)).findByName("Counter-Strike");
        verify(gamerSkillRepository, times(1)).save(any(GamerSkill.class));
    }

    @Test
    void linkGamerToGame_WithExistingLink_ShouldUpdateLevel() throws Exception {
        when(gamerRepository.findByUsername("Joey")).thenReturn(Optional.of(testGamer));
        when(gameRepository.findByName("Counter-Strike")).thenReturn(Optional.of(testGame));
        when(gamerSkillRepository.findByGamerIdAndGameId(1L, 1L)).thenReturn(Optional.of(testGamerSkill));
        when(gamerSkillRepository.save(any(GamerSkill.class))).thenReturn(testGamerSkill);
        
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGamerSkillDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Gamer skill updated successfully"));
        
        verify(gamerSkillRepository, times(1)).save(any(GamerSkill.class));
    }

    @Test
    void linkGamerToGame_WithNonExistentGamer_ShouldReturn404() throws Exception {
        when(gamerRepository.findByUsername("NonExistent")).thenReturn(Optional.empty());
        
        GamerSkillDTO invalidDTO = new GamerSkillDTO("NonExistent", "Counter-Strike", Level.PRO);
        
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Gamer not found with username: NonExistent"));
        
        verify(gamerRepository, times(1)).findByUsername("NonExistent");
        verify(gamerSkillRepository, never()).save(any(GamerSkill.class));
    }

    @Test
    void linkGamerToGame_WithNonExistentGame_ShouldReturn404() throws Exception {
        when(gamerRepository.findByUsername("Joey")).thenReturn(Optional.of(testGamer));
        when(gameRepository.findByName("NonExistentGame")).thenReturn(Optional.empty());
        
        GamerSkillDTO invalidDTO = new GamerSkillDTO("Joey", "NonExistentGame", Level.PRO);
        
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Game not found with name: NonExistentGame"));
        
        verify(gameRepository, times(1)).findByName("NonExistentGame");
        verify(gamerSkillRepository, never()).save(any(GamerSkill.class));
    }

    @Test
    void linkGamerToGame_WithEmptyUsername_ShouldReturn400() throws Exception {
        GamerSkillDTO invalidDTO = new GamerSkillDTO("", "Counter-Strike", Level.PRO);
        
        mockMvc.perform(post("/api/gamer-skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
        
        verify(gamerRepository, never()).findByUsername(any());
    }

// --- Test cases for GET /api/gamer-skills/by-level endpoint ---

    @Test
    void getGamersByLevelAndGame_WithValidData_ShouldReturnGamers() throws Exception {
        List<GamerSkill> gamerSkills = Arrays.asList(testGamerSkill);
        when(gamerSkillRepository.findByGame_NameAndLevel("Counter-Strike", Level.PRO))
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
        
        verify(gamerSkillRepository, times(1)).findByGame_NameAndLevel("Counter-Strike", Level.PRO);
    }

    @Test
    void getGamersByLevelAndGame_WithNoResults_ShouldReturn404() throws Exception {
        when(gamerSkillRepository.findByGame_NameAndLevel("Counter-Strike", Level.INVINCIBLE))
                .thenReturn(Arrays.asList());
        
        mockMvc.perform(get("/api/gamer-skills/by-level")
                .param("gameName", "Counter-Strike")
                .param("level", "INVINCIBLE"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No gamers found with INVINCIBLE level in Counter-Strike"));
    }

    @Test
    void getGamersByLevelAndGame_WithEmptyGameName_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/gamer-skills/by-level")
                .param("gameName", "")
                .param("level", "PRO"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Game name is required"));
    }

// --- Test cases for GET /api/gamer-skills/search endpoint ---

    @Test
    void searchGamers_WithAllParameters_ShouldReturnFilteredResults() throws Exception {
        List<GamerSkill> gamerSkills = Arrays.asList(testGamerSkill);
        when(gamerSkillRepository.findByLevelAndGameNameAndCountry(Level.PRO, "Counter-Strike", "USA"))
                .thenReturn(gamerSkills);
        
        mockMvc.perform(get("/api/gamer-skills/search")
                .param("level", "PRO")
                .param("gameName", "Counter-Strike")
                .param("country", "USA"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
        
        verify(gamerSkillRepository, times(1))
                .findByLevelAndGameNameAndCountry(Level.PRO, "Counter-Strike", "USA");
    }

    @Test
    void searchGamers_WithNoParameters_ShouldReturnAllGamers() throws Exception {
        List<GamerSkill> gamerSkills = Arrays.asList(testGamerSkill);
        when(gamerSkillRepository.findByLevelAndGameNameAndCountry(null, null, null))
                .thenReturn(gamerSkills);
        
        mockMvc.perform(get("/api/gamer-skills/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchGamers_WithNoResults_ShouldReturn404() throws Exception {
        when(gamerSkillRepository.findByLevelAndGameNameAndCountry(Level.INVINCIBLE, "NonExistentGame", "Mars"))
                .thenReturn(Arrays.asList());
        
        mockMvc.perform(get("/api/gamer-skills/search")
                .param("level", "INVINCIBLE")
                .param("gameName", "NonExistentGame")
                .param("country", "Mars"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("No gamers found matching criteria")));
    }
}