$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$RootDir = [string](Resolve-Path (Join-Path $PSScriptRoot "..\.."))
$Services = @(
    "commerce-gateway",
    "commerce-user-service",
    "commerce-product-service",
    "commerce-inventory-service",
    "commerce-payment-service",
    "commerce-notification-service",
    "commerce-address-service",
    "commerce-order-service"
)

function Require-Command {
    param([string]$Name)

    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "Missing required command: $Name"
    }
}

function Get-RunnableJar {
    param([string]$Module)

    $TargetDir = Join-Path $RootDir "$Module\target"
    if (-not (Test-Path $TargetDir)) {
        throw "Missing target directory for $Module. Run .\mvnw.cmd -DskipTests package first."
    }

    $Jar = Get-ChildItem -Path $TargetDir -Filter "$Module-*.jar" |
        Where-Object { $_.Name -notmatch "(sources|javadoc)" } |
        Select-Object -First 1

    if (-not $Jar) {
        throw "No runnable jar found for $Module. Run .\mvnw.cmd -pl $Module -am -DskipTests package first."
    }

    return $Jar.FullName
}

Require-Command "docker"

Push-Location $RootDir
try {
    foreach ($Service in $Services) {
        $Jar = Get-RunnableJar $Service
        Write-Host ">>> Building image ${Service}:local from $Jar"
        docker build --build-arg "MODULE=$Service" -t "${Service}:local" .
    }
}
finally {
    Pop-Location
}
