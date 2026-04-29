package com.biblio;

import com.biblio.controller.ReferenceController;
import com.biblio.model.*;
import com.biblio.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la couche DAO.
 * Chaque test utilise une base SQLite propre (biblio.db du dossier courant,
 * recréée au lancement de la classe) afin de rester reproductible.
 *
 * INF 4018 – TN3 – Samuel Buhashe Barhabazi
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReferenceDAOTest {

    private static final ReferenceDAO refDao = new ReferenceDAO();
    private static final AuteurDAO auteurDao = new AuteurDAO();
    private static final EtiquetteDAO etiquetteDao = new EtiquetteDAO();
    private static final ReferenceController controller = new ReferenceController();

    @BeforeAll
    static void nettoyerBaseExistante() throws Exception {
        // On part d'une base SQLite vide pour des tests reproductibles, indépendants
        // de la base de démonstration embarquée dans le JAR.
        File f = new File("biblio.db");
        if (f.exists() && !f.delete())
            throw new IllegalStateException("Impossible de supprimer biblio.db");
        // Création d'un fichier vide : SQLite l'acceptera comme une nouvelle base.
        // Cela neutralise aussi l'extraction automatique de biblio-demo.db.
        Files.createFile(Path.of("biblio.db"));
        DatabaseConnection.getInstance();
    }

    @Test @Order(1)
    void ajouterUnAuteur_creeUnIdentifiant() throws SQLException {
        Auteur a = new Auteur("Tremblay", "Marc");
        auteurDao.ajouter(a);
        assertTrue(a.getIdAuteur() > 0, "L'identifiant doit être généré");
    }

    @Test @Order(2)
    void ajouterEtiquetteEnDoublon_leveUneErreur() throws SQLException {
        etiquetteDao.ajouter(new Etiquette("important"));
        assertThrows(SQLException.class,
                () -> etiquetteDao.ajouter(new Etiquette("important")),
                "La contrainte UNIQUE doit être appliquée");
    }

    @Test @Order(3)
    void ajouterReferenceSansTitre_leveUneValidation() {
        Reference r = new Reference("", "Revue", 2024, "https://x.y", "desc");
        assertThrows(IllegalArgumentException.class,
                () -> controller.ajouterReference(r, List.of(new Auteur("T", "M")), List.of()));
    }

    @Test @Order(4)
    void ajouterReferenceAvecAnneeHorsBornes_leveUneValidation() {
        Reference r = new Reference("Titre", "Revue", 1800, "https://x.y", "desc");
        assertThrows(IllegalArgumentException.class,
                () -> controller.ajouterReference(r, List.of(new Auteur("T", "M")), List.of()));
    }

    @Test @Order(5)
    void ajouterReferenceSansAuteur_leveUneValidation() {
        Reference r = new Reference("Titre", "Revue", 2024, "https://x.y", "desc");
        assertThrows(IllegalArgumentException.class,
                () -> controller.ajouterReference(r, List.of(), List.of()));
    }

    @Test @Order(6)
    void parcoursComplet_ajouterModifierRechercherSupprimer() throws SQLException {
        // Préparer un auteur et une étiquette
        Auteur a = new Auteur("Lavoie", "Julie"); auteurDao.ajouter(a);
        Etiquette e = new Etiquette("mvc"); etiquetteDao.ajouter(e);

        // Ajouter une référence
        Reference r = new Reference("Test MVC", "Revue", 2024, "https://ex.com", "desc");
        controller.ajouterReference(r, List.of(a), List.of(e));
        assertTrue(r.getIdReference() > 0);

        // Recherche par étiquette "mvc"
        List<Reference> trouvees = controller.rechercherParEtiquette("mvc");
        assertEquals(1, trouvees.size());
        assertEquals("Test MVC", trouvees.get(0).getTitre());

        // Modification
        r.setTitre("Test MVC (v2)");
        controller.modifierReference(r);
        assertEquals("Test MVC (v2)",
                controller.listerReferences().stream()
                        .filter(x -> x.getIdReference() == r.getIdReference())
                        .findFirst().orElseThrow().getTitre());

        // Suppression : cascade sur les associations
        controller.supprimerReference(r.getIdReference());
        assertTrue(controller.rechercherParEtiquette("mvc").isEmpty());
    }

    @Test @Order(7)
    void rechercheInsensibleALaCasse() throws SQLException {
        etiquetteDao.ajouter(new Etiquette("Python"));
        Auteur a = new Auteur("Nguyen", "Kim"); auteurDao.ajouter(a);
        Reference r = new Reference("Livre Py", "R", 2023, null, null);
        controller.ajouterReference(r, List.of(a),
                controller.listerEtiquettes().stream()
                        .filter(x -> x.getNom().equalsIgnoreCase("Python"))
                        .toList());
        assertEquals(1, controller.rechercherParEtiquette("PYTHON").size());
        assertEquals(1, controller.rechercherParEtiquette("python").size());
    }
}
