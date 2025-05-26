package com.example.gaming_directory.service;

import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.repository.GameRepository;
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
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;

    @BeforeEach
    void setUp() {
        testGame = new Game("Counter-Strike");
        testGame.setId(1L);
    }

    @Test
    void getAllGames_ShouldReturnAllGames() {
        List<Game> games = Arrays.asList(testGame);
        when(gameRepository.findAll()).thenReturn(games);

        List<Game> result = gameService.getAllGames();

        assertEquals(1, result.size());
        assertEquals("Counter-Strike", result.get(0).getName());
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    void createGame_WithValidData_ShouldCreateGame() {
        when(gameRepository.findByName("Counter-Strike")).thenReturn(Optional.empty());
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        Game result = gameService.createGame("Counter-Strike");

        assertEquals("Counter-Strike", result.getName());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void createGame_WithExistingName_ShouldThrowException() {
        when(gameRepository.findByName("Counter-Strike")).thenReturn(Optional.of(testGame));

        assertThrows(IllegalArgumentException.class, () -> 
            gameService.createGame("Counter-Strike"));
        
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void createGame_WithEmptyName_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            gameService.createGame(""));
            
        verify(gameRepository, never()).findByName(any());
    }

    @Test
    void getGameById_WithValidId_ShouldReturnGame() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        Optional<Game> result = gameService.getGameById(1L);

        assertTrue(result.isPresent());
        assertEquals("Counter-Strike", result.get().getName());
    }

    @Test
    void getGameById_WithInvalidId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            gameService.getGameById(0L));
        
        verify(gameRepository, never()).findById(any());
    }
}