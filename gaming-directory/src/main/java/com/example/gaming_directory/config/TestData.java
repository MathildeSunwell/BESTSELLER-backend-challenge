package com.example.gaming_directory.config;

import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.repository.GamerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestData implements CommandLineRunner {
    
    @Autowired
    private GamerRepository gamerRepository;
    
    @Override
    public void run(String... args) throws Exception {
        
        // sample gamers
        gamerRepository.save(new Gamer("Joey"));
        gamerRepository.save(new Gamer("Chandler"));
        gamerRepository.save(new Gamer("Ross"));
        gamerRepository.save(new Gamer("Monica"));
        gamerRepository.save(new Gamer("Rachel"));
        gamerRepository.save(new Gamer("Phoebe"));

        System.out.println("Sample gamers loaded!");
    }
}
