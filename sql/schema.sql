-- ============================================
-- DDL : Création du schéma de la base de données
-- Projet No 2 – Gestion de références bibliographiques
-- INF 4018 – Samuel Buhashe Barhabazi
-- ============================================

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Reference (
    id_reference  INTEGER PRIMARY KEY AUTOINCREMENT,
    titre         VARCHAR(255) NOT NULL,
    revue         VARCHAR(255),
    annee         INTEGER NOT NULL CHECK (annee >= 1900),
    hyperlien     VARCHAR(500),
    description   TEXT
);

CREATE TABLE IF NOT EXISTS Auteur (
    id_auteur  INTEGER PRIMARY KEY AUTOINCREMENT,
    nom        VARCHAR(100) NOT NULL,
    prenom     VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS Etiquette (
    id_etiquette  INTEGER PRIMARY KEY AUTOINCREMENT,
    nom           VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Reference_Auteur (
    id_reference  INTEGER NOT NULL,
    id_auteur     INTEGER NOT NULL,
    PRIMARY KEY (id_reference, id_auteur),
    FOREIGN KEY (id_reference) REFERENCES Reference(id_reference)
        ON DELETE CASCADE,
    FOREIGN KEY (id_auteur) REFERENCES Auteur(id_auteur)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS Reference_Etiquette (
    id_reference  INTEGER NOT NULL,
    id_etiquette  INTEGER NOT NULL,
    PRIMARY KEY (id_reference, id_etiquette),
    FOREIGN KEY (id_reference) REFERENCES Reference(id_reference)
        ON DELETE CASCADE,
    FOREIGN KEY (id_etiquette) REFERENCES Etiquette(id_etiquette)
        ON DELETE CASCADE
);

-- Index pour accélérer la recherche par étiquette
CREATE INDEX IF NOT EXISTS idx_etiquette_nom ON Etiquette(nom);
CREATE INDEX IF NOT EXISTS idx_ref_etiq_etiq ON Reference_Etiquette(id_etiquette);
