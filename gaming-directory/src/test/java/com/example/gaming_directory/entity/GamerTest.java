package com.example.gaming_directory.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GamerTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
// Test valid gamer - pass validation
    @Test
    void validGamer_ShouldPassValidation() {
        Gamer gamer = new Gamer("ValidUsername", "USA");
        
        Set<ConstraintViolation<Gamer>> violations = validator.validate(gamer);
        
        assertThat(violations).isEmpty();
    }

// Test empty username - fail validation
    @Test
    void emptyUsername_ShouldFailValidation() {
        Gamer gamer = new Gamer("", "USA");
        
        Set<ConstraintViolation<Gamer>> violations = validator.validate(gamer);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Username is required");
    }

// Test null username - fail validation
    @Test
    void nullUsername_ShouldFailValidation() {
        Gamer gamer = new Gamer();
        gamer.setUsername(null);
        gamer.setCountry("USA");

        Set<ConstraintViolation<Gamer>> violations = validator.validate(gamer);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Username is required");
    }

// Test empty country - fail validation
    @Test
    void emptyCountry_ShouldFailValidation() {
        Gamer gamer = new Gamer("ValidUsername", "");
        
        Set<ConstraintViolation<Gamer>> violations = validator.validate(gamer);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Country is required");
    }

// Test null country - fail validation
    @Test
    void nullCountry_ShouldFailValidation() {
        Gamer gamer = new Gamer();
        gamer.setUsername("ValidUsername");
        gamer.setCountry(null);

        Set<ConstraintViolation<Gamer>> violations = validator.validate(gamer);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Country is required");
    }

// Test both username and country empty - fail with 2 violations
    @Test
    void emptyUsernameAndCountry_ShouldFailValidationWithTwoViolations() {
        Gamer gamer = new Gamer("", "");
        
        Set<ConstraintViolation<Gamer>> violations = validator.validate(gamer);
        
        assertThat(violations).hasSize(2);
    }
}