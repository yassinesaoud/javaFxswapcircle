package com.example.crud.model;

import java.time.LocalDateTime;
public class Objet {
    private int idObjet;
    private String nom;
    private String description;
    private String etat;
    private LocalDateTime dateAjout;
    private String image;
    private String categorie;

    // Constructor
    public Objet() {}

    public Objet(int idObjet, String nom, String description, String etat, LocalDateTime dateAjout, String image, String categorie) {
        this.idObjet = idObjet;
        this.nom = nom;
        this.description = description;
        this.etat = etat;
        this.dateAjout = dateAjout;
        this.image = image;
        this.categorie = categorie;
    }

    // Getters and Setters
    public int getIdObjet() { return idObjet; }
    public void setIdObjet(int idObjet) { this.idObjet = idObjet; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public LocalDateTime getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
}