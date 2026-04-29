# BiblioManager - Installeur tout-en-un pour Windows
# Usage : iex (irm 'https://github.com/sbarhabazi/BiblioManager/raw/main/install.ps1')
#
# Ce script :
#   1. Verifie la presence de Java 17 ou plus recent (PATH ou emplacements standards)
#   2. Si absent, l'installe automatiquement via winget (Eclipse Temurin 17)
#   3. Recharge le PATH dans la session courante (pas besoin de fermer PowerShell)
#   4. Telecharge BiblioManager.jar dans le dossier courant
#   5. Lance l'application

$ErrorActionPreference = 'Stop'

function Test-JavaVersion {
    # Invoque java.exe (par defaut depuis le PATH, ou par chemin absolu si fourni)
    # Renvoie le numero de version majeur (ex. 17), ou 0 si introuvable / illisible.
    # NB : java -version ecrit sur stderr ; le 2>&1 doit etre interieur a l'invocation.
    param([string]$JavaCmd = 'java')
    try {
        $output = & $JavaCmd -version 2>&1 | Out-String
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

function Find-JavaExeInCommonLocations {
    # Renvoie le chemin absolu de java.exe le plus recent trouve, ou $null.
    $roots = @(
        'C:\Program Files\Eclipse Adoptium',
        'C:\Program Files\Java',
        'C:\Program Files (x86)\Eclipse Adoptium',
        'C:\Program Files (x86)\Java',
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
    # Trier alphabetiquement decroissant : jdk-21 > jdk-17, etc.
    return ($found | Sort-Object FullName -Descending | Select-Object -First 1).FullName
}

Write-Host ''
Write-Host '=== BiblioManager - Installeur Windows ===' -ForegroundColor Cyan
Write-Host ''

# 1. Verification initiale via le PATH
Write-Host '[1/4] Verification de Java...' -ForegroundColor Cyan
Update-PathFromRegistry
$javaMajor = Test-JavaVersion 'java'
$javaExe = $null

if ($javaMajor -ge 17) {
    Write-Host ('Java ' + $javaMajor + ' deja disponible dans le PATH.') -ForegroundColor Green
    $javaExe = (Get-Command java).Source
} else {
    # 1b. Recherche dans les emplacements standards
    Write-Host 'Java non disponible dans le PATH. Recherche dans les emplacements standards...' -ForegroundColor Yellow
    $candidate = Find-JavaExeInCommonLocations
    if ($candidate) {
        $candidateMajor = Test-JavaVersion $candidate
        if ($candidateMajor -ge 17) {
            $javaExe = $candidate
            $javaMajor = $candidateMajor
            $env:Path = (Split-Path $javaExe) + ';' + $env:Path
            Write-Host ('Java ' + $javaMajor + ' trouve dans : ' + $javaExe) -ForegroundColor Green
        }
    }
}

# 2. Installation via winget si toujours absent
if (-not $javaExe) {
    Write-Host ''
    Write-Host '[2/4] Installation de Java 17 (Eclipse Temurin) via winget (1-2 minutes)...' -ForegroundColor Cyan

    if (-not (Get-Command winget -ErrorAction SilentlyContinue)) {
        Write-Host ''
        Write-Host 'Erreur : winget n''est pas disponible sur ce systeme.' -ForegroundColor Red
        Write-Host 'Installez Java 17 manuellement depuis https://adoptium.net' -ForegroundColor Yellow
        exit 1
    }

    # On capture la sortie de winget pour la reafficher en cas de probleme uniquement.
    # Important : les codes retour winget '-1978335189' (already installed, no upgrade)
    # ou '-1978335212' (no applicable update) ne sont PAS des erreurs ; on tolere tout
    # et on verifie ensuite la presence reelle de java.exe.
    $wingetLog = & winget install --silent --accept-package-agreements --accept-source-agreements --id EclipseAdoptium.Temurin.17.JDK 2>&1 | Out-String
    $wingetExit = $LASTEXITCODE
    Update-PathFromRegistry

    # 2b. Re-scan apres tentative d'installation
    $candidate = Find-JavaExeInCommonLocations
    if ($candidate) {
        $candidateMajor = Test-JavaVersion $candidate
        if ($candidateMajor -ge 17) {
            $javaExe = $candidate
            $javaMajor = $candidateMajor
            $env:Path = (Split-Path $javaExe) + ';' + $env:Path
        }
    }

    if (-not $javaExe) {
        Write-Host ''
        Write-Host 'Java 17+ reste introuvable apres l''installation.' -ForegroundColor Red
        Write-Host ('Code retour winget : ' + $wingetExit) -ForegroundColor DarkGray
        Write-Host '--- Sortie winget ---' -ForegroundColor DarkGray
        Write-Host $wingetLog -ForegroundColor DarkGray
        Write-Host ''
        Write-Host 'Installez Java 17 manuellement depuis https://adoptium.net' -ForegroundColor Yellow
        Write-Host '(en cochant "Set JAVA_HOME variable"), fermez puis rouvrez' -ForegroundColor Yellow
        Write-Host 'PowerShell, puis relancez ce script.' -ForegroundColor Yellow
        exit 1
    }

    Write-Host ('Java ' + $javaMajor + ' installe et active dans cette session.') -ForegroundColor Green
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

# 4. Lancement (par chemin absolu de java.exe pour eviter tout souci de PATH)
Write-Host ''
Write-Host '[4/4] Lancement de l''application...' -ForegroundColor Cyan
Start-Process -FilePath $javaExe -ArgumentList '-jar', $jarPath -WorkingDirectory (Get-Location)
Write-Host ''
Write-Host 'BiblioManager est lance. La fenetre principale doit s''afficher dans quelques secondes.' -ForegroundColor Green
Write-Host ('La base de donnees personnelle sera creee dans : ' + (Get-Location).Path) -ForegroundColor DarkGray
Write-Host ''
Write-Host ('Pour relancer plus tard : ' + $javaExe + ' -jar BiblioManager.jar') -ForegroundColor DarkGray
Write-Host ''
