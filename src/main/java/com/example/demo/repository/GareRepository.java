package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Gare;

import java.util.List;

@Repository
public interface GareRepository extends JpaRepository<Gare, Long> {
    List<Gare> findByVille(String ville);
    List<Gare> findByNomContainingIgnoreCase(String nom);
    List<Gare> findByEstOuverteTrue();
}


/*
┌─────────────────────────────────────────────────────┐
│                    CONTROLLER                        │ ← HTTP Requests/Responses
│  (GareController.java, TrainController.java)        │
└─────────────────────────────────────────────────────┘
            ↓ (appelle les services)
┌─────────────────────────────────────────────────────┐
│                     SERVICE                          │ ← Logique métier
│  (GareService.java, TrainService.java)              │
│  + leurs implémentations                             │
└─────────────────────────────────────────────────────┘
            ↓ (utilise les repositories)
┌─────────────────────────────────────────────────────┐
│                   REPOSITORY                         │ ← Accès aux données
│  (GareRepository.java, TrainRepository.java)        │
└─────────────────────────────────────────────────────┘
            ↓ (génère du SQL)
┌─────────────────────────────────────────────────────┐
│                 BASE DE DONNÉES                     │ ← MySQL
│      (Tables: gare, train)                          │
└─────────────────────────────────────────────────────┘
*/
