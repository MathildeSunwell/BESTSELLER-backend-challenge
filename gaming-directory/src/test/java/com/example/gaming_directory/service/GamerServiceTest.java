package com.example.gaming_directory.service;

import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.repository.GamerRepository;
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
class GamerServiceTest {

    @Mock
    private GamerRepository gamerRepository;

    @InjectMocks
    private GamerService gamerService;

    private Gamer testGamer;

    @BeforeEach
    void setUp() {
        testGamer = new Gamer("TestUser", "USA");
        testGamer.setId(1L);
    }

    @Test
    void getAllGamers_ShouldReturnAllGamers() {
        List<Gamer> gamers = Arrays.asList(testGamer);
        when(gamerRepository.findAll()).thenReturn(gamers);

        List<Gamer> result = gamerService.getAllGamers();

        assertEquals(1, result.size());
        assertEquals("TestUser", result.get(0).getUsername());
        verify(gamerRepository, times(1)).findAll();
    }

    @Test
    void createGamer_WithValidData_ShouldCreateGamer() {
        when(gamerRepository.findByUsername("TestUser")).thenReturn(Optional.empty());
        when(gamerRepository.save(any(Gamer.class))).thenReturn(testGamer);

        Gamer result = gamerService.createGamer("TestUser", "USA");

        assertEquals("TestUser", result.getUsername());
        assertEquals("USA", result.getCountry());
        verify(gamerRepository, times(1)).save(any(Gamer.class));
    }

    @Test
    void createGamer_WithExistingUsername_ShouldThrowException() {
        when(gamerRepository.findByUsername("TestUser")).thenReturn(Optional.of(testGamer));

        assertThrows(IllegalArgumentException.class, () -> 
            gamerService.createGamer("TestUser", "USA"));
        
        verify(gamerRepository, never()).save(any(Gamer.class));
    }

    @Test
    void createGamer_WithEmptyUsername_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            gamerService.createGamer("", "USA"));
            
        verify(gamerRepository, never()).findByUsername(any());
    }

    @Test
    void getGamerById_WithValidId_ShouldReturnGamer() {
        when(gamerRepository.findById(1L)).thenReturn(Optional.of(testGamer));

        Optional<Gamer> result = gamerService.getGamerById(1L);

        assertTrue(result.isPresent());
        assertEquals("TestUser", result.get().getUsername());
    }

    @Test
    void getGamerById_WithInvalidId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            gamerService.getGamerById(0L));
        
        verify(gamerRepository, never()).findById(any());
    }
}