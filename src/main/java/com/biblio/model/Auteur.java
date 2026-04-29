package com.biblio.model;

/**
 * Entité POJO représentant un auteur.
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
public class Auteur {
    private int idAuteur;
    private String nom;
    private String prenom;

    public Auteur() {}

    public Auteur(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public int getIdAuteur() { return idAuteur; }
    public void setIdAuteur(int idAuteur) { this.idAuteur = idAuteur; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    @Override
    public String toString() {
        return (prenom != null ? prenom + " " : "") + nom;
    }
}
