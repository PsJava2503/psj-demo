$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$RootDir = [string](Resolve-Path (Join-Path $PSScriptRoot "..\.."))
$Namespace = "commerce-dev"
$KustomizeDir = Join-Path $RootDir "deploy\k8s"

$InfraDeployments = @("nacos", "redis", "rabbitmq")
$PostgresStatefulSets = @(
    "commerce-user-postgres",
    "commerce-product-postgres",
    "commerce-inventory-postgres",
    "commerce-payment-postgres",
    "commerce-notification-postgres",
    "commerce-address-postgres",
    "commerce-order-postgres"
)
$AppDeployments = @(
    "commerce-gateway",
    "commerce-user-service",
    "commerce-product-service",
    "commerce-inventory-service",
    "commerce-payment-service",
    "commerce-notification-service",
    "commerce-address-service",
    "commerce-order-service",
    "commerce-agent-service"
)

function Require-Command {
    param([string]$Name)

    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "Missing required command: $Name"
    }
}

Require-Command "kubectl"

$Context = kubectl config current-context
Write-Host ">>> kubectl context: $Context"
if ($Context -ne "docker-desktop") {
    Write-Warning "This script is intended for Docker Desktop Kubernetes. Continuing with the current context."
}

Write-Host ">>> Applying Kubernetes resources"
kubectl apply -k $KustomizeDir

Write-Host ">>> Waiting for infrastructure"
foreach ($Deployment in $InfraDeployments) {
    kubectl rollout status "deployment/$Deployment" -n $Namespace --timeout=300s
}

Write-Host ">>> Waiting for PostgreSQL stateful sets"
foreach ($StatefulSet in $PostgresStatefulSets) {
    kubectl rollout status "statefulset/$StatefulSet" -n $Namespace --timeout=300s
}

Write-Host ">>> Waiting for applications"
foreach ($Deployment in $AppDeployments) {
    kubectl rollout status "deployment/$Deployment" -n $Namespace --timeout=600s
}

Write-Host ">>> Current resources"
kubectl get pods,svc -n $Namespace

Write-Host ""
Write-Host ">>> Commerce gateway is available at http://localhost:30080"
