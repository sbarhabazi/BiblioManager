# BiblioManager - Installeur tout-en-un pour Windows
# Usage : iex (irm https://raw.githubusercontent.com/sbarhabazi/BiblioManager/main/install.ps1)
#
# Ce script :
#   1. Verifie la presence de Java 17 ou plus recent
#   2. Si absent, l'installe automatiquement via winget (Eclipse Temurin 17)
#   3. Recharge le PATH dans la session courante (pas besoin de fermer PowerShell)
#   4. Telecharge BiblioManager.jar dans le dossier courant
#   5. Lance l'application

$ErrorActionPreference = 'Stop'

function Test-JavaVersion {
    try {
        $output = (& java -version) 2>&1 | Out-String
        if ($output -match 'version "?(\d+)') {
            return [int]$matches[1]
        }
    } catch {}
    return 0
}

function Update-PathFromRegistry {
    $env:Path = [System.Environment]::GetEnvironmentVariable('Path','Machine') + ';' +
                [System.Environment]::GetEnvironmentVariable('Path','User')
}

Write-Host ''
Write-Host '=== BiblioManager - Installeur Windows ===' -ForegroundColor Cyan
Write-Host ''

# 1. Verification de Java
Write-Host '[1/4] Verification de Java...' -ForegroundColor Cyan
$javaMajor = Test-JavaVersion

if ($javaMajor -ge 17) {
    Write-Host ('Java ' + $javaMajor + ' detecte.') -ForegroundColor Green
} else {
    if ($javaMajor -gt 0) {
        Write-Host ('Java ' + $javaMajor + ' detecte (mais version 17+ requise).') -ForegroundColor Yellow
    } else {
        Write-Host 'Java non detecte.' -ForegroundColor Yellow
    }

    # 2. Installation via winget
    Write-Host ''
    Write-Host '[2/4] Installation de Java 17 (Eclipse Temurin) via winget...' -ForegroundColor Cyan

    if (-not (Get-Command winget -ErrorAction SilentlyContinue)) {
        Write-Host ''
        Write-Host 'Erreur : winget n''est pas disponible sur ce systeme.' -ForegroundColor Red
        Write-Host 'Installez Java 17 manuellement depuis https://adoptium.net' -ForegroundColor Yellow
        Write-Host 'puis relancez ce script.' -ForegroundColor Yellow
        exit 1
    }

    & winget install --silent --accept-package-agreements --accept-source-agreements --id EclipseAdoptium.Temurin.17.JDK
    if ($LASTEXITCODE -ne 0) {
        Write-Host ''
        Write-Host 'Erreur : l''installation de Java via winget a echoue.' -ForegroundColor Red
        Write-Host 'Installez Java 17 manuellement depuis https://adoptium.net' -ForegroundColor Yellow
        exit 1
    }

    # Recharger le PATH dans la session courante
    Update-PathFromRegistry

    # Si java reste introuvable, ajouter manuellement le dossier d'installation
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        $temurinRoot = 'C:\Program Files\Eclipse Adoptium'
        if (Test-Path $temurinRoot) {
            $javaExe = Get-ChildItem -Path $temurinRoot -Filter 'java.exe' -Recurse -ErrorAction SilentlyContinue |
                       Select-Object -First 1
            if ($javaExe) {
                $env:Path = $javaExe.Directory.FullName + ';' + $env:Path
            }
        }
    }

    $javaMajor = Test-JavaVersion
    if ($javaMajor -ge 17) {
        Write-Host ('Java ' + $javaMajor + ' installe et active dans cette session.') -ForegroundColor Green
    } else {
        Write-Host ''
        Write-Host 'Java a ete installe mais n''est pas encore reconnu dans cette session.' -ForegroundColor Yellow
        Write-Host 'Fermez puis rouvrez PowerShell, puis relancez ce script.' -ForegroundColor Yellow
        exit 1
    }
}

# 3. Telechargement du jar
Write-Host ''
Write-Host '[3/4] Telechargement de BiblioManager.jar...' -ForegroundColor Cyan
$jarUrl = 'https://github.com/sbarhabazi/BiblioManager/releases/latest/download/BiblioManager.jar'
$jarPath = Join-Path (Get-Location) 'BiblioManager.jar'
$ProgressPreference = 'SilentlyContinue'  # accelere Invoke-WebRequest
Invoke-WebRequest -Uri $jarUrl -OutFile $jarPath -UseBasicParsing
$ProgressPreference = 'Continue'

if (-not (Test-Path $jarPath)) {
    Write-Host 'Erreur : le telechargement a echoue.' -ForegroundColor Red
    exit 1
}
$sizeMb = [math]::Round((Get-Item $jarPath).Length / 1MB, 2)
Write-Host ('Telecharge : ' + $jarPath + ' (' + $sizeMb + ' Mo)') -ForegroundColor Green

# 4. Lancement
Write-Host ''
Write-Host '[4/4] Lancement de l''application...' -ForegroundColor Cyan
Start-Process -FilePath 'java' -ArgumentList '-jar', $jarPath -WorkingDirectory (Get-Location)
Write-Host ''
Write-Host 'BiblioManager est lance. La fenetre principale doit s''afficher dans quelques secondes.' -ForegroundColor Green
Write-Host ('La base de donnees personnelle sera creee dans : ' + (Get-Location).Path) -ForegroundColor DarkGray
Write-Host ''
Write-Host 'Pour relancer plus tard : java -jar BiblioManager.jar' -ForegroundColor DarkGray
Write-Host ''
