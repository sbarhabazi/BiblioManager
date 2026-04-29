# Documentation utilisateur

Ce dossier contient le guide d'utilisation sous trois formes synchronisées :

| Fichier | Rôle | Source de vérité pour… |
|---|---|---|
| `Guide-Utilisation.md` | Source markdown — contenu textuel intégral | le **texte** (titres, paragraphes, commandes, tableaux) |
| `Guide-Utilisation.docx` | Document Word avec mise en forme finale | le **rendu visuel** (polices, marges, styles) |
| `Guide-Utilisation.pdf` | Export PDF du DOCX | la **livraison à l'utilisateur final** |
| `screenshots/` | Captures d'écran référencées dans le guide | — |

## Chaîne de génération initiale

Le DOCX a été généré à partir du markdown avec :

```bash
pandoc docs/Guide-Utilisation.md \
  -o docs/Guide-Utilisation.docx \
  --resource-path=. \
  --toc --toc-depth=2 \
  -V geometry:margin=2cm
```

Puis le DOCX a été ouvert dans Word et **retravaillé manuellement** pour la mise en forme finale (polices, espacement, table des matières paginée). Le PDF en est l'export direct.

## Règle de synchronisation

À toute **modification textuelle** (correction, ajout, mise à jour de version, etc.) :

1. Modifier `Guide-Utilisation.md`.
2. Reporter la même modification dans `Guide-Utilisation.docx` (manuellement, pour préserver la mise en forme).
3. Ré-exporter `Guide-Utilisation.pdf` depuis Word.
4. Commit groupé des trois fichiers.

Si on régénère le DOCX depuis le markdown via pandoc, **toute mise en forme manuelle est perdue**. Donc cette régénération automatique n'est utilisée qu'au premier jet ou en cas de refonte volontaire du style.
