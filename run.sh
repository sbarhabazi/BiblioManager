#!/usr/bin/env bash
# BiblioManager - Script de lancement (Linux / macOS)
set -e
cd "$(dirname "$0")"

JAR="target/BiblioManager.jar"
if [ ! -f "$JAR" ]; then
  echo "Erreur : $JAR introuvable. Lancez d'abord ./install.sh"
  exit 1
fi

exec java -jar "$JAR"
