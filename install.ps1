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

function Find-JavaInCommonLocations {
    # Cherche java.exe dans les emplacements standards (machine et user scope).
    # Renvoie le dossier "bin" du Java le plus recent trouve, ou $null.
    $roots = @(
        'C:\Program Files\Eclipse Adoptium',
        'C:\Program Files\Java',
        'C:\Program Files (x86)\Java',
        'C:\Program Files (x86)\Eclipse Adoptium',
        (Join-Path $env:LOCALAPPDATA 'Programs\Eclipse Adoptium'),
        (Join-Path $env:LOCALAPPDATA 'Programs\Java')
    )
    $found = @()
    foreach ($root in $roots) {
        if (Test-Path $root) {
            $found += Get-ChildItem -Path $root -Filter 'java.exe' -Recurse -ErrorAction SilentlyContinue |
                      Where-Object { $_.FullName -match '\\bin\\java\.exe$' }
        }
    }
    if ($found.Count -eq 0) { return $null }
    # On retient le binaire au chemin le plus "recent" alphabetiquement
    # (jdk-21 > jdk-17, etc.)
    $best = $found | Sort-Object FullName -Descending | Select-Object -First 1
    return $best.Directory.FullName
}

Write-Host ''
Write-Host '=== BiblioManager - Installeur Windows ===' -ForegroundColor Cyan
Write-Host ''

# 1. Verification initiale de Java
Write-Host '[1/4] Verification de Java...' -ForegroundColor Cyan
Update-PathFromRegistry
$javaMajor = Test-JavaVersion

if ($javaMajor -ge 17) {
    Write-Host ('Java ' + $javaMajor + ' detecte.') -ForegroundColor Green
} else {
    if ($javaMajor -gt 0) {
        Write-Host ('Java ' + $javaMajor + ' detecte mais version 17+ requise.') -ForegroundColor Yellow
    } else {
        Write-Host 'Java non detecte dans le PATH. Recherche dans les emplacements standards...' -ForegroundColor Yellow
    }

    # Avant d'invoquer winget, on cherche si Java est deja sur la machine
    $javaBin = Find-JavaInCommonLocations
    if ($javaBin) {
        Write-Host ('Java trouve dans : ' + $javaBin) -ForegroundColor Green
        $env:Path = $javaBin + ';' + $env:Path
        $javaMajor = Test-JavaVersion
    }

    # Si toujours pas Java 17+, on tente winget
    if ($javaMajor -lt 17) {
        Write-Host ''
        Write-Host '[2/4] Installation de Java 17 (Eclipse Temurin) via winget...' -ForegroundColor Cyan

        if (-not (Get-Command winget -ErrorAction SilentlyContinue)) {
            Write-Host ''
            Write-Host 'Erreur : winget n''est pas disponible sur ce systeme.' -ForegroundColor Red
            Write-Host 'Installez Java 17 manuellement depuis https://adoptium.net' -ForegroundColor Yellow
            exit 1
        }

        # On lance winget mais on n'echoue PAS sur le code retour : winget renvoie
        # un code d'erreur quand le paquet est deja installe, alors que ce n'est
        # pas un probleme. On verifie ensuite la presence reelle de java.exe.
        & winget install --silent --accept-package-agreements --accept-source-agreements --id EclipseAdoptium.Temurin.17.JDK 2>&1 | Out-Host
        $wingetExit = $LASTEXITCODE
        Update-PathFromRegistry

        # Re-scan apres installation
        $javaBin = Find-JavaInCommonLocations
        if ($javaBin) {
            $env:Path = $javaBin + ';' + $env:Path
        }
        $javaMajor = Test-JavaVersion

        if ($javaMajor -lt 17) {
            Write-Host ''
            Write-Host 'Java 17+ reste introuvable apres l''installation.' -ForegroundColor Red
            Write-Host ('Code retour winget : ' + $wingetExit) -ForegroundColor DarkGray
            Write-Host 'Installez Java 17 manuellement depuis https://adoptium.net' -ForegroundColor Yellow
            Write-Host '(en cochant "Set JAVA_HOME variable"), fermez puis rouvrez' -ForegroundColor Yellow
            Write-Host 'PowerShell, puis relancez ce script.' -ForegroundColor Yellow
            exit 1
        }
    }

    Write-Host ('Java ' + $javaMajor + ' actif dans cette session.') -ForegroundColor Green
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
