package com.biblio.model;

/**
 * Entité POJO représentant une étiquette (tag).
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class Etiquette {
    private int idEtiquette;
    private String nom;

    public Etiquette() {}

    public Etiquette(String nom) { this.nom = nom; }

    public int getIdEtiquette() { return idEtiquette; }
    public void setIdEtiquette(int idEtiquette) { this.idEtiquette = idEtiquette; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    @Override
    public String toString() { return nom; }
}
