package com.example.gaming_directory.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.gaming_directory.entity.GamerSkill;
import com.example.gaming_directory.enums.Level;

@Repository
public interface GamerSkillRepository extends JpaRepository<GamerSkill, Long> {
    
    // Find existing skill for a gamer and game combination
    Optional<GamerSkill> findByGamerIdAndGameId(Long gamerId, Long gameId);
    
    // Find gamers by level and game name
    List<GamerSkill> findByGame_NameAndLevel(String gameName, Level level);
    
    // Search for auto-matching gamers based on criteria
    @Query("SELECT gs FROM GamerSkill gs WHERE " +
           "(:level IS NULL OR gs.level = :level) AND " +
           "(:gameName IS NULL OR gs.game.name = :gameName) AND " +
           "(:country IS NULL OR gs.gamer.country = :country)")
    List<GamerSkill> findByLevelAndGameNameAndCountry(
        @Param("level") Level level, 
        @Param("gameName") String gameName, 
        @Param("country") String country);
}