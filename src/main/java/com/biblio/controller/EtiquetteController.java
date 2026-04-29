package com.biblio.controller;

import com.biblio.model.Etiquette;
import com.biblio.model.EtiquetteDAO;
import java.sql.SQLException;
import java.util.List;

public class EtiquetteController {
    private final EtiquetteDAO dao = new EtiquetteDAO();

    public void ajouter(Etiquette e) throws SQLException {
        if (e.getNom() == null || e.getNom().trim().isEmpty())
            throw new IllegalArgumentException("Le nom de l'étiquette est obligatoire.");
        dao.ajouter(e);
    }
    public void modifier(Etiquette e) throws SQLException { dao.modifier(e); }
    public void supprimer(int id) throws SQLException { dao.supprimer(id); }
    public List<Etiquette> lister() throws SQLException { return dao.listerTout(); }
}
