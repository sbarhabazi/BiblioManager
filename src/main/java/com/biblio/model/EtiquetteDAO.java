package com.biblio.model;

import com.biblio.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour l'entité Etiquette.
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class EtiquetteDAO {

    public int ajouter(Etiquette e) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("INSERT INTO Etiquette(nom) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNom());
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) e.setIdEtiquette(k.getInt(1));
            }
        }
        return e.getIdEtiquette();
    }

    public void modifier(Etiquette e) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("UPDATE Etiquette SET nom=? WHERE id_etiquette=?")) {
            ps.setString(1, e.getNom());
            ps.setInt(2, e.getIdEtiquette());
            ps.executeUpdate();
        }
    }

    public void supprimer(int idEtiquette) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("DELETE FROM Etiquette WHERE id_etiquette=?")) {
            ps.setInt(1, idEtiquette);
            ps.executeUpdate();
        }
    }

    public List<Etiquette> listerTout() throws SQLException {
        List<Etiquette> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Etiquette ORDER BY nom")) {
            while (rs.next()) {
                Etiquette e = new Etiquette(rs.getString("nom"));
                e.setIdEtiquette(rs.getInt("id_etiquette"));
                list.add(e);
            }
        }
        return list;
    }
}
