package com.example.demo.controller;

import com.example.demo.model.Gare;
import com.example.demo.service.GareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gares")
@CrossOrigin(origins = "*")
public class GareController {

    @Autowired
    private GareService gareService;

    // ============ CRUD ENDPOINTS ============

    // 1. TEST CONNEXION API & 3. LISTER TOUTES LES GARES
    @GetMapping
    public ResponseEntity<List<Gare>> getAllGares() {
        try {
            List<Gare> gares = gareService.getAllGares();
            return ResponseEntity.ok(gares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 2. CRÉER UNE GARE
    @PostMapping
    public ResponseEntity<?> createGare(@RequestBody Gare gare) {
        try {
            if (gare.getNom() == null || gare.getNom().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Le nom de la gare est obligatoire"));
            }
            if (gare.getVille() == null || gare.getVille().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La ville est obligatoire"));
            }
            
            Gare createdGare = gareService.createGare(gare);
            return new ResponseEntity<>(createdGare, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la création de la gare: " + e.getMessage()));
        }
    }

    // 4. OBTENIR UNE GARE PAR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getGareById(@PathVariable Long id) {
        try {
            Gare gare = gareService.getGareById(id);
            return ResponseEntity.ok(gare);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur interne du serveur"));
        }
    }

    // 5. METTRE À JOUR UNE GARE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGare(@PathVariable Long id, @RequestBody Gare gareDetails) {
        try {
            if (gareDetails.getNom() == null || gareDetails.getNom().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Le nom de la gare est obligatoire"));
            }
            
            Gare updatedGare = gareService.updateGare(id, gareDetails);
            return ResponseEntity.ok(updatedGare);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    // 10. SUPPRIMER UNE GARE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGare(@PathVariable Long id) {
        try {
            gareService.deleteGare(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Gare supprimée avec succès");
            response.put("gareId", id);
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la suppression: " + e.getMessage()));
        }
    }

    // ============ SEARCH & FILTER ENDPOINTS ============

    // 11. GARES PAR VILLE
    @GetMapping("/ville/{ville}")
    public ResponseEntity<?> getGaresByVille(@PathVariable String ville) {
        try {
            if (ville == null || ville.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Le paramètre 'ville' est requis"));
            }
            
            List<Gare> gares = gareService.getGaresByVille(ville);
            return ResponseEntity.ok(gares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la recherche par ville"));
        }
    }

    // 12. GARES OUVERTES
    @GetMapping("/ouvertes")
    public ResponseEntity<?> getGaresOuvertes() {
        try {
            List<Gare> gares = gareService.getGaresOuvertes();
            return ResponseEntity.ok(gares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la récupération des gares ouvertes"));
        }
    }

    // Recherche par nom (BONUS)
    @GetMapping("/recherche")
    public ResponseEntity<?> searchGaresByNom(@RequestParam String nom) {
        try {
            if (nom == null || nom.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Le paramètre 'nom' est requis"));
            }
            
            List<Gare> gares = gareService.getGaresByNom(nom);
            return ResponseEntity.ok(gares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la recherche par nom"));
        }
    }

    // ============ STATISTICS & ANALYTICS ENDPOINTS ============

    // 13. STATISTIQUES GARE
    @GetMapping("/{id}/statistiques")
    public ResponseEntity<?> getStatistiquesGare(@PathVariable Long id) {
        try {
            Map<String, Object> stats = gareService.getStatistiquesGare(id);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors du calcul des statistiques"));
        }
    }

    // Statistiques globales (BONUS)
    @GetMapping("/statistiques/globales")
    public ResponseEntity<?> getStatistiquesGlobales() {
        try {
            Map<String, Object> stats = gareService.getStatistiquesGlobales();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors du calcul des statistiques globales"));
        }
    }

    // ============ BULK OPERATIONS ============

    // Créer plusieurs gares en une fois (BONUS)
    @PostMapping("/batch")
    public ResponseEntity<?> createMultipleGares(@RequestBody List<Gare> gares) {
        try {
            if (gares == null || gares.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("La liste des gares ne peut pas être vide"));
            }
            
            List<Gare> createdGares = gareService.createMultipleGares(gares);
            return new ResponseEntity<>(createdGares, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la création multiple: " + e.getMessage()));
        }
    }

    // ============ HEALTH & INFO ENDPOINTS ============

    // Vérifier si une gare existe
    @GetMapping("/{id}/exists")
    public ResponseEntity<?> checkGareExists(@PathVariable Long id) {
        try {
            boolean exists = gareService.gareExists(id);
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("gareId", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la vérification"));
        }
    }

    // Compter le nombre total de gares
    @GetMapping("/count")
    public ResponseEntity<?> countGares() {
        try {
            long count = gareService.countGares();
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors du comptage"));
        }
    }

    // ============ SPECIFIC BUSINESS LOGIC ============

    // Gares avec plus de X voies
    @GetMapping("/voies/min/{minVoies}")
    public ResponseEntity<?> getGaresByMinVoies(@PathVariable Integer minVoies) {
        try {
            if (minVoies == null || minVoies < 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("Le nombre minimal de voies doit être positif"));
            }
            
            List<Gare> gares = gareService.getGaresByMinVoies(minVoies);
            return ResponseEntity.ok(gares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la recherche par nombre de voies"));
        }
    }

    // Fermer/ouvrir une gare
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateGareStatus(
            @PathVariable Long id,
            @RequestParam Boolean estOuverte) {
        try {
            if (estOuverte == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Le paramètre 'estOuverte' est requis"));
            }
            
            Gare updatedGare = gareService.updateGareStatus(id, estOuverte);
            return ResponseEntity.ok(updatedGare);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors du changement de statut"));
        }
    }

    // Gares par région
    @GetMapping("/region/{region}")
    public ResponseEntity<?> getGaresByRegion(@PathVariable String region) {
        try {
            if (region == null || region.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Le paramètre 'region' est requis"));
            }
            
            List<Gare> gares = gareService.getGaresByRegion(region);
            return ResponseEntity.ok(gares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la recherche par région"));
        }
    }

    // ============ HELPER METHODS ============

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", LocalDateTime.now().toString());
        return error;
    }
}