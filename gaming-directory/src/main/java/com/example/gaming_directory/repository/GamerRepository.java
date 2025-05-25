package com.example.gaming_directory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.gaming_directory.entity.Gamer;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, Long> {  

    // findByUsername returns Optional<Gamer> - either contains a Gamer or is empty. 
    Optional<Gamer> findByUsername(String username);
}
