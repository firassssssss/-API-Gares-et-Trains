package com.example.demo.service;

import com.example.demo.model.Gare;
import com.example.demo.model.Train;
import com.example.demo.repository.GareRepository;
import com.example.demo.repository.TrainRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainService {
    
    @Autowired
    private TrainRepository trainRepository;
    
    @Autowired
    private GareRepository gareRepository;
    
    // CRUD methods...
    public Train createTrain(Train train) {
        
        Gare gareDepart = gareRepository.findById(train.getGareDepart().getId())
                .orElseThrow(() -> new RuntimeException("Gare de départ non trouvée"));
        
        Gare gareArrivee = gareRepository.findById(train.getGareArrivee().getId())
                .orElseThrow(() -> new RuntimeException("Gare d'arrivée non trouvée"));
        
        train.setGareDepart(gareDepart);
        train.setGareArrivee(gareArrivee);
        
        return trainRepository.save(train);
    }
    
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }
    
    public Train getTrainById(Long id) {
        return trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train non trouvé avec l'id: " + id));
    }
    
    public Train updateTrain(Long id, Train trainDetails) {
        Train train = getTrainById(id);
        train.setNumeroTrain(trainDetails.getNumeroTrain());
        train.setHeureDepart(trainDetails.getHeureDepart());
        train.setHeureArrivee(trainDetails.getHeureArrivee());
        
        if (trainDetails.getGareDepart() != null) {
            Gare gareDepart = gareRepository.findById(trainDetails.getGareDepart().getId())
                    .orElseThrow(() -> new RuntimeException("Gare de départ non trouvée"));
            train.setGareDepart(gareDepart);
        }
        
        if (trainDetails.getGareArrivee() != null) {
            Gare gareArrivee = gareRepository.findById(trainDetails.getGareArrivee().getId())
                    .orElseThrow(() -> new RuntimeException("Gare d'arrivée non trouvée"));
            train.setGareArrivee(gareArrivee);
        }
        
        return trainRepository.save(train);
    }
    
    public void deleteTrain(Long id) {
        Train train = getTrainById(id);
        trainRepository.delete(train);
    }
    
    // Services spécifiques
    public List<Train> getTrainsByVilles(String villeDepart, String villeArrivee) {
        return trainRepository.findByGareDepartVilleAndGareArriveeVille(villeDepart, villeArrivee);
    }
    
    public List<Train> getTrainsByGare(Long gareId) {
        return trainRepository.findTrainsByGareId(gareId);
    }
    
    public List<Train> getTrainsByNumero(String numero) {
        return trainRepository.findByNumeroTrainContainingIgnoreCase(numero);
    }
    
    public List<Train> getTrainsProchains() {
        // Retourne les trains des prochaines 24h
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime demain = maintenant.plusDays(1);
        return trainRepository.findByHeureDepartBetween(maintenant, demain);
    }
    
    // ✅ FIXED: Using java.time.Duration
    public Map<String, Object> calculerDureeVoyage(Long trainId) {
        Train train = getTrainById(trainId);
        
        if (train.getHeureDepart() == null || train.getHeureArrivee() == null) {
            throw new RuntimeException("Les heures de départ/arrivée ne sont pas définies");
        }
        
        if (train.getHeureDepart().isAfter(train.getHeureArrivee())) {
            throw new RuntimeException("L'heure de départ doit être avant l'heure d'arrivée");
        }
        
        // Use java.time.Duration
        Duration duree = Duration.between(train.getHeureDepart(), train.getHeureArrivee());
        
        Map<String, Object> result = new HashMap<>();
        result.put("trainId", trainId);
        result.put("numeroTrain", train.getNumeroTrain());
        result.put("heureDepart", train.getHeureDepart());
        result.put("heureArrivee", train.getHeureArrivee());
        result.put("dureeEnMinutes", duree.toMinutes());
        result.put("dureeEnHeures", duree.toHours());
        result.put("dureeFormatee", String.format("%dh %02dm", 
            duree.toHours(), 
            duree.toMinutes() % 60)); // Fixed: use % operator
        
        return result;
    }
    
    // ✅ Add the rechercheAvancee method
    public List<Train> rechercheAvancee(
            String villeDepart,
            String villeArrivee,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Integer dureeMaxMinutes,
            Boolean gareOuverteSeulement) {
        
        List<Train> allTrains = trainRepository.findAll();
        
        return allTrains.stream()
                .filter(train -> {
                    // Filter by departure city
                    if (villeDepart != null && !villeDepart.isEmpty()) {
                        return train.getGareDepart().getVille().equalsIgnoreCase(villeDepart);
                    }
                    return true;
                })
                .filter(train -> {
                    // Filter by arrival city
                    if (villeArrivee != null && !villeArrivee.isEmpty()) {
                        return train.getGareArrivee().getVille().equalsIgnoreCase(villeArrivee);
                    }
                    return true;
                })
                .filter(train -> {
                    // Filter by start date
                    if (dateDebut != null && train.getHeureDepart() != null) {
                        return !train.getHeureDepart().isBefore(dateDebut);
                    }
                    return true;
                })
                .filter(train -> {
                    // Filter by end date
                    if (dateFin != null && train.getHeureDepart() != null) {
                        return !train.getHeureDepart().isAfter(dateFin);
                    }
                    return true;
                })
                .filter(train -> {
                    // Filter by max duration
                    if (dureeMaxMinutes != null && dureeMaxMinutes > 0 
                        && train.getHeureDepart() != null && train.getHeureArrivee() != null) {
                        Duration duree = Duration.between(train.getHeureDepart(), train.getHeureArrivee());
                        return duree.toMinutes() <= dureeMaxMinutes;
                    }
                    return true;
                })
                .filter(train -> {
                    // Filter by open stations only
                    if (gareOuverteSeulement != null && gareOuverteSeulement) {
                        return train.getGareDepart().getEstOuverte() 
                               && train.getGareArrivee().getEstOuverte();
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}