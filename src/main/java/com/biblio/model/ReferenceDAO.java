package com.biblio.model;

import com.biblio.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour l'entité Reference.
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class ReferenceDAO {

    public int ajouter(Reference r) throws SQLException {
        String sql = "INSERT INTO Reference(titre,revue,annee,hyperlien,description) VALUES (?,?,?,?,?)";
        Connection c = DatabaseConnection.getInstance();
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getTitre());
            ps.setString(2, r.getRevue());
            ps.setInt(3, r.getAnnee());
            ps.setString(4, r.getHyperlien());
            ps.setString(5, r.getDescription());
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) r.setIdReference(k.getInt(1));
            }
        }
        return r.getIdReference();
    }

    public void modifier(Reference r) throws SQLException {
        String sql = "UPDATE Reference SET titre=?,revue=?,annee=?,hyperlien=?,description=? WHERE id_reference=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, r.getTitre());
            ps.setString(2, r.getRevue());
            ps.setInt(3, r.getAnnee());
            ps.setString(4, r.getHyperlien());
            ps.setString(5, r.getDescription());
            ps.setInt(6, r.getIdReference());
            ps.executeUpdate();
        }
    }

    public void supprimer(int idReference) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("DELETE FROM Reference WHERE id_reference=?")) {
            ps.setInt(1, idReference);
            ps.executeUpdate();
        }
    }

    public List<Reference> listerTout() throws SQLException {
        List<Reference> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Reference ORDER BY annee DESC")) {
            while (rs.next()) list.add(mapper(rs));
        }
        return list;
    }

    public List<Reference> rechercherParEtiquette(String nomEtiquette) throws SQLException {
        String sql = "SELECT r.* FROM Reference r " +
                "INNER JOIN Reference_Etiquette re ON r.id_reference = re.id_reference " +
                "INNER JOIN Etiquette e ON re.id_etiquette = e.id_etiquette " +
                "WHERE LOWER(e.nom) = LOWER(?) ORDER BY r.annee DESC";
        List<Reference> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, nomEtiquette);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        }
        return list;
    }

    public void associerAuteur(int idReference, int idAuteur) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("INSERT OR IGNORE INTO Reference_Auteur VALUES (?,?)")) {
            ps.setInt(1, idReference); ps.setInt(2, idAuteur); ps.executeUpdate();
        }
    }

    public void associerEtiquette(int idReference, int idEtiquette) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("INSERT OR IGNORE INTO Reference_Etiquette VALUES (?,?)")) {
            ps.setInt(1, idReference); ps.setInt(2, idEtiquette); ps.executeUpdate();
        }
    }

    public void supprimerTousLesLiens(int idReference) throws SQLException {
        Connection c = DatabaseConnection.getInstance();
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM Reference_Auteur WHERE id_reference=?")) {
            ps.setInt(1, idReference); ps.executeUpdate();
        }
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM Reference_Etiquette WHERE id_reference=?")) {
            ps.setInt(1, idReference); ps.executeUpdate();
        }
    }

    public void dissocierEtiquette(int idReference, int idEtiquette) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .prepareStatement("DELETE FROM Reference_Etiquette WHERE id_reference=? AND id_etiquette=?")) {
            ps.setInt(1, idReference); ps.setInt(2, idEtiquette); ps.executeUpdate();
        }
    }

    public List<Auteur> listerAuteursDe(int idReference) throws SQLException {
        List<Auteur> list = new ArrayList<>();
        String sql = "SELECT a.* FROM Auteur a INNER JOIN Reference_Auteur ra " +
                "ON a.id_auteur = ra.id_auteur WHERE ra.id_reference=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, idReference);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Auteur a = new Auteur(rs.getString("nom"), rs.getString("prenom"));
                    a.setIdAuteur(rs.getInt("id_auteur"));
                    list.add(a);
                }
            }
        }
        return list;
    }

    public List<Etiquette> listerEtiquettesDe(int idReference) throws SQLException {
        List<Etiquette> list = new ArrayList<>();
        String sql = "SELECT e.* FROM Etiquette e INNER JOIN Reference_Etiquette re " +
                "ON e.id_etiquette = re.id_etiquette WHERE re.id_reference=?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, idReference);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Etiquette e = new Etiquette(rs.getString("nom"));
                    e.setIdEtiquette(rs.getInt("id_etiquette"));
                    list.add(e);
                }
            }
        }
        return list;
    }

    private Reference mapper(ResultSet rs) throws SQLException {
        Reference r = new Reference(
            rs.getString("titre"), rs.getString("revue"),
            rs.getInt("annee"), rs.getString("hyperlien"),
            rs.getString("description"));
        r.setIdReference(rs.getInt("id_reference"));
        return r;
    }
}
