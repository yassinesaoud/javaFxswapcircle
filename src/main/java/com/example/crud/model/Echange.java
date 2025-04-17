package com.example.crud.model;

import java.time.LocalDateTime;
public class Echange {
    private int idEchange;
    private int idObjet;
    private String nameEchange;
    private String imageEchange;
    private LocalDateTime dateEchange;
    private String message;
    private String statut;

    // Constructor
    public Echange() {}

    public Echange(int idEchange, int idObjet, String nameEchange, String imageEchange, LocalDateTime dateEchange, String message, String statut) {
        this.idEchange = idEchange;
        this.idObjet = idObjet;
        this.nameEchange = nameEchange;
        this.imageEchange = imageEchange;
        this.dateEchange = dateEchange;
        this.message = message;
        this.statut = statut;
    }

    // Getters and Setters
    public int getIdEchange() { return idEchange; }
    public void setIdEchange(int idEchange) { this.idEchange = idEchange; }
    public int getIdObjet() { return idObjet; }
    public void setIdObjet(int idObjet) { this.idObjet = idObjet; }
    public String getNameEchange() { return nameEchange; }
    public void setNameEchange(String nameEchange) { this.nameEchange = nameEchange; }
    public String getImageEchange() { return imageEchange; }
    public void setImageEchange(String imageEchange) { this.imageEchange = imageEchange; }
    public LocalDateTime getDateEchange() { return dateEchange; }
    public void setDateEchange(LocalDateTime dateEchange) { this.dateEchange = dateEchange; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}