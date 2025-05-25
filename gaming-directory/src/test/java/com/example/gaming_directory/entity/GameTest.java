package com.example.gaming_directory.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
// Test valid game - pass validation
    @Test
    void validGame_ShouldPassValidation() {
        Game game = new Game("Counter-Strike");
        
        Set<ConstraintViolation<Game>> violations = validator.validate(game);
        
        assertThat(violations).isEmpty();
    }

// Test empty name - fail validation
    @Test
    void emptyName_ShouldFailValidation() {
        Game game = new Game("");
        
        Set<ConstraintViolation<Game>> violations = validator.validate(game);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Game name is required");
    }

// Test null name - fail validation
    @Test
    void nullName_ShouldFailValidation() {
        Game game = new Game();
        game.setName(null);

        Set<ConstraintViolation<Game>> violations = validator.validate(game);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Game name is required");
    }

// Test whitespace-only name - fail validation
    @Test
    void whitespaceOnlyName_ShouldFailValidation() {
        Game game = new Game("   ");
        
        Set<ConstraintViolation<Game>> violations = validator.validate(game);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Game name is required");
    }

// Test constructor and getters/setters
    @Test
    void testConstructorAndGettersSetters() {
        Game game = new Game("Diablo");
        
        assertThat(game.getName()).isEqualTo("Diablo");
        assertThat(game.getId()).isNull(); // ID is null until persisted
        assertThat(game.getGamerSkills()).isNotNull().isEmpty();
        
        game.setId(1L);
        game.setName("Updated Name");
        
        assertThat(game.getId()).isEqualTo(1L);
        assertThat(game.getName()).isEqualTo("Updated Name");
    }

// Test default constructor
    @Test
    void testDefaultConstructor() {
        Game game = new Game();
        
        assertThat(game.getName()).isNull();
        assertThat(game.getId()).isNull();
        assertThat(game.getGamerSkills()).isNotNull().isEmpty();
    }
}