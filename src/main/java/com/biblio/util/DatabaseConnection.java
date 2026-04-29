package com.biblio.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestionnaire de connexion SQLite (Singleton).
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class DatabaseConnection {
    private static final String DB_FILE = "biblio.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;
    private static final String DEMO_RESOURCE = "/biblio-demo.db";
    private static Connection instance;

    private DatabaseConnection() {}

    public static synchronized Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            extraireBaseDemoSiAbsente();
            instance = DriverManager.getConnection(DB_URL);
            try (Statement st = instance.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            initSchemaIfNeeded(instance);
        }
        return instance;
    }

    /**
     * Au tout premier lancement, si biblio.db n'existe pas dans le dossier courant,
     * on extrait la base de démonstration embarquée dans le JAR vers ./biblio.db.
     * Permet à l'utilisateur final de lancer l'application avec les données de démo
     * sans avoir à déposer manuellement de fichier .db à côté du JAR.
     */
    private static void extraireBaseDemoSiAbsente() throws SQLException {
        Path cible = Path.of(DB_FILE);
        if (Files.exists(cible)) return;
        try (InputStream in = DatabaseConnection.class.getResourceAsStream(DEMO_RESOURCE)) {
            if (in == null) return;
            Files.copy(in, cible, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new SQLException("Impossible d'extraire la base de démonstration : " + ex.getMessage(), ex);
        }
    }

    private static void initSchemaIfNeeded(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Reference (" +
                "id_reference INTEGER PRIMARY KEY AUTOINCREMENT," +
                "titre VARCHAR(255) NOT NULL," +
                "revue VARCHAR(255)," +
                "annee INTEGER NOT NULL CHECK (annee >= 1900)," +
                "hyperlien VARCHAR(500)," +
                "description TEXT)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Auteur (" +
                "id_auteur INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom VARCHAR(100) NOT NULL," +
                "prenom VARCHAR(100))");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Etiquette (" +
                "id_etiquette INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom VARCHAR(100) NOT NULL UNIQUE)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Reference_Auteur (" +
                "id_reference INTEGER NOT NULL," +
                "id_auteur INTEGER NOT NULL," +
                "PRIMARY KEY (id_reference, id_auteur)," +
                "FOREIGN KEY (id_reference) REFERENCES Reference(id_reference) ON DELETE CASCADE," +
                "FOREIGN KEY (id_auteur) REFERENCES Auteur(id_auteur) ON DELETE RESTRICT)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Reference_Etiquette (" +
                "id_reference INTEGER NOT NULL," +
                "id_etiquette INTEGER NOT NULL," +
                "PRIMARY KEY (id_reference, id_etiquette)," +
                "FOREIGN KEY (id_reference) REFERENCES Reference(id_reference) ON DELETE CASCADE," +
                "FOREIGN KEY (id_etiquette) REFERENCES Etiquette(id_etiquette) ON DELETE CASCADE)");
        }
    }
}
