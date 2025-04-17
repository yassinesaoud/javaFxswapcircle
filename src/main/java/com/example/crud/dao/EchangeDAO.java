package com.example.crud.dao;

import com.example.crud.model.Echange;
import com.example.crud.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EchangeDAO {
    
    public List<Echange> getAllEchanges() throws SQLException {
        List<Echange> echanges = new ArrayList<>();
        String query = "SELECT * FROM echanges";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
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
    
    public void saveEchange(Echange echange) throws SQLException {
        String query = "INSERT INTO echanges (id_objet, name_echange, image_echange, date_echange, message, statut) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, echange.getIdObjet());
            pstmt.setString(2, echange.getNameEchange());
            pstmt.setString(3, echange.getImageEchange());
            pstmt.setTimestamp(4, Timestamp.valueOf(echange.getDateEchange()));
            pstmt.setString(5, echange.getMessage());
            pstmt.setString(6, echange.getStatut());
            
            pstmt.executeUpdate();
        }
    }
    
    public void updateEchange(Echange echange) throws SQLException {
        String query = "UPDATE echanges SET id_objet=?, name_echange=?, message=?, statut=? WHERE id_echange=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, echange.getIdObjet());
            pstmt.setString(2, echange.getNameEchange());
            pstmt.setString(3, echange.getMessage());
            pstmt.setString(4, echange.getStatut());
            pstmt.setInt(5, echange.getIdEchange());
            
            pstmt.executeUpdate();
        }
    }
    
    public void deleteEchange(int id) throws SQLException {
        String query = "DELETE FROM echanges WHERE id_echange=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
} 