$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$Namespace = "commerce-dev"

if (-not (Get-Command "kubectl" -ErrorAction SilentlyContinue)) {
    throw "Missing required command: kubectl"
}

Write-Host ">>> Pods"
kubectl get pods -n $Namespace -o wide

Write-Host ""
Write-Host ">>> Services"
kubectl get svc -n $Namespace

Write-Host ""
Write-Host ">>> Persistent volume claims"
kubectl get pvc -n $Namespace

Write-Host ""
Write-Host ">>> Recent events"
kubectl get events -n $Namespace --sort-by=.lastTimestamp
