-- Create objets table
CREATE TABLE IF NOT EXISTS objets (
    id_objet INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    etat VARCHAR(50) NOT NULL,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    categorie VARCHAR(50) NOT NULL,
    image VARCHAR(255)
);

-- Add image column if it doesn't exist
ALTER TABLE objets
ADD COLUMN IF NOT EXISTS image VARCHAR(255);

-- Create echanges table
CREATE TABLE IF NOT EXISTS echanges (
    id_echange INT PRIMARY KEY AUTO_INCREMENT,
    id_objet INT NOT NULL,
    name_echange VARCHAR(255) NOT NULL,
    message TEXT,
    statut VARCHAR(50) NOT NULL DEFAULT 'En attente',
    date_echange TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_objet) REFERENCES objets(id_objet)
); 