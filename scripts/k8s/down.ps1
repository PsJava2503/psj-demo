$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$Namespace = "commerce-dev"

if (-not (Get-Command "kubectl" -ErrorAction SilentlyContinue)) {
    throw "Missing required command: kubectl"
}

Write-Host ">>> Deleting namespace $Namespace"
kubectl delete namespace $Namespace --ignore-not-found
