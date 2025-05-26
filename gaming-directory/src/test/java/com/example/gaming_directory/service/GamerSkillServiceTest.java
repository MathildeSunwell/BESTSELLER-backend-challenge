package com.example.gaming_directory.service;

import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.entity.GamerSkill;
import com.example.gaming_directory.enums.Level;
import com.example.gaming_directory.repository.GameRepository;
import com.example.gaming_directory.repository.GamerRepository;
import com.example.gaming_directory.repository.GamerSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamerSkillServiceTest {

    @Mock
    private GamerSkillRepository gamerSkillRepository;
    
    @Mock
    private GamerRepository gamerRepository;
    
    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GamerSkillService gamerSkillService;

    private Gamer testGamer;
    private Game testGame;
    private GamerSkill testGamerSkill;

    @BeforeEach
    void setUp() {
        testGamer = new Gamer("TestUser", "USA");
        testGamer.setId(1L);
        
        testGame = new Game("Counter-Strike");
        testGame.setId(1L);
        
        testGamerSkill = new GamerSkill(testGamer, testGame, Level.PRO);
        testGamerSkill.setId(1L);
    }

    @Test
    void linkGamerToGame_WithValidData_ShouldCreateLink() {
        when(gamerRepository.findByUsername("TestUser")).thenReturn(Optional.of(testGamer));
        when(gameRepository.findByName("Counter-Strike")).thenReturn(Optional.of(testGame));
        when(gamerSkillRepository.findByGamerIdAndGameId(1L, 1L)).thenReturn(Optional.empty());
        when(gamerSkillRepository.save(any(GamerSkill.class))).thenReturn(testGamerSkill);

        GamerSkill result = gamerSkillService.linkGamerToGame("TestUser", "Counter-Strike", Level.PRO);

        assertEquals(Level.PRO, result.getLevel());
        assertEquals("TestUser", result.getGamer().getUsername());
        assertEquals("Counter-Strike", result.getGame().getName());
        verify(gamerSkillRepository, times(1)).save(any(GamerSkill.class));
    }

    @Test
    void linkGamerToGame_WithExistingLink_ShouldUpdateLevel() {
        when(gamerRepository.findByUsername("TestUser")).thenReturn(Optional.of(testGamer));
        when(gameRepository.findByName("Counter-Strike")).thenReturn(Optional.of(testGame));
        when(gamerSkillRepository.findByGamerIdAndGameId(1L, 1L)).thenReturn(Optional.of(testGamerSkill));
        when(gamerSkillRepository.save(any(GamerSkill.class))).thenReturn(testGamerSkill);

        GamerSkill result = gamerSkillService.linkGamerToGame("TestUser", "Counter-Strike", Level.INVINCIBLE);

        verify(gamerSkillRepository, times(1)).save(any(GamerSkill.class));
    }

    @Test
    void linkGamerToGame_WithNonExistentGamer_ShouldThrowException() {
        when(gamerRepository.findByUsername("NonExistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            gamerSkillService.linkGamerToGame("NonExistent", "Counter-Strike", Level.PRO));
        
        verify(gamerSkillRepository, never()).save(any(GamerSkill.class));
    }

    @Test
    void linkGamerToGame_WithNonExistentGame_ShouldThrowException() {
        when(gamerRepository.findByUsername("TestUser")).thenReturn(Optional.of(testGamer));
        when(gameRepository.findByName("NonExistentGame")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            gamerSkillService.linkGamerToGame("TestUser", "NonExistentGame", Level.PRO));
        
        verify(gamerSkillRepository, never()).save(any(GamerSkill.class));
    }

    @Test
    void linkGamerToGame_WithEmptyUsername_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            gamerSkillService.linkGamerToGame("", "Counter-Strike", Level.PRO));
        
        verify(gamerRepository, never()).findByUsername(any());
    }

    @Test
    void getGamersByLevelAndGame_WithValidData_ShouldReturnGamers() {
        List<GamerSkill> expectedSkills = Arrays.asList(testGamerSkill);
        when(gamerSkillRepository.findByGame_NameAndLevel("Counter-Strike", Level.PRO))
                .thenReturn(expectedSkills);

        List<GamerSkill> result = gamerSkillService.getGamersByLevelAndGame("Counter-Strike", Level.PRO);

        assertEquals(1, result.size());
        assertEquals("TestUser", result.get(0).getGamer().getUsername());
        verify(gamerSkillRepository, times(1)).findByGame_NameAndLevel("Counter-Strike", Level.PRO);
    }

    @Test
    void getGamersByLevelAndGame_WithEmptyGameName_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            gamerSkillService.getGamersByLevelAndGame("", Level.PRO));
        
        verify(gamerSkillRepository, never()).findByGame_NameAndLevel(any(), any());
    }

    @Test
    void searchGamers_WithAllParameters_ShouldReturnFilteredResults() {
        List<GamerSkill> expectedSkills = Arrays.asList(testGamerSkill);
        when(gamerSkillRepository.findByLevelAndGameNameAndCountry(Level.PRO, "Counter-Strike", "USA"))
                .thenReturn(expectedSkills);

        List<GamerSkill> result = gamerSkillService.searchGamers(Level.PRO, "Counter-Strike", "USA");

        assertEquals(1, result.size());
        verify(gamerSkillRepository, times(1))
                .findByLevelAndGameNameAndCountry(Level.PRO, "Counter-Strike", "USA");
    }

    @Test
    void searchGamers_WithNullParameters_ShouldSearchAll() {
        List<GamerSkill> expectedSkills = Arrays.asList(testGamerSkill);
        when(gamerSkillRepository.findByLevelAndGameNameAndCountry(null, null, null))
                .thenReturn(expectedSkills);

        List<GamerSkill> result = gamerSkillService.searchGamers(null, null, null);

        assertEquals(1, result.size());
        verify(gamerSkillRepository, times(1))
                .findByLevelAndGameNameAndCountry(null, null, null);
    }
}