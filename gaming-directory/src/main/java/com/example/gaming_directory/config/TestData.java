package com.example.gaming_directory.config;

import com.example.gaming_directory.entity.Game;
import com.example.gaming_directory.entity.Gamer;
import com.example.gaming_directory.entity.GamerSkill;
import com.example.gaming_directory.enums.Level;
import com.example.gaming_directory.repository.GameRepository;
import com.example.gaming_directory.repository.GamerRepository;
import com.example.gaming_directory.repository.GamerSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestData implements CommandLineRunner {
    
    @Autowired
    private GamerRepository gamerRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GamerSkillRepository gamerSkillRepository;
    
    // '...' means this method can take any number of string arguments (varargs)
    @Override
    public void run(String... args) throws Exception {
        
        // Sample gamers with countries
        Gamer joey = gamerRepository.save(new Gamer("Joey", "USA"));
        Gamer chandler = gamerRepository.save(new Gamer("Chandler", "USA"));
        Gamer ross = gamerRepository.save(new Gamer("Ross", "Canada"));
        Gamer monica = gamerRepository.save(new Gamer("Monica", "UK"));
        Gamer rachel = gamerRepository.save(new Gamer("Rachel", "France"));
        Gamer phoebe = gamerRepository.save(new Gamer("Phoebe", "France"));
        Gamer alice = gamerRepository.save(new Gamer("Alice", "Germany"));
        Gamer bob = gamerRepository.save(new Gamer("Bob", "Germany"));
        Gamer charlie = gamerRepository.save(new Gamer("Charlie", "Japan"));
        Gamer diana = gamerRepository.save(new Gamer("Diana", "Japan"));
        Gamer eve = gamerRepository.save(new Gamer("Eve", "Brazil"));
        Gamer frank = gamerRepository.save(new Gamer("Frank", "Brazil"));
        Gamer grace = gamerRepository.save(new Gamer("Grace", "Australia"));
        Gamer henry = gamerRepository.save(new Gamer("Henry", "Australia"));
        Gamer ivy = gamerRepository.save(new Gamer("Ivy", "Sweden"));
        Gamer jack = gamerRepository.save(new Gamer("Jack", "Sweden"));
        Gamer kate = gamerRepository.save(new Gamer("Kate", "USA"));
        Gamer luke = gamerRepository.save(new Gamer("Luke", "UK"));
        Gamer mary = gamerRepository.save(new Gamer("Mary", "Canada"));
        Gamer nick = gamerRepository.save(new Gamer("Nick", "France"));

        System.out.println("Sample gamers loaded!");

        // Sample games
        Game counterStrike = gameRepository.save(new Game("Counter-Strike"));
        Game diablo = gameRepository.save(new Game("Diablo"));
        Game fortnite = gameRepository.save(new Game("Fortnite"));
        Game lol = gameRepository.save(new Game("League of Legends"));

        System.out.println("Sample games loaded!");

        // Sample gamer skills
        gamerSkillRepository.save(new GamerSkill(joey, counterStrike, Level.NOOB));
        gamerSkillRepository.save(new GamerSkill(joey, diablo, Level.PRO));

        gamerSkillRepository.save(new GamerSkill(chandler, counterStrike, Level.PRO));
        gamerSkillRepository.save(new GamerSkill(chandler, fortnite, Level.INVINCIBLE));

        gamerSkillRepository.save(new GamerSkill(ross, diablo, Level.NOOB));
        gamerSkillRepository.save(new GamerSkill(ross, lol, Level.PRO));
        
        gamerSkillRepository.save(new GamerSkill(monica, counterStrike, Level.INVINCIBLE));
        gamerSkillRepository.save(new GamerSkill(monica, diablo, Level.PRO));

        gamerSkillRepository.save(new GamerSkill(rachel, fortnite, Level.NOOB));
        gamerSkillRepository.save(new GamerSkill(rachel, lol, Level.INVINCIBLE));

        gamerSkillRepository.save(new GamerSkill(phoebe, diablo, Level.INVINCIBLE));
        gamerSkillRepository.save(new GamerSkill(phoebe, lol, Level.NOOB));
        
        gamerSkillRepository.save(new GamerSkill(alice, counterStrike, Level.PRO));
        gamerSkillRepository.save(new GamerSkill(alice, diablo, Level.INVINCIBLE));

        gamerSkillRepository.save(new GamerSkill(bob, counterStrike, Level.PRO));
        gamerSkillRepository.save(new GamerSkill(bob, diablo, Level.NOOB));

        gamerSkillRepository.save(new GamerSkill(charlie, lol, Level.INVINCIBLE));
        gamerSkillRepository.save(new GamerSkill(charlie, diablo, Level.PRO));

        gamerSkillRepository.save(new GamerSkill(diana, lol, Level.INVINCIBLE));
        gamerSkillRepository.save(new GamerSkill(diana, fortnite, Level.NOOB));

        gamerSkillRepository.save(new GamerSkill(eve, diablo, Level.INVINCIBLE));
        gamerSkillRepository.save(new GamerSkill(eve, fortnite, Level.PRO));

        gamerSkillRepository.save(new GamerSkill(frank, diablo, Level.INVINCIBLE));
        gamerSkillRepository.save(new GamerSkill(frank, counterStrike, Level.NOOB));

        gamerSkillRepository.save(new GamerSkill(grace, diablo, Level.PRO));
        gamerSkillRepository.save(new GamerSkill(grace, lol, Level.NOOB));

        gamerSkillRepository.save(new GamerSkill(henry, lol, Level.PRO));
        gamerSkillRepository.save(new GamerSkill(henry, diablo, Level.INVINCIBLE));

        gamerSkillRepository.save(new GamerSkill(ivy, counterStrike, Level.INVINCIBLE));
        gamerSkillRepository.save(new GamerSkill(ivy, diablo, Level.NOOB));

        gamerSkillRepository.save(new GamerSkill(jack, counterStrike, Level.INVINCIBLE));
        gamerSkillRepository.save(new GamerSkill(jack, lol, Level.PRO));

        gamerSkillRepository.save(new GamerSkill(kate, fortnite, Level.PRO));
        gamerSkillRepository.save(new GamerSkill(kate, lol, Level.NOOB));

        gamerSkillRepository.save(new GamerSkill(luke, diablo, Level.PRO));
        gamerSkillRepository.save(new GamerSkill(luke, counterStrike, Level.NOOB));

        gamerSkillRepository.save(new GamerSkill(mary, lol, Level.PRO));
        gamerSkillRepository.save(new GamerSkill(mary, diablo, Level.INVINCIBLE));

        gamerSkillRepository.save(new GamerSkill(nick, diablo, Level.NOOB));
        gamerSkillRepository.save(new GamerSkill(nick, fortnite, Level.INVINCIBLE));

        System.out.println("Sample gamer skills loaded!");
        System.out.println("All test data loaded successfully!");
    
    }
}