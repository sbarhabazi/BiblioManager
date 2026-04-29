@echo off
REM BiblioManager - Script de lancement (Windows)
cd /d "%~dp0"

if not exist "target\BiblioManager.jar" (
  echo Erreur : target\BiblioManager.jar introuvable. Lancez d'abord install.bat
  exit /b 1
)

java -jar "target\BiblioManager.jar"
