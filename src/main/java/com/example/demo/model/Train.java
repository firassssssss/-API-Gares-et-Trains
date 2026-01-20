package com.example.demo.model;



import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "train")
public class Train {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_train", nullable = false, unique = true)
    private String numeroTrain;
    
    @Column(name = "heure_depart", nullable = false)
    private LocalDateTime heureDepart;
    
    @Column(name = "heure_arrivee", nullable = false)
    private LocalDateTime heureArrivee;
    
    @ManyToOne
    @JoinColumn(name = "gare_depart_id", nullable = false)
    @JsonIgnoreProperties({"departs", "arrivees"})  // ← AJOUTEZ ICI
    private Gare gareDepart;
    
    @ManyToOne
    @JoinColumn(name = "gare_arrivee_id", nullable = false)
    @JsonIgnoreProperties({"departs", "arrivees"})  // ← AJOUTEZ ICI AUSSI
    private Gare gareArrivee ;
    
  
    public Train() {}
    
    public Train(String numeroTrain, LocalDateTime heureDepart, LocalDateTime heureArrivee, 
                 Gare gareDepart, Gare gareArrivee) {
        this.numeroTrain = numeroTrain;
        this.heureDepart = heureDepart;
        this.heureArrivee = heureArrivee;
        this.gareDepart = gareDepart;
        this.gareArrivee = gareArrivee;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumeroTrain() { return numeroTrain; }
    public void setNumeroTrain(String numeroTrain) { this.numeroTrain = numeroTrain; }
    
    public LocalDateTime getHeureDepart() { return heureDepart; }
    public void setHeureDepart(LocalDateTime heureDepart) { this.heureDepart = heureDepart; }
    
    public LocalDateTime getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(LocalDateTime heureArrivee) { this.heureArrivee = heureArrivee; }
    
    public Gare getGareDepart() { return gareDepart; }
    public void setGareDepart(Gare gareDepart) { this.gareDepart = gareDepart; }
    
    public Gare getGareArrivee() { return gareArrivee; }
    public void setGareArrivee(Gare gareArrivee) { this.gareArrivee = gareArrivee; }
}