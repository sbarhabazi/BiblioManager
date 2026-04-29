@echo off
REM BiblioManager - Script d'installation (Windows)
REM Compile le projet et produit target\BiblioManager.jar
REM Prerequis : Java 17 ou plus recent.

cd /d "%~dp0"

echo ==^> Verification de Java...
where java >nul 2>nul
if errorlevel 1 (
  echo Erreur : Java n'est pas installe.
  echo Installez Java 17+ depuis https://adoptium.net puis relancez ce script.
  exit /b 1
)
java -version

echo.
echo ==^> Compilation et tests (peut prendre 1-2 minutes au premier lancement)...
call mvnw.cmd -q clean package
if errorlevel 1 exit /b 1

echo.
echo ==^> Installation terminee.
echo Pour lancer l'application : run.bat
