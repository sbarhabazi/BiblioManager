---
title: "Guide d'utilisation — BiblioManager"
author: "Samuel Buhashe Barhabazi"
date: "29 avril 2026"
lang: fr
---

# Guide d'utilisation — BiblioManager

**Application :** BiblioManager v1.0.0 — Outil de gestion de références bibliographiques
**Auteur :** Samuel Buhashe Barhabazi (n° étudiant 25134398, code permanent BARS28329701)
**Cours :** INF 4018 — Projet d'intégration — Université TÉLUQ — Hiver 2026
**Dépôt public :** [https://github.com/sbarhabazi/BiblioManager](https://github.com/sbarhabazi/BiblioManager)
**Téléchargement direct :** [https://github.com/sbarhabazi/BiblioManager/releases/latest](https://github.com/sbarhabazi/BiblioManager/releases/latest)

---

## 1. But de l'application

**BiblioManager** est une application de bureau qui permet à un chercheur, à un enseignant ou à un étudiant de **gérer une bibliothèque personnelle de références bibliographiques** (articles, livres, notes de cours, sites web) directement sur son ordinateur, **sans connexion internet et sans compte**.

Pour chaque référence, l'utilisateur peut enregistrer :

- un **titre** (obligatoire),
- une **revue** ou source,
- une **année** de publication,
- un **hyperlien** vers la version en ligne,
- une **description** libre,
- la liste de ses **auteurs**,
- une ou plusieurs **étiquettes** (tags) thématiques.

L'application offre la **recherche rapide par étiquette**, des validations automatiques (titre obligatoire, année cohérente, hyperlien correct, au moins un auteur) et une **base SQLite intégrée** : aucune installation de serveur de base de données.

L'application est livrée comme **un seul fichier exécutable Java** (`BiblioManager.jar`) qui contient tout le code et les données de démonstration.

---

## 2. Prérequis

Le seul prérequis est **Java 17 ou plus récent** sur votre ordinateur.

Pour vérifier si Java est déjà installé, ouvrez un terminal (ou PowerShell sous Windows) et tapez :

```
java -version
```

Si la commande affiche une version `17.x.x` ou supérieure, vous êtes prêt. Sinon, installez Java en une commande selon votre système :

| Système | Commande d'installation |
|---|---|
| **macOS** | `brew install --cask temurin@17` (ou téléchargement manuel depuis [https://adoptium.net](https://adoptium.net)) |
| **Windows** | `winget install --silent EclipseAdoptium.Temurin.17.JDK` (PowerShell) ou installateur graphique depuis [https://adoptium.net](https://adoptium.net) en cochant « Set JAVA_HOME » |
| **Linux (Debian/Ubuntu)** | `sudo apt install openjdk-17-jdk` |
| **Linux (Fedora/RHEL)** | `sudo dnf install java-17-openjdk` |

> **Important sous Windows :** après l'installation de Java, **fermez puis rouvrez PowerShell** (ou l'invite de commandes), sinon la commande `java` ne sera pas reconnue.

Aucun autre logiciel n'est nécessaire (pas de Maven, pas de SQLite, pas de navigateur).

---

## 3. Installation simple (recommandée)

### 3.1 Windows — une seule commande

Ouvrez **PowerShell** (touche Windows, tapez « PowerShell », Entrée) et **collez la commande suivante**, puis appuyez sur Entrée :

```
iex (irm 'https://github.com/sbarhabazi/BiblioManager/raw/main/install.ps1')
```

Cette commande exécute un petit script d'installation hébergé sur le dépôt GitHub officiel du projet. Le script effectue automatiquement :

1. la vérification de Java 17 sur votre machine ;
2. **si Java est absent**, son installation via le gestionnaire `winget` (intégré à Windows 10 et 11) en utilisant la distribution officielle Eclipse Temurin ;
3. le rechargement du `PATH` dans la session PowerShell courante (vous n'avez **pas besoin** de fermer puis rouvrir la fenêtre) ;
4. le téléchargement du fichier `BiblioManager.jar` (environ 13 Mo) ;
5. le lancement de l'application.

Aucune autre intervention n'est requise. La fenêtre principale s'ouvre dans les secondes qui suivent.

> **Sécurité :** si vous souhaitez inspecter le script avant exécution, ouvrez l'URL `https://raw.githubusercontent.com/sbarhabazi/BiblioManager/main/install.ps1` dans votre navigateur. Le script complet (environ 80 lignes commentées en français) y est consultable.

### 3.2 macOS / Linux — deux commandes

Ouvrez un terminal et tapez (la barre oblique inversée `\` en fin de ligne permet au shell de joindre les deux lignes en une seule commande) :

```
curl -L -o BiblioManager.jar \
  https://github.com/sbarhabazi/BiblioManager/releases/latest/download/BiblioManager.jar
java -jar BiblioManager.jar
```

### 3.3 Alternative pour tous les systèmes : téléchargement par navigateur

Si vous préférez ne pas utiliser de terminal, ouvrez l'adresse suivante dans votre navigateur web :

[https://github.com/sbarhabazi/BiblioManager/releases/latest](https://github.com/sbarhabazi/BiblioManager/releases/latest)

Cliquez sur le fichier **`BiblioManager.jar`** dans la section *Assets* pour le télécharger, puis :

- **Windows :** double-cliquez sur le fichier téléchargé. Si rien ne se passe, ouvrez PowerShell dans le dossier de téléchargement (clic droit dans le dossier en maintenant **Maj** → *Ouvrir dans le terminal*) puis tapez `java -jar BiblioManager.jar`.
- **macOS :** double-cliquez sur le fichier. Si macOS bloque l'ouverture, faites clic droit → *Ouvrir* → confirmer.
- **Linux :** dans un terminal, `cd` jusqu'au dossier de téléchargement puis `java -jar BiblioManager.jar`.

### 3.4 Que se passe-t-il au premier lancement ?

La fenêtre principale s'ouvre **immédiatement** avec une base de démonstration déjà préchargée (4 références, 6 auteurs, 7 étiquettes). Au tout premier lancement, l'application crée un fichier `biblio.db` dans le dossier courant : c'est votre base de données personnelle. Lors des lancements suivants, elle réutilise ce même fichier.

---

## 4. Installation depuis les sources (pour développeur)

Si vous souhaitez compiler vous-même le projet ou lire le code source :

```
git clone https://github.com/sbarhabazi/BiblioManager.git
cd BiblioManager

# Linux / macOS
./install.sh && ./run.sh

# Windows
install.bat && run.bat
```

Le script `install.sh` (ou `install.bat`) utilise le **Maven Wrapper** inclus dans le projet. Vous n'avez donc **pas besoin d'installer Maven** : seul Java 17+ est requis.

---

## 5. Premier lancement et exemples concrets à tester

À l'ouverture, vous voyez la fenêtre principale organisée en **trois onglets** : `Références`, `Auteurs`, `Étiquettes`.

![Fenêtre principale au premier lancement](docs/screenshots/01_fenetre_principale.png){width=15cm}

L'onglet `Références` est sélectionné par défaut et affiche les **4 références de démonstration** (titres masqués ici, à découvrir dans l'application).

### 5.1 Tester la recherche par étiquette

Dans la liste déroulante en haut, sélectionnez l'étiquette `important` puis cliquez sur **Filtrer**.

Le tableau ne montre plus que les **3 références** marquées comme importantes. Cliquez sur **Réinitialiser** pour revoir toutes les références.

![Filtre actif sur l'étiquette « important »](docs/screenshots/03_filtre_important.png){width=15cm}

> Astuce : la recherche est **insensible à la casse**. `IMPORTANT`, `Important` et `important` donnent le même résultat.

### 5.2 Tester l'ajout d'une référence

1. Cliquez sur le bouton **Ajouter** en bas à droite.
2. Dans la boîte de dialogue, remplissez :
   - **Titre :** `Test installation BiblioManager`
   - **Revue :** `Manuel de validation`
   - **Année :** `2026`
   - **Hyperlien :** `https://github.com/sbarhabazi/BiblioManager`
   - **Description :** `Référence créée pour valider l'installation.`
3. Dans la liste **Auteurs**, sélectionnez `Tremblay Marc` (Cmd+clic / Ctrl+clic pour en sélectionner plusieurs).
4. Dans la liste **Étiquettes**, sélectionnez `important` et `apprentissage`.
5. Cliquez sur **Valider**.

La nouvelle référence apparaît immédiatement dans le tableau.

![Boîte de dialogue d'ajout d'une référence](docs/screenshots/02_dialog_ajout.png){width=15cm}

### 5.3 Tester la modification

Sélectionnez la ligne que vous venez d'ajouter, cliquez sur **Modifier**, changez le titre en `Test installation — version 2`, validez. La modification apparaît instantanément.

### 5.4 Tester la suppression

Sélectionnez la même ligne, cliquez sur **Supprimer**, confirmez. La référence et toutes ses associations (auteurs, étiquettes) sont supprimées en cascade.

### 5.5 Tester les validations automatiques

Cliquez sur **Ajouter** et tentez de créer une référence avec :

- un **titre vide** → message « Le titre est obligatoire. »
- une **année à 1800** → message « L'année doit être comprise entre 1900 et 2026. »
- un **hyperlien sans `http`** (exemple : `xyz`) → message d'erreur sur l'hyperlien.
- **aucun auteur sélectionné** → message « Au moins un auteur est obligatoire. »

![Exemple de message de validation](docs/screenshots/04_erreur_validation.png){width=15cm}

### 5.6 Tester les contraintes d'intégrité

Allez dans l'onglet **Auteurs**, sélectionnez `Tremblay Marc` (qui est associé à au moins une référence), cliquez sur **Supprimer**. Vous recevez un message d'erreur car la base SQLite refuse de supprimer un auteur encore lié à une référence (contrainte `RESTRICT`).

![Refus de suppression d'un auteur lié](docs/screenshots/05_erreur_suppression_auteur.png){width=15cm}

Pour pouvoir le supprimer, retirez d'abord toutes ses références (ou modifiez-les pour assigner un autre auteur).

---

## 6. Sauvegarde et restauration

Toutes vos données sont stockées dans le fichier **`biblio.db`** placé **dans le dossier d'où vous lancez** `java -jar BiblioManager.jar`.

- **Sauvegarder :** copiez ce fichier vers un disque externe ou un service de stockage (iCloud, Google Drive, OneDrive, USB).
- **Restaurer :** remplacez le `biblio.db` existant par votre sauvegarde, puis relancez l'application.

Pour repartir d'une **base vierge** (sans aucune donnée) : supprimez `biblio.db`, créez un fichier vide du même nom, puis relancez. Pour retrouver les **données de démonstration**, supprimez simplement `biblio.db` puis relancez : la base de démo embarquée dans le `.jar` sera ré-extraite automatiquement.

---

## 7. Dépannage

| Symptôme | Cause probable | Solution |
|---|---|---|
| `command not found: java` | Java pas installé ou pas dans le `PATH`. | Installez Java 17+ (voir section 2). Sur Windows, redémarrez le terminal après installation. |
| Le double-clic sur `BiblioManager.jar` ne fait rien (Windows) | L'extension `.jar` n'est pas associée à Java. | Ouvrez `cmd.exe` dans le dossier puis tapez `java -jar BiblioManager.jar`. |
| `Error: LinkageError occurred while loading main class` | Vous utilisez Java 8 ou Java 11. | Installez Java **17 ou plus récent**. Vérifiez avec `java -version`. |
| Fenêtre vide ou écran noir au démarrage (Linux) | Pilote graphique exotique. | Lancez avec `java -Dsun.java2d.opengl=false -jar BiblioManager.jar`. |
| Problème d'encodage (caractères accentués) | Locale système non UTF-8. | Lancez avec `java -Dfile.encoding=UTF-8 -jar BiblioManager.jar`. |
| Le tableau reste vide alors qu'aucune référence n'a été ajoutée | Vous lancez le `.jar` sans la base de démo et un `biblio.db` vide existe déjà dans le dossier. | Supprimez `biblio.db` du dossier courant puis relancez : la base de démo embarquée sera ré-extraite. |

---

## 8. Architecture du logiciel (pour information)

| Couche | Responsabilité | Paquet Java |
|---|---|---|
| **Vue** | Interface graphique Swing : 3 onglets, boîte de dialogue d'édition. | `com.biblio.view` |
| **Contrôleur** | Validation métier (titre, année, hyperlien, auteur obligatoire) et orchestration des cas d'usage. | `com.biblio.controller` |
| **Modèle / DAO** | Entités POJO `Reference`, `Auteur`, `Etiquette` et accès SQL via JDBC. | `com.biblio.model` |
| **Utilitaires** | Singleton de connexion SQLite, extraction automatique de la base de démo embarquée. | `com.biblio.util` |

Pile technique : **Java 17**, **Swing**, **SQLite** (`org.xerial:sqlite-jdbc`), **JUnit 5**, **Maven** (fat-jar via `maven-shade-plugin`).
Tous les détails sont disponibles dans le code source : [https://github.com/sbarhabazi/BiblioManager](https://github.com/sbarhabazi/BiblioManager).

---

## 9. Assistance

Pour toute question relative à l'installation, à l'utilisation ou à la maintenance :

- **Auteur :** Samuel Buhashe Barhabazi
- **Courriel :** sbarhabazi@gmail.com
- **Cours :** INF 4018 — Projet d'intégration — Université TÉLUQ — Hiver 2026
- **Code source et suivi des versions :** [https://github.com/sbarhabazi/BiblioManager](https://github.com/sbarhabazi/BiblioManager)
