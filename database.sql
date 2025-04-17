-- Create the database
CREATE DATABASE IF NOT EXISTS crud_db;
USE crud_db;

-- Create objets table
CREATE TABLE IF NOT EXISTS objets (
    id_objet INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    etat VARCHAR(50) NOT NULL,
    date_ajout DATETIME NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    image VARCHAR(255)
);

-- Create echanges table
CREATE TABLE IF NOT EXISTS echanges (
    id_echange INT PRIMARY KEY AUTO_INCREMENT,
    id_objet INT NOT NULL,
    name_echange VARCHAR(100) NOT NULL,
    image_echange VARCHAR(255),
    date_echange DATETIME NOT NULL,
    message TEXT,
    statut VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_objet) REFERENCES objets(id_objet)
); 