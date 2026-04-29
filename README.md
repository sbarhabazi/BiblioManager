# BiblioManager

> Outil de bureau pour gérer ses références bibliographiques (titre, revue, année, hyperlien, description), avec **auteurs** et **étiquettes** (tags). Stockage 100 % local dans une base SQLite embarquée.

**Auteur :** Samuel Buhashe Barhabazi
**Cours :** INF 4018 — Projet d'intégration — Université TÉLUQ — Hiver 2026
**Licence :** MIT

---

## Installation (utilisateur final)

### Windows — une seule commande

Ouvrez **PowerShell** (touche Windows → tapez `PowerShell` → Entrée) et collez :

```powershell
iex (irm https://raw.githubusercontent.com/sbarhabazi/BiblioManager/main/install.ps1)
```

Le script vérifie Java, l'installe automatiquement via `winget` si absent, télécharge le `.jar` et lance l'application. **Aucune autre intervention nécessaire.**

### macOS / Linux — deux commandes

**Prérequis :** Java 17 ou plus récent. Vérifier avec `java -version`. Si absent :
- macOS : `brew install --cask temurin@17`
- Linux Debian/Ubuntu : `sudo apt install openjdk-17-jdk`
- Linux Fedora/RHEL : `sudo dnf install java-17-openjdk`

```bash
curl -L -o BiblioManager.jar https://github.com/sbarhabazi/BiblioManager/releases/latest/download/BiblioManager.jar
java -jar BiblioManager.jar
```

### Alternative graphique (tous OS)

Téléchargez `BiblioManager.jar` depuis [la page Release](https://github.com/sbarhabazi/BiblioManager/releases/latest), puis double-cliquez dessus. (Sous macOS, faire clic droit → *Ouvrir* la première fois.)

L'application s'ouvre avec une **base de démonstration** (4 références, 6 auteurs, 7 étiquettes) déjà chargée. Aucun autre fichier à télécharger, aucune configuration.

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
