-- ============================================
-- DML : Insertion des données de test
-- Projet No 2 – Gestion de références bibliographiques
-- INF 4018 – Samuel Buhashe Barhabazi
-- ============================================

-- Auteurs (collègues de cohorte et auteurs fictifs pour la démo)
INSERT INTO Auteur (nom, prenom) VALUES ('Tremblay', 'Marc');
INSERT INTO Auteur (nom, prenom) VALUES ('Lavoie', 'Julie');
INSERT INTO Auteur (nom, prenom) VALUES ('Nguyen', 'Kim');
INSERT INTO Auteur (nom, prenom) VALUES ('Fournier', 'Pascal');
INSERT INTO Auteur (nom, prenom) VALUES ('Okonkwo', 'Amara');
INSERT INTO Auteur (nom, prenom) VALUES ('Ntang', 'Pierre Marie');

-- Étiquettes
INSERT INTO Etiquette (nom) VALUES ('important');
INSERT INTO Etiquette (nom) VALUES ('apprentissage');
INSERT INTO Etiquette (nom) VALUES ('java');
INSERT INTO Etiquette (nom) VALUES ('python');
INSERT INTO Etiquette (nom) VALUES ('bases de données');
INSERT INTO Etiquette (nom) VALUES ('mvc');
INSERT INTO Etiquette (nom) VALUES ('amusant');

-- Références
INSERT INTO Reference (titre, revue, annee, hyperlien, description)
VALUES ('Introduction à Python pour les nuls', 'Éditions Logibro', 2021,
  'https://example.com/python-nuls',
  'Petit guide accessible pour démarrer avec Python. Bon rappel sur les bases.');

INSERT INTO Reference (titre, revue, annee, hyperlien, description)
VALUES ('Guide pratique des bases de données SQLite', 'Cahier du CIFA', 2022,
  'https://example.com/sqlite-guide',
  'Tutoriel concis sur SQLite pour les projets monoposte. Utile pour le TN3.');

INSERT INTO Reference (titre, revue, annee, hyperlien, description)
VALUES ('Notes de cours sur le patron MVC', 'Blogue personnel', 2023,
  'https://example.com/mvc-notes',
  'Notes perso sur MVC récupérées pendant la révision d''INF 1410.');

INSERT INTO Reference (titre, revue, annee, hyperlien, description)
VALUES ('Conduite de projet logiciel selon le processus RUP : retours d''expérience en formation à distance',
  'Cahiers du génie logiciel académique', 2024,
  'https://www.teluq.ca/',
  'Article de référence pour le cours INF 4018 sur l''application des phases d''analyse, de conception et de réalisation à des projets étudiants.');

-- Associations Reference_Auteur
INSERT INTO Reference_Auteur VALUES (1, 1);  -- Tremblay -> Python pour les nuls
INSERT INTO Reference_Auteur VALUES (2, 2);  -- Lavoie -> SQLite
INSERT INTO Reference_Auteur VALUES (2, 3);  -- Nguyen -> SQLite
INSERT INTO Reference_Auteur VALUES (3, 4);  -- Fournier -> MVC
INSERT INTO Reference_Auteur VALUES (4, 6);  -- Ntang -> RUP en formation à distance

-- Associations Reference_Etiquette
INSERT INTO Reference_Etiquette VALUES (1, 2);  -- Python + apprentissage
INSERT INTO Reference_Etiquette VALUES (1, 4);  -- Python + python
INSERT INTO Reference_Etiquette VALUES (1, 7);  -- Python + amusant
INSERT INTO Reference_Etiquette VALUES (2, 1);  -- SQLite + important
INSERT INTO Reference_Etiquette VALUES (2, 5);  -- SQLite + bases de données
INSERT INTO Reference_Etiquette VALUES (3, 1);  -- MVC + important
INSERT INTO Reference_Etiquette VALUES (3, 6);  -- MVC + mvc
INSERT INTO Reference_Etiquette VALUES (4, 1);  -- RUP + important
INSERT INTO Reference_Etiquette VALUES (4, 2);  -- RUP + apprentissage
INSERT INTO Reference_Etiquette VALUES (4, 6);  -- RUP + mvc

-- Exemple de requête de recherche par étiquette
-- SELECT r.id_reference, r.titre, r.revue, r.annee, r.hyperlien, r.description
-- FROM Reference r
-- INNER JOIN Reference_Etiquette re ON r.id_reference = re.id_reference
-- INNER JOIN Etiquette e ON re.id_etiquette = e.id_etiquette
-- WHERE LOWER(e.nom) = LOWER('important')
-- ORDER BY r.annee DESC;
