#!/usr/bin/env bash
set -euo pipefail

ENV_NAME="${1:-dev}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

case "$ENV_NAME" in
  dev)
    NACOS_PORT=8848
    REDIS_PORT=6379
    RABBITMQ_PORT=5672
    RABBITMQ_MGMT_PORT=15672
    APP_OFFSET=0
    DB_BASE_PORT=15430
    ;;
  test)
    NACOS_PORT=18848
    REDIS_PORT=16379
    RABBITMQ_PORT=15672
    RABBITMQ_MGMT_PORT=25672
    APP_OFFSET=10000
    DB_BASE_PORT=25430
    ;;
  prod)
    NACOS_PORT=28848
    REDIS_PORT=26379
    RABBITMQ_PORT=25672
    RABBITMQ_MGMT_PORT=35672
    APP_OFFSET=20000
    DB_BASE_PORT=35430
    ;;
  *)
    echo "Usage: $0 [dev|test|prod]" >&2
    exit 1
    ;;
esac

NETWORK_NAME="psj-commerce-${ENV_NAME}-net"
DB_USER="postgres"
DB_PASSWORD="postgres"
NACOS_ADDR="nacos:8848"
REDIS_HOST="redis"
RABBITMQ_HOST="rabbitmq"
RABBITMQ_USER="guest"
RABBITMQ_PASSWORD="guest"

require_command() {
  local cmd="$1"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "Missing required command: ${cmd}" >&2
    exit 1
  fi
}

preflight_check() {
  require_command docker
  if ! docker info >/dev/null 2>&1; then
    echo "Docker daemon is not running. Start Docker Desktop first." >&2
    exit 1
  fi
  if ! docker compose version >/dev/null 2>&1; then
    echo "Docker Compose plugin is not available. Please install/enable docker compose." >&2
    exit 1
  fi
}

require_file() {
  local file="$1"
  local hint="$2"
  if [[ ! -f "$file" ]]; then
    echo "Missing required file: $file" >&2
    echo "$hint" >&2
    exit 1
  fi
}

resolve_app_jar() {
  local module="$1"
  local target_dir="${ROOT_DIR}/${module}/target"
  local jar=""
  local candidate
  for candidate in "${target_dir}"/*.jar; do
    [[ -e "$candidate" ]] || continue
    case "$candidate" in
      *original*|*sources*|*javadoc*) continue ;;
      *) jar="$candidate"; break ;;
    esac
  done
  if [[ -z "$jar" ]]; then
    echo "No runnable jar found for ${module} in ${target_dir}" >&2
    echo "Please package first: ./mvnw -pl ${module} -am -DskipTests package" >&2
    exit 1
  fi
  echo "$jar"
}

run_infra_compose() {
  COMPOSE_PROJECT_NAME="psj-${ENV_NAME}-infra" \
  ENV_NAME="$ENV_NAME" \
  NETWORK_NAME="$NETWORK_NAME" \
  NACOS_PORT="$NACOS_PORT" \
  REDIS_PORT="$REDIS_PORT" \
  RABBITMQ_PORT="$RABBITMQ_PORT" \
  RABBITMQ_MGMT_PORT="$RABBITMQ_MGMT_PORT" \
  docker compose -f "${ROOT_DIR}/deploy/infra/docker-compose.yml" "$@"
}

infra_images_ready() {
  docker image inspect \
    "nacos/nacos-server:v2.4.2" \
    "redis:7.4-alpine" \
    "rabbitmq:3.13-management" >/dev/null 2>&1
}

pull_infra_images() {
  local attempts=3
  local i
  for i in $(seq 1 "$attempts"); do
    if run_infra_compose pull; then
      return 0
    fi
    echo "Infra image pull failed (attempt ${i}/${attempts})." >&2
    sleep 2
  done

  if infra_images_ready; then
    echo "Pull failed, but local infra images are available; continuing." >&2
    return 0
  fi

  echo "Unable to pull infra images and no local cache found." >&2
  echo "Please check Docker Hub connectivity or configure a registry mirror." >&2
  return 1
}

compose_up() {
  local module="$1"
  local app_port="$2"
  local db_name="${3:-}"
  local db_port="${4:-0}"
  local app_jar

  app_jar="$(resolve_app_jar "$module")"
  require_file "${ROOT_DIR}/${module}/deploy/docker-compose.yml" "Expected compose file in module deploy directory."

  COMPOSE_PROJECT_NAME="psj-${ENV_NAME}-${module##*/}" \
  ENV_NAME="$ENV_NAME" \
  NETWORK_NAME="$NETWORK_NAME" \
  NACOS_ADDR="$NACOS_ADDR" \
  REDIS_HOST="$REDIS_HOST" \
  REDIS_PORT="$REDIS_PORT" \
  RABBITMQ_HOST="$RABBITMQ_HOST" \
  RABBITMQ_PORT="$RABBITMQ_PORT" \
  RABBITMQ_USER="$RABBITMQ_USER" \
  RABBITMQ_PASSWORD="$RABBITMQ_PASSWORD" \
  APP_PORT="$app_port" \
  APP_JAR="$app_jar" \
  DB_NAME="$db_name" \
  DB_PORT="$db_port" \
  DB_USER="$DB_USER" \
  DB_PASSWORD="$DB_PASSWORD" \
  docker compose -f "${ROOT_DIR}/${module}/deploy/docker-compose.yml" up -d
}

preflight_check

echo ">>> Pulling infra images (${ENV_NAME})"
pull_infra_images

echo ">>> Starting infra stack (${ENV_NAME})"
run_infra_compose up -d

echo ">>> Starting business services (${ENV_NAME})"
compose_up "psj-commerce-user-service" $((8083 + APP_OFFSET)) "psj_commerce_user" $((DB_BASE_PORT + 1))
compose_up "psj-commerce-product-service" $((8084 + APP_OFFSET)) "psj_commerce_product" $((DB_BASE_PORT + 2))
compose_up "psj-commerce-inventory-service" $((8082 + APP_OFFSET)) "psj_commerce_inventory" $((DB_BASE_PORT + 3))
compose_up "psj-commerce-payment-service" $((8085 + APP_OFFSET)) "psj_commerce_payment" $((DB_BASE_PORT + 4))
compose_up "psj-commerce-notification-service" $((8086 + APP_OFFSET)) "psj_commerce_notification" $((DB_BASE_PORT + 5))
compose_up "psj-commerce-address-service" $((8087 + APP_OFFSET)) "psj_commerce_address" $((DB_BASE_PORT + 6))
compose_up "psj-commerce-order-service" $((8081 + APP_OFFSET)) "psj_commerce_order" $((DB_BASE_PORT + 7))
compose_up "psj-commerce-gateway" $((8080 + APP_OFFSET))

echo ">>> Done. Environment: ${ENV_NAME}"
