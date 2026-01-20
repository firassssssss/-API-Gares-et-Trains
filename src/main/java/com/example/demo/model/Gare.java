package com.example.demo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;  // ← AJOUTÉ

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "gare")
public class Gare {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String ville;
    
    private Integer nombreVoies;
    private Boolean estOuverte = true;
    
    @OneToMany(mappedBy = "gareDepart", cascade = CascadeType.ALL)
    @JsonIgnore  // ← AJOUTEZ ICI
    private List<Train> departs = new ArrayList<>();
    
    @OneToMany(mappedBy = "gareArrivee", cascade = CascadeType.ALL)
    @JsonIgnore  // ← AJOUTEZ ICI
    private List<Train> arrivees = new ArrayList<>();
    
    public Gare() {}
    
    public Gare(String nom, String ville, Integer nombreVoies, Boolean estOuverte) {
        this.nom = nom;
        this.ville = ville;
        this.nombreVoies = nombreVoies;
        this.estOuverte = estOuverte;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    
    public Integer getNombreVoies() { return nombreVoies; }
    public void setNombreVoies(Integer nombreVoies) { this.nombreVoies = nombreVoies; }
    
    public Boolean getEstOuverte() { return estOuverte; }
    public void setEstOuverte(Boolean estOuverte) { this.estOuverte = estOuverte; }
    
    public List<Train> getDeparts() { return departs; }
    public void setDeparts(List<Train> departs) { this.departs = departs; }
    
    public List<Train> getArrivees() { return arrivees; }
    public void setArrivees(List<Train> arrivees) { this.arrivees = arrivees; }
}