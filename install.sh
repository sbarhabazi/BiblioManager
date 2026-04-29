#!/usr/bin/env bash
# BiblioManager - Script d'installation (Linux / macOS)
# Compile le projet et produit target/BiblioManager.jar
# Prérequis : Java 17 ou plus récent.

set -e
cd "$(dirname "$0")"

echo "==> Vérification de Java..."
if ! command -v java >/dev/null 2>&1; then
  echo "Erreur : Java n'est pas installé."
  echo "Installez Java 17+ depuis https://adoptium.net puis relancez ce script."
  exit 1
fi
java -version

echo
echo "==> Compilation et tests (peut prendre 1-2 minutes au premier lancement)..."
./mvnw -q clean package

echo
echo "==> Installation terminée."
echo "Pour lancer l'application : ./run.sh"
