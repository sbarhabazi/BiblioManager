# BiblioManager

> Outil de bureau pour gérer ses références bibliographiques (titre, revue, année, hyperlien, description), avec **auteurs** et **étiquettes** (tags). Stockage 100 % local dans une base SQLite embarquée.

**Auteur :** Samuel Buhashe Barhabazi
**Cours :** INF 4018 — Projet d'intégration — Université TÉLUQ — Hiver 2026
**Licence :** MIT

---

## Installation en 2 commandes (utilisateur final)

**Prérequis :** Java 17 ou plus récent. ([macOS](https://adoptium.net) · [Windows](https://adoptium.net) · `sudo apt install openjdk-17-jdk` sous Linux). Vérifier avec `java -version`.

```bash
# 1. Télécharger le JAR autonome (depuis la dernière Release)
curl -L -o BiblioManager.jar https://github.com/sbarhabazi/BiblioManager/releases/latest/download/BiblioManager.jar

# 2. Lancer
java -jar BiblioManager.jar
```

L'application s'ouvre avec une **base de démonstration** (3 références, 5 auteurs, 7 étiquettes) déjà chargée. Aucun autre fichier à télécharger, aucune configuration.

> Sous Windows, vous pouvez aussi simplement double-cliquer sur `BiblioManager.jar`.

---

## Installation depuis les sources (développeur)

```bash
git clone https://github.com/sbarhabazi/BiblioManager.git
cd BiblioManager

# Linux / macOS
./install.sh && ./run.sh

# Windows
install.bat && run.bat
```

Les scripts utilisent **Maven Wrapper** (`./mvnw`) — vous n'avez pas besoin d'installer Maven séparément.

---

## Fonctionnalités

- CRUD complet sur les **Références**, les **Auteurs** et les **Étiquettes**
- **Recherche par étiquette** insensible à la casse (`important`, `Important` et `IMPORTANT` donnent le même résultat)
- Validations : titre obligatoire, année entre 1900 et l'année courante, hyperlien `http://` ou `https://`, au moins un auteur par référence
- Intégrité référentielle SQL : `CASCADE` sur la suppression d'une référence, `RESTRICT` sur celle d'un auteur lié
- Interface Swing native, organisée en trois onglets

---

## Architecture

| Couche | Responsabilité | Paquet |
|---|---|---|
| Vue | Interface Swing (3 onglets, boîte de dialogue) | `com.biblio.view` |
| Contrôleur | Validation et orchestration des cas d'usage | `com.biblio.controller` |
| Modèle / DAO | Entités POJO et accès JDBC | `com.biblio.model` |
| Utilitaires | Singleton de connexion SQLite | `com.biblio.util` |

Pile : **Java 17**, **Swing**, **SQLite** (`org.xerial:sqlite-jdbc`), **JUnit 5**, **Maven** (fat-jar via `maven-shade-plugin`).

---

## Documentation

- [Guide d'utilisation détaillé (PDF)](docs/Guide-Utilisation.pdf)
- [Guide d'utilisation détaillé (Word)](docs/Guide-Utilisation.docx)
- [Captures d'écran](docs/screenshots/)
- [Schéma SQL](sql/schema.sql) · [Données de démo](sql/data.sql)

---

## Tests

```bash
./mvnw test
```

7 tests JUnit 5 couvrent : génération d'identifiant, contrainte UNIQUE, validations métier, parcours CRUD complet, recherche insensible à la casse.

---

## Sauvegarde des données

Toutes les données sont stockées dans `biblio.db` créé dans le dossier d'où vous lancez le JAR. Pour sauvegarder, copiez ce fichier. Pour restaurer, remplacez-le.
