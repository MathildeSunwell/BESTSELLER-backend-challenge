package com.example.gaming_directory.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.gaming_directory.entity.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    Optional<Game> findByName(String name);
}
