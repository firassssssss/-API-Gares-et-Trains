package com.example.demo.service;

import com.example.demo.model.Gare;
import com.example.demo.model.Train;
import com.example.demo.repository.GareRepository;
import com.example.demo.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GareService {
    
    @Autowired
    private GareRepository gareRepository;
    
    @Autowired
    private TrainRepository trainRepository;
    
    // ============ CRUD METHODS ============
    
    public List<Gare> getAllGares() {
        return gareRepository.findAll();
    }
    
    public Gare getGareById(Long id) {
        return gareRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gare non trouvée avec l'id: " + id));
    }
    
    public Gare createGare(Gare gare) {
        // Set default values if not provided
        if (gare.getEstOuverte() == null) {
            gare.setEstOuverte(true);
        }
        if (gare.getNombreVoies() == null) {
            gare.setNombreVoies(1);
        }
        return gareRepository.save(gare);
    }
    
    public Gare updateGare(Long id, Gare gareDetails) {
        Gare gare = getGareById(id);
        gare.setNom(gareDetails.getNom());
        gare.setVille(gareDetails.getVille());
        gare.setNombreVoies(gareDetails.getNombreVoies());
        gare.setEstOuverte(gareDetails.getEstOuverte());
        return gareRepository.save(gare);
    }
    
    public void deleteGare(Long id) {
        Gare gare = getGareById(id);
        gareRepository.delete(gare);
    }
    
    // ============ SEARCH & FILTER METHODS ============
    
    public List<Gare> getGaresByVille(String ville) {
        return gareRepository.findByVille(ville);
    }
    
    public List<Gare> getGaresOuvertes() {
        return gareRepository.findByEstOuverteTrue();
    }
    
    public List<Gare> getGaresByNom(String nom) {
        return gareRepository.findByNomContainingIgnoreCase(nom);
    }
    
    // ============ MISSING METHODS (ADD THESE) ============
    
    // 1. Créer plusieurs gares
    public List<Gare> createMultipleGares(List<Gare> gares) {
        // Set default values for each gare
        for (Gare gare : gares) {
            if (gare.getEstOuverte() == null) {
                gare.setEstOuverte(true);
            }
            if (gare.getNombreVoies() == null) {
                gare.setNombreVoies(1);
            }
        }
        return gareRepository.saveAll(gares);
    }
    
    // 2. Vérifier si une gare existe
    public boolean gareExists(Long id) {
        return gareRepository.existsById(id);
    }
    
    // 3. Compter le nombre total de gares
    public long countGares() {
        return gareRepository.count();
    }
    
    // 4. Gares avec plus de X voies
    public List<Gare> getGaresByMinVoies(Integer minVoies) {
        List<Gare> allGares = getAllGares();
        return allGares.stream()
                .filter(gare -> gare.getNombreVoies() != null && gare.getNombreVoies() >= minVoies)
                .collect(Collectors.toList());
    }
    
    // 5. Mettre à jour le statut d'une gare
    public Gare updateGareStatus(Long id, Boolean estOuverte) {
        Gare gare = getGareById(id);
        gare.setEstOuverte(estOuverte);
        return gareRepository.save(gare);
    }
    
    // 6. Gares par région (pour l'exemple, on utilise la ville comme région)
    // Vous pouvez adapter cette logique selon vos besoins
    public List<Gare> getGaresByRegion(String region) {
        // Pour l'exemple, on considère que la région = ville
        // Vous pouvez créer un champ "region" dans l'entité Gare si nécessaire
        return gareRepository.findByVille(region);
    }
    
    // ============ STATISTIQUES GARE ============
    
    public Map<String, Object> getStatistiquesGare(Long gareId) {
        Gare gare = getGareById(gareId);
        
        // Get all trains for this station
        List<Train> trains = trainRepository.findTrainsByGareId(gareId);
        
        // Calculate statistics
        long trainsDepart = trains.stream()
                .filter(t -> t.getGareDepart().getId().equals(gareId))
                .count();
        
        long trainsArrivee = trains.stream()
                .filter(t -> t.getGareArrivee().getId().equals(gareId))
                .count();
        
        long totalTrains = trainsDepart + trainsArrivee;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("gareId", gareId);
        stats.put("nom", gare.getNom());
        stats.put("ville", gare.getVille());
        stats.put("nombreVoies", gare.getNombreVoies());
        stats.put("estOuverte", gare.getEstOuverte());
        stats.put("totalTrains", trains.size());
        stats.put("trainsDepart", trainsDepart);
        stats.put("trainsArrivee", trainsArrivee);
        stats.put("traficTotal", totalTrains);
        
        // Additional metrics
        if (!trains.isEmpty()) {
            // Calculate peak hours (example)
            Map<Integer, Long> departuresByHour = trains.stream()
                .filter(t -> t.getGareDepart().getId().equals(gareId) && t.getHeureDepart() != null)
                .collect(Collectors.groupingBy(
                    t -> t.getHeureDepart().getHour(),
                    Collectors.counting()
                ));
            
            stats.put("departuresByHour", departuresByHour);
        }
        
        return stats;
    }
    
    // ============ STATISTIQUES GLOBALES ============
    
    public Map<String, Object> getStatistiquesGlobales() {
        List<Gare> gares = getAllGares();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalGares", gares.size());
        stats.put("garesOuvertes", gares.stream().filter(Gare::getEstOuverte).count());
        stats.put("garesFermees", gares.stream().filter(g -> !g.getEstOuverte()).count());
        stats.put("totalVoies", gares.stream().mapToInt(Gare::getNombreVoies).sum());
        
        // Calculate average number of tracks
        double moyenneVoies = gares.isEmpty() ? 0 : 
            gares.stream().mapToInt(Gare::getNombreVoies).average().orElse(0);
        stats.put("moyenneVoies", Math.round(moyenneVoies * 100.0) / 100.0);
        
        // Ville with most stations
        Map<String, Long> garesParVille = gares.stream()
                .collect(Collectors.groupingBy(Gare::getVille, Collectors.counting()));
        
        if (!garesParVille.isEmpty()) {
            String villePlusGares = garesParVille.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            long nombreGares = garesParVille.get(villePlusGares);
            stats.put("villePlusGares", villePlusGares);
            stats.put("nombreGaresDansVille", nombreGares);
        }
        
        // Distribution des gares par ville
        stats.put("distributionParVille", garesParVille);
        
        return stats;
    }
}