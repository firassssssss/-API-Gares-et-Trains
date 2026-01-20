package com.example.demo.repository;

import com.example.demo.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    
    // Existing methods
    List<Train> findByGareDepartVilleAndGareArriveeVille(String villeDepart, String villeArrivee);
    List<Train> findByNumeroTrainContainingIgnoreCase(String numero);
    List<Train> findByHeureDepartBetween(LocalDateTime start, LocalDateTime end);
    
    // ✅ ENDPOINT 13: Required method
    @Query("SELECT t FROM Train t WHERE t.gareDepart.id = :gareId OR t.gareArrivee.id = :gareId")
    List<Train> findTrainsByGareId(@Param("gareId") Long gareId);
    
    // Additional methods for advanced search
    @Query("SELECT t FROM Train t WHERE " +
           "(:villeDepart IS NULL OR t.gareDepart.ville = :villeDepart) AND " +
           "(:villeArrivee IS NULL OR t.gareArrivee.ville = :villeArrivee)")
    List<Train> findByVilles(@Param("villeDepart") String villeDepart, 
                            @Param("villeArrivee") String villeArrivee);
    
    // Trains with duration less than X minutes
    @Query("SELECT t FROM Train t WHERE " +
           "TIMESTAMPDIFF(MINUTE, t.heureDepart, t.heureArrivee) <= :maxMinutes")
    List<Train> findByMaxDuration(@Param("maxMinutes") Long maxMinutes);
}