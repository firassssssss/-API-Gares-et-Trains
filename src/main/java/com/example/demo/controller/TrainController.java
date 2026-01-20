package com.example.demo.controller;

import com.example.demo.model.Train;
import com.example.demo.service.TrainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trains")
@CrossOrigin(origins = "*")  // Autorise les requêtes depuis n'importe quelle origine
public class TrainController {
    
    @Autowired
    private TrainService trainService;
    
    // === CRUD DE BASE ===
    
    /**
     * Créer un nouveau train
     * POST http://localhost:8080/api/trains
     */
    @PostMapping
    public ResponseEntity<Train> createTrain(@RequestBody Train train) {
        Train createdTrain = trainService.createTrain(train);
        return new ResponseEntity<>(createdTrain, HttpStatus.CREATED);  // HTTP 201
    }
    
    /**
     * Récupérer tous les trains
     * GET http://localhost:8080/api/trains
     */
    @GetMapping
    public ResponseEntity<List<Train>> getAllTrains() {
        List<Train> trains = trainService.getAllTrains();
        return new ResponseEntity<>(trains, HttpStatus.OK);  // HTTP 200
    }
    
    /**
     * Récupérer un train par son ID
     * GET http://localhost:8080/api/trains/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Train> getTrainById(@PathVariable Long id) {
        Train train = trainService.getTrainById(id);
        return new ResponseEntity<>(train, HttpStatus.OK);  // HTTP 200
    }
    
    /**
     * Mettre à jour un train existant
     * PUT http://localhost:8080/api/trains/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<Train> updateTrain(@PathVariable Long id, @RequestBody Train trainDetails) {
        Train updatedTrain = trainService.updateTrain(id, trainDetails);
        return new ResponseEntity<>(updatedTrain, HttpStatus.OK);  // HTTP 200
    }
    
    /**
     * Supprimer un train
     * DELETE http://localhost:8080/api/trains/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // HTTP 204
    }
    
    // === SERVICES SPÉCIFIQUES ===
    
    /**
     * Rechercher des trains entre deux villes
     * GET http://localhost:8080/api/trains/recherche?depart=Paris&arrivee=Lyon
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<Train>> getTrainsByVilles(
            @RequestParam String depart,
            @RequestParam String arrivee) {
        List<Train> trains = trainService.getTrainsByVilles(depart, arrivee);
        return new ResponseEntity<>(trains, HttpStatus.OK);  // HTTP 200
    }
    
    /**
     * Récupérer tous les trains liés à une gare (départs et arrivées)
     * GET http://localhost:8080/api/trains/gare/1
     */
    @GetMapping("/gare/{gareId}")
    public ResponseEntity<List<Train>> getTrainsByGare(@PathVariable Long gareId) {
        List<Train> trains = trainService.getTrainsByGare(gareId);
        return new ResponseEntity<>(trains, HttpStatus.OK);  // HTTP 200
    }
    
    /**
     * Rechercher des trains par numéro (recherche partielle)
     * GET http://localhost:8080/api/trains/search?numero=TGV
     */
    @GetMapping("/search")
    public ResponseEntity<List<Train>> searchTrainsByNumero(@RequestParam String numero) {
        List<Train> trains = trainService.getTrainsByNumero(numero);
        return new ResponseEntity<>(trains, HttpStatus.OK);  // HTTP 200
    }
    
    /**
     * Récupérer les trains des prochaines 24h
     * GET http://localhost:8080/api/trains/prochains
     */
    @GetMapping("/prochains")
    public ResponseEntity<List<Train>> getTrainsProchains() {
        List<Train> trains = trainService.getTrainsProchains();
        return new ResponseEntity<>(trains, HttpStatus.OK);  // HTTP 200
    }
    
    // === GESTION DES ERREURS ===
    
    /**
     * Gestionnaire d'exception pour les erreurs RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}/duree")
    public ResponseEntity<Map<String, Object>> getDureeVoyage(@PathVariable Long id) {
        try {
            Map<String, Object> duree = trainService.calculerDureeVoyage(id);
            return ResponseEntity.ok(duree);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
 // Add this to TrainController.java
    @GetMapping("/recherche-avancee")
    public ResponseEntity<List<Train>> rechercheAvancee(
            @RequestParam(required = false) String villeDepart,
            @RequestParam(required = false) String villeArrivee,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(required = false) Integer dureeMaxMinutes,
            @RequestParam(required = false) Boolean gareOuverteSeulement) {
        
        List<Train> trains = trainService.rechercheAvancee(
            villeDepart, 
            villeArrivee, 
            dateDebut, 
            dateFin, 
            dureeMaxMinutes, 
            gareOuverteSeulement
        );
        return ResponseEntity.ok(trains);
    }
 // Add this method to your TrainController.java

    /**
     * 15. RECHERCHE AVANCÉE TRAINS
     * GET http://localhost:8787/api/trains/recherche-avancee?villeDepart=Paris&villeArrivee=Lyon&dureeMaxMinutes=300
     */

}