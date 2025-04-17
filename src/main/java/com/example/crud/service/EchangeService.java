package com.example.crud.service;

import com.example.crud.util.DatabaseConnection;
import com.example.crud.model.Echange;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EchangeService {
    // Create
    public void addEchange(Echange echange) throws SQLException {
        String sql = "INSERT INTO echange (id_objet, name_echange, image_echange, date_echange, message, statut) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, echange.getIdObjet());
            stmt.setString(2, echange.getNameEchange());
            stmt.setString(3, echange.getImageEchange());
            stmt.setTimestamp(4, Timestamp.valueOf(echange.getDateEchange()));
            stmt.setString(5, echange.getMessage());
            stmt.setString(6, echange.getStatut());
            stmt.executeUpdate();

            // Set the generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    echange.setIdEchange(rs.getInt(1));
                }
            }
        }
    }

    // Read (all)
    public List<Echange> getAllEchanges() throws SQLException {
        List<Echange> echanges = new ArrayList<>();
        String sql = "SELECT * FROM echange";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Echange echange = new Echange();
                echange.setIdEchange(rs.getInt("id_echange"));
                echange.setIdObjet(rs.getInt("id_objet"));
                echange.setNameEchange(rs.getString("name_echange"));
                echange.setImageEchange(rs.getString("image_echange"));
                echange.setDateEchange(rs.getTimestamp("date_echange").toLocalDateTime());
                echange.setMessage(rs.getString("message"));
                echange.setStatut(rs.getString("statut"));
                echanges.add(echange);
            }
        }
        return echanges;
    }

    // Read (by ID)
    public Echange getEchangeById(int id) throws SQLException {
        String sql = "SELECT * FROM echange WHERE id_echange = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Echange echange = new Echange();
                    echange.setIdEchange(rs.getInt("id_echange"));
                    echange.setIdObjet(rs.getInt("id_objet"));
                    echange.setNameEchange(rs.getString("name_echange"));
                    echange.setImageEchange(rs.getString("image_echange"));
                    echange.setDateEchange(rs.getTimestamp("date_echange").toLocalDateTime());
                    echange.setMessage(rs.getString("message"));
                    echange.setStatut(rs.getString("statut"));
                    return echange;
                }
            }
        }
        return null;
    }

    // Update
    public void updateEchange(Echange echange) throws SQLException {
        String sql = "UPDATE echange SET id_objet = ?, name_echange = ?, image_echange = ?, date_echange = ?, message = ?, statut = ? WHERE id_echange = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, echange.getIdObjet());
            stmt.setString(2, echange.getNameEchange());
            stmt.setString(3, echange.getImageEchange());
            stmt.setTimestamp(4, Timestamp.valueOf(echange.getDateEchange()));
            stmt.setString(5, echange.getMessage());
            stmt.setString(6, echange.getStatut());
            stmt.setInt(7, echange.getIdEchange());
            stmt.executeUpdate();
        }
    }

    // Delete
    public void deleteEchange(int id) throws SQLException {
        String sql = "DELETE FROM echange WHERE id_echange = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}