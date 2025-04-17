package com.example.crud.service;

import com.example.crud.util.DatabaseConnection;
import com.example.crud.model.Objet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetService {
    // Create
    public void addObjet(Objet objet) throws SQLException {
        String sql = "INSERT INTO objet (nom, description, etat, date_ajout, image, categorie) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objet.getNom());
            stmt.setString(2, objet.getDescription());
            stmt.setString(3, objet.getEtat());
            stmt.setTimestamp(4, Timestamp.valueOf(objet.getDateAjout()));
            stmt.setString(5, objet.getImage());
            stmt.setString(6, objet.getCategorie());
            stmt.executeUpdate();

            // Set the generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objet.setIdObjet(rs.getInt(1));
                }
            }
        }
    }

    // Read (all)
    public List<Objet> getAllObjets() throws SQLException {
        List<Objet> objets = new ArrayList<>();
        String sql = "SELECT * FROM objet";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Objet objet = new Objet();
                objet.setIdObjet(rs.getInt("id_objet"));
                objet.setNom(rs.getString("nom"));
                objet.setDescription(rs.getString("description"));
                objet.setEtat(rs.getString("etat"));
                objet.setDateAjout(rs.getTimestamp("date_ajout").toLocalDateTime());
                objet.setImage(rs.getString("image"));
                objet.setCategorie(rs.getString("categorie"));
                objets.add(objet);
            }
        }
        return objets;
    }

    // Read (by ID)
    public Objet getObjetById(int id) throws SQLException {
        String sql = "SELECT * FROM objet WHERE id_objet = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Objet objet = new Objet();
                    objet.setIdObjet(rs.getInt("id_objet"));
                    objet.setNom(rs.getString("nom"));
                    objet.setDescription(rs.getString("description"));
                    objet.setEtat(rs.getString("etat"));
                    objet.setDateAjout(rs.getTimestamp("date_ajout").toLocalDateTime());
                    objet.setImage(rs.getString("image"));
                    objet.setCategorie(rs.getString("categorie"));
                    return objet;
                }
            }
        }
        return null;
    }

    // Update
    public void updateObjet(Objet objet) throws SQLException {
        String sql = "UPDATE objet SET nom = ?, description = ?, etat = ?, date_ajout = ?, image = ?, categorie = ? WHERE id_objet = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objet.getNom());
            stmt.setString(2, objet.getDescription());
            stmt.setString(3, objet.getEtat());
            stmt.setTimestamp(4, Timestamp.valueOf(objet.getDateAjout()));
            stmt.setString(5, objet.getImage());
            stmt.setString(6, objet.getCategorie());
            stmt.setInt(7, objet.getIdObjet());
            stmt.executeUpdate();
        }
    }

    // Delete
    public void deleteObjet(int id) throws SQLException {
        String sql = "DELETE FROM objet WHERE id_objet = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}