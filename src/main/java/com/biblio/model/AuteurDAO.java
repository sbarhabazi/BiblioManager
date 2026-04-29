package com.biblio.model;

import com.biblio.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour l'entité Auteur.
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class AuteurDAO {

    public int ajouter(Auteur a) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("INSERT INTO Auteur(nom,prenom) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) a.setIdAuteur(k.getInt(1));
            }
        }
        return a.getIdAuteur();
    }

    public void modifier(Auteur a) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("UPDATE Auteur SET nom=?,prenom=? WHERE id_auteur=?")) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.setInt(3, a.getIdAuteur());
            ps.executeUpdate();
        }
    }

    public void supprimer(int idAuteur) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("DELETE FROM Auteur WHERE id_auteur=?")) {
            ps.setInt(1, idAuteur);
            ps.executeUpdate();
        }
    }

    public List<Auteur> listerTout() throws SQLException {
        List<Auteur> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Auteur ORDER BY nom, prenom")) {
            while (rs.next()) {
                Auteur a = new Auteur(rs.getString("nom"), rs.getString("prenom"));
                a.setIdAuteur(rs.getInt("id_auteur"));
                list.add(a);
            }
        }
        return list;
    }
}
