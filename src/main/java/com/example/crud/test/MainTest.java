package com.example.crud.test;
import com.example.crud.model.Echange;
import com.example.crud.model.Objet;
import com.example.crud.service.EchangeService;
import com.example.crud.service.ObjetService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
public class MainTest {
    public static void main(String[] args) {
        try {
            // Initialize services
            ObjetService objetService = new ObjetService();
            EchangeService echangeService = new EchangeService();

            // === Test ObjetService CRUD ===
            System.out.println("=== Testing ObjetService CRUD ===");

            // Create
            Objet objet = new Objet(
                    0, // ID will be auto-generated
                    "Chaise",
                    "Une chaise en bois",
                    "Neuf",
                    LocalDateTime.now(),
                    "chaise.jpg",
                    "Meubles"
            );
            objetService.addObjet(objet);
            System.out.println("Created Objet: " + objet.getNom() + " (ID: " + objet.getIdObjet() + ")");

            // Read
            Objet addedObjet = objetService.getObjetById(objet.getIdObjet());
            if (addedObjet == null) {
                System.out.println("No objet found with ID: " + objet.getIdObjet());
                return;
            }
            System.out.println("Read Objet: ID=" + addedObjet.getIdObjet() + ", Nom=" + addedObjet.getNom());

            List<Objet> objets = objetService.getAllObjets();
            System.out.println("All Objets (" + objets.size() + "):");
            objets.forEach(o -> System.out.println(" - " + o.getNom() + " (ID: " + o.getIdObjet() + ")"));

            // Update
            addedObjet.setDescription("Chaise en bois de haute qualité");
            addedObjet.setEtat("Comme neuf");
            objetService.updateObjet(addedObjet);
            Objet updatedObjet = objetService.getObjetById(addedObjet.getIdObjet());
            System.out.println("Updated Objet: ID=" + updatedObjet.getIdObjet() + ", Desc=" + updatedObjet.getDescription());

            // === Test EchangeService CRUD ===
            System.out.println("\n=== Testing EchangeService CRUD ===");

            // Create
            Echange echange = new Echange(
                    0, // ID will be auto-generated
                    addedObjet.getIdObjet(), // Link to the created Objet
                    "Échange Chaise",
                    "echange_chaise.jpg",
                    LocalDateTime.now(),
                    "Proposition d'échange pour une chaise",
                    "En attente"
            );
            echangeService.addEchange(echange);
            System.out.println("Created Echange: " + echange.getNameEchange() + " (ID: " + echange.getIdEchange() + ")");

            // Read
            Echange addedEchange = echangeService.getEchangeById(echange.getIdEchange());
            if (addedEchange == null) {
                System.out.println("No echange found with ID: " + echange.getIdEchange());
                return;
            }
            System.out.println("Read Echange: ID=" + addedEchange.getIdEchange() + ", Name=" + addedEchange.getNameEchange());

            List<Echange> echanges = echangeService.getAllEchanges();
            System.out.println("All Echanges (" + echanges.size() + "):");
            echanges.forEach(e -> System.out.println(" - " + e.getNameEchange() + " (ID: " + e.getIdEchange() + ")"));

            // Update
            addedEchange.setStatut("Terminé");
            addedEchange.setMessage("Échange finalisé avec succès");
            echangeService.updateEchange(addedEchange);
            Echange updatedEchange = echangeService.getEchangeById(addedEchange.getIdEchange());
            System.out.println("Updated Echange: ID=" + updatedEchange.getIdEchange() + ", Statut=" + updatedEchange.getStatut());

            // Delete Echange
         //   echangeService.deleteEchange(addedEchange.getIdEchange());
           // System.out.println("Deleted Echange ID: " + addedEchange.getIdEchange());
            //System.out.println("Echanges after deletion: " + echangeService.getAllEchanges());

            // Delete Objet (after Echange, due to foreign key)
            //objetService.deleteObjet(addedObjet.getIdObjet());
            //System.out.println("Deleted Objet ID: " + addedObjet.getIdObjet());
            //System.out.println("Objets after deletion: " + objetService.getAllObjets());

            // Final State
            System.out.println("\n=== Final State ===");
            System.out.println("Objets: " + objetService.getAllObjets());
            System.out.println("Echanges: " + echangeService.getAllEchanges());

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}