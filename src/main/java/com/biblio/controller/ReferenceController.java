package com.biblio.controller;

import com.biblio.model.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Contrôleur principal pour la gestion des références.
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class ReferenceController {
    private final ReferenceDAO refDao = new ReferenceDAO();
    private final AuteurDAO auteurDao = new AuteurDAO();
    private final EtiquetteDAO etiquetteDao = new EtiquetteDAO();

    public void ajouterReference(Reference r, List<Auteur> auteurs, List<Etiquette> etiquettes) throws SQLException {
        validerReference(r);
        if (auteurs == null || auteurs.isEmpty())
            throw new IllegalArgumentException("Au moins un auteur est obligatoire.");
        refDao.ajouter(r);
        for (Auteur a : auteurs) refDao.associerAuteur(r.getIdReference(), a.getIdAuteur());
        if (etiquettes != null)
            for (Etiquette e : etiquettes) refDao.associerEtiquette(r.getIdReference(), e.getIdEtiquette());
    }

    public void modifierReference(Reference r) throws SQLException {
        validerReference(r);
        refDao.modifier(r);
    }

    public void supprimerReference(int idReference) throws SQLException {
        refDao.supprimer(idReference);
    }

    public List<Reference> listerReferences() throws SQLException {
        List<Reference> refs = refDao.listerTout();
        for (Reference r : refs) {
            r.setAuteurs(refDao.listerAuteursDe(r.getIdReference()));
            r.setEtiquettes(refDao.listerEtiquettesDe(r.getIdReference()));
        }
        return refs;
    }

    public List<Reference> rechercherParEtiquette(String nomEtiquette) throws SQLException {
        List<Reference> refs = refDao.rechercherParEtiquette(nomEtiquette);
        for (Reference r : refs) {
            r.setAuteurs(refDao.listerAuteursDe(r.getIdReference()));
            r.setEtiquettes(refDao.listerEtiquettesDe(r.getIdReference()));
        }
        return refs;
    }

    public List<Auteur> listerAuteurs() throws SQLException { return auteurDao.listerTout(); }
    public List<Etiquette> listerEtiquettes() throws SQLException { return etiquetteDao.listerTout(); }

    /**
     * Remplace intégralement les auteurs et étiquettes associés à une référence.
     * Utilisé par la boîte de dialogue lors d'une modification.
     */
    public void resynchroniserLiens(int idReference, List<Auteur> auteurs, List<Etiquette> etiquettes)
            throws SQLException {
        if (auteurs == null || auteurs.isEmpty())
            throw new IllegalArgumentException("Au moins un auteur est obligatoire.");
        refDao.supprimerTousLesLiens(idReference);
        for (Auteur a : auteurs) refDao.associerAuteur(idReference, a.getIdAuteur());
        if (etiquettes != null)
            for (Etiquette e : etiquettes) refDao.associerEtiquette(idReference, e.getIdEtiquette());
    }

    private void validerReference(Reference r) {
        if (r.getTitre() == null || r.getTitre().trim().isEmpty())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        int anneeCourante = java.time.Year.now().getValue();
        if (r.getAnnee() < 1900 || r.getAnnee() > anneeCourante)
            throw new IllegalArgumentException("L'année doit être comprise entre 1900 et " + anneeCourante + ".");
        if (r.getHyperlien() != null && !r.getHyperlien().isEmpty()
                && !r.getHyperlien().matches("^https?://.+"))
            throw new IllegalArgumentException("L'hyperlien doit commencer par http:// ou https://.");
    }
}
