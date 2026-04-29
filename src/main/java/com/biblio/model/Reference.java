package com.biblio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité POJO représentant une référence bibliographique.
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class Reference {
    private int idReference;
    private String titre;
    private String revue;
    private int annee;
    private String hyperlien;
    private String description;
    private List<Auteur> auteurs = new ArrayList<>();
    private List<Etiquette> etiquettes = new ArrayList<>();

    public Reference() {}

    public Reference(String titre, String revue, int annee, String hyperlien, String description) {
        this.titre = titre;
        this.revue = revue;
        this.annee = annee;
        this.hyperlien = hyperlien;
        this.description = description;
    }

    public int getIdReference() { return idReference; }
    public void setIdReference(int idReference) { this.idReference = idReference; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getRevue() { return revue; }
    public void setRevue(String revue) { this.revue = revue; }
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    public String getHyperlien() { return hyperlien; }
    public void setHyperlien(String hyperlien) { this.hyperlien = hyperlien; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Auteur> getAuteurs() { return auteurs; }
    public void setAuteurs(List<Auteur> auteurs) { this.auteurs = auteurs; }
    public List<Etiquette> getEtiquettes() { return etiquettes; }
    public void setEtiquettes(List<Etiquette> etiquettes) { this.etiquettes = etiquettes; }

    @Override
    public String toString() {
        return titre + " (" + annee + ")";
    }
}
