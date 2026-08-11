param(
    [string]$OutputDirectory = ".signing"
)

$ErrorActionPreference = "Stop"

if (Test-Path $OutputDirectory) {
    throw "Signing directory already exists: $OutputDirectory"
}

$keytoolCommand = Get-Command keytool -ErrorAction SilentlyContinue
$keytool = if ($keytoolCommand) {
    $keytoolCommand.Source
} else {
    "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe"
}
if (-not (Test-Path $keytool)) {
    throw "keytool was not found. Install Android Studio or JDK 17."
}
$fullOutputDirectory = [System.IO.Path]::GetFullPath($OutputDirectory)
[System.IO.Directory]::CreateDirectory($fullOutputDirectory) | Out-Null

function New-RandomSecret {
    $bytes = [System.Security.Cryptography.RandomNumberGenerator]::GetBytes(48)
    return [Convert]::ToBase64String($bytes)
}

$storePassword = New-RandomSecret
$keyPassword = $storePassword
$alias = "autoskip"
$keystorePath = Join-Path $fullOutputDirectory "autoskip-release.jks"

& $keytool -genkeypair -v `
    -keystore $keystorePath `
    -storepass $storePassword `
    -keypass $keyPassword `
    -alias $alias `
    -keyalg RSA `
    -keysize 4096 `
    -validity 10000 `
    -dname "CN=AutoSkip, OU=Release, O=AutoSkip, L=Unknown, C=KZ"

if ($LASTEXITCODE -ne 0) {
    throw "keytool failed with exit code $LASTEXITCODE"
}

$keystoreBytes = [System.IO.File]::ReadAllBytes($keystorePath)
[System.IO.File]::WriteAllText(
    (Join-Path $fullOutputDirectory "keystore.base64"),
    [Convert]::ToBase64String($keystoreBytes)
)
[System.IO.File]::WriteAllText(
    (Join-Path $fullOutputDirectory "keystore-password.txt"),
    $storePassword
)
[System.IO.File]::WriteAllText(
    (Join-Path $fullOutputDirectory "key-password.txt"),
    $keyPassword
)
[System.IO.File]::WriteAllText(
    (Join-Path $fullOutputDirectory "key-alias.txt"),
    $alias
)

Write-Host "Release key created in $fullOutputDirectory"
Write-Host "Back up this directory securely. Losing it prevents future app updates."
