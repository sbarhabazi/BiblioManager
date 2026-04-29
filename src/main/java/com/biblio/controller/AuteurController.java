package com.biblio.controller;

import com.biblio.model.Auteur;
import com.biblio.model.AuteurDAO;
import java.sql.SQLException;
import java.util.List;

public class AuteurController {
    private final AuteurDAO dao = new AuteurDAO();

    public void ajouter(Auteur a) throws SQLException {
        if (a.getNom() == null || a.getNom().trim().isEmpty())
            throw new IllegalArgumentException("Le nom de l'auteur est obligatoire.");
        dao.ajouter(a);
    }
    public void modifier(Auteur a) throws SQLException { dao.modifier(a); }
    public void supprimer(int id) throws SQLException { dao.supprimer(id); }
    public List<Auteur> lister() throws SQLException { return dao.listerTout(); }
}
