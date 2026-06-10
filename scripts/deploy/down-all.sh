#!/usr/bin/env bash
set -euo pipefail

ENV_NAME="${1:-dev}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

case "$ENV_NAME" in
  dev|test|prod) ;;
  *)
    echo "Usage: $0 [dev|test|prod]" >&2
    exit 1
    ;;
esac

down_compose() {
  local module="$1"
  COMPOSE_PROJECT_NAME="psj-${ENV_NAME}-${module##*/}" \
  docker compose -f "${ROOT_DIR}/${module}/deploy/docker-compose.yml" down --remove-orphans
}

down_compose "psj-commerce-gateway"
down_compose "psj-commerce-order-service"
down_compose "psj-commerce-address-service"
down_compose "psj-commerce-notification-service"
down_compose "psj-commerce-payment-service"
down_compose "psj-commerce-inventory-service"
down_compose "psj-commerce-product-service"
down_compose "psj-commerce-user-service"

COMPOSE_PROJECT_NAME="psj-${ENV_NAME}-infra" \
docker compose -f "${ROOT_DIR}/deploy/infra/docker-compose.yml" down --remove-orphans

echo ">>> Stopped environment: ${ENV_NAME}"
