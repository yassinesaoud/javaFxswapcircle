package com.example.crud.dao;

import com.example.crud.model.Objet;
import com.example.crud.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetDAO {
    
    public List<Objet> getAllObjets() throws SQLException {
        List<Objet> objets = new ArrayList<>();
        String query = "SELECT * FROM objets";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Objet objet = new Objet();
                objet.setIdObjet(rs.getInt("id_objet"));
                objet.setNom(rs.getString("nom"));
                objet.setDescription(rs.getString("description"));
                objet.setEtat(rs.getString("etat"));
                objet.setDateAjout(rs.getTimestamp("date_ajout").toLocalDateTime());
                objet.setCategorie(rs.getString("categorie"));
                objet.setImage(rs.getString("image"));
                objets.add(objet);
            }
        }
        return objets;
    }
    
    public void saveObjet(Objet objet) throws SQLException {
        String query = "INSERT INTO objets (nom, description, etat, date_ajout, categorie, image) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, objet.getNom());
            pstmt.setString(2, objet.getDescription());
            pstmt.setString(3, objet.getEtat());
            pstmt.setTimestamp(4, Timestamp.valueOf(objet.getDateAjout()));
            pstmt.setString(5, objet.getCategorie());
            pstmt.setString(6, objet.getImage());
            
            pstmt.executeUpdate();
        }
    }
    
    public void updateObjet(Objet objet) throws SQLException {
        String query = "UPDATE objets SET nom=?, description=?, etat=?, categorie=?, image=? WHERE id_objet=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, objet.getNom());
            pstmt.setString(2, objet.getDescription());
            pstmt.setString(3, objet.getEtat());
            pstmt.setString(4, objet.getCategorie());
            pstmt.setString(5, objet.getImage());
            pstmt.setInt(6, objet.getIdObjet());
            
            pstmt.executeUpdate();
        }
    }
    
    public void deleteObjet(int id) throws SQLException {
        String query = "DELETE FROM objets WHERE id_objet=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
} 