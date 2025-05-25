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
        Gamer gamer = new Gamer("ValidUsername");
        
        Set<ConstraintViolation<Gamer>> violations = validator.validate(gamer);
        
        assertThat(violations).isEmpty();
    }
    

    // Test empty username - fail validation
    @Test
    void emptyUsername_ShouldFailValidation() {
        Gamer gamer = new Gamer("");
        
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

        Set<ConstraintViolation<Gamer>> violations = validator.validate(gamer);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Username is required");
    }
}
