package com.example.gaming_directory.entity;

import com.example.gaming_directory.enums.Level;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GamerSkillTest {
    
    private Validator validator;
    private Gamer testGamer;
    private Game testGame;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        testGamer = new Gamer("TestGamer", "USA");
        testGame = new Game("Test Game");
    }
    
// Test valid gamer skill - pass validation
    @Test
    void validGamerSkill_ShouldPassValidation() {
        GamerSkill gamerSkill = new GamerSkill(testGamer, testGame, Level.PRO);
        
        Set<ConstraintViolation<GamerSkill>> violations = validator.validate(gamerSkill);
        
        assertThat(violations).isEmpty();
    }

// Test null gamer - fail validation
    @Test
    void nullGamer_ShouldFailValidation() {
        GamerSkill gamerSkill = new GamerSkill(null, testGame, Level.PRO);
        
        Set<ConstraintViolation<GamerSkill>> violations = validator.validate(gamerSkill);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("gamer");
    }

// Test null game - fail validation
    @Test
    void nullGame_ShouldFailValidation() {
        GamerSkill gamerSkill = new GamerSkill(testGamer, null, Level.PRO);
        
        Set<ConstraintViolation<GamerSkill>> violations = validator.validate(gamerSkill);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("game");
    }

// Test null level - fail validation
    @Test
    void nullLevel_ShouldFailValidation() {
        GamerSkill gamerSkill = new GamerSkill(testGamer, testGame, null);
        
        Set<ConstraintViolation<GamerSkill>> violations = validator.validate(gamerSkill);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("level");
    }

// Test all levels - should pass validation
    @Test
    void allLevels_ShouldPassValidation() {
        for (Level level : Level.values()) {
            GamerSkill gamerSkill = new GamerSkill(testGamer, testGame, level);
            
            Set<ConstraintViolation<GamerSkill>> violations = validator.validate(gamerSkill);
            
            assertThat(violations).isEmpty();
        }
    }

// Test constructor and getters/setters
    @Test
    void testConstructorAndGettersSetters() {
        GamerSkill gamerSkill = new GamerSkill(testGamer, testGame, Level.INVINCIBLE);
        
        assertThat(gamerSkill.getGamer()).isEqualTo(testGamer);
        assertThat(gamerSkill.getGame()).isEqualTo(testGame);
        assertThat(gamerSkill.getLevel()).isEqualTo(Level.INVINCIBLE);
        assertThat(gamerSkill.getId()).isNull(); // ID is null until persisted
        
        gamerSkill.setId(1L);
        gamerSkill.setLevel(Level.NOOB);
        
        assertThat(gamerSkill.getId()).isEqualTo(1L);
        assertThat(gamerSkill.getLevel()).isEqualTo(Level.NOOB);
    }

// Test convenience methods for JSON representation
    @Test
    void testConvenienceMethods() {
        GamerSkill gamerSkill = new GamerSkill(testGamer, testGame, Level.PRO);
        
        assertThat(gamerSkill.getGamerName()).isEqualTo("TestGamer");
        assertThat(gamerSkill.getGamerCountry()).isEqualTo("USA");
        assertThat(gamerSkill.getGameName()).isEqualTo("Test Game");
        assertThat(gamerSkill.getSkillLevel()).isEqualTo("PRO");
    }

// Test default constructor
    @Test
    void testDefaultConstructor() {
        GamerSkill gamerSkill = new GamerSkill();
        
        assertThat(gamerSkill.getGamer()).isNull();
        assertThat(gamerSkill.getGame()).isNull();
        assertThat(gamerSkill.getLevel()).isNull();
        assertThat(gamerSkill.getId()).isNull();
    }

// Test multiple validation failures
    @Test
    void multipleNullFields_ShouldFailValidationWithMultipleViolations() {
        GamerSkill gamerSkill = new GamerSkill(null, null, null);
        
        Set<ConstraintViolation<GamerSkill>> violations = validator.validate(gamerSkill);
        
        assertThat(violations).hasSize(3); // gamer, game, and level are all null
    }
}