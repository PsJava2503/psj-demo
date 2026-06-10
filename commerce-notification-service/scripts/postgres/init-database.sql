-- Run with:
-- psql -h localhost -U postgres -d postgres -f commerce-notification-service/scripts/postgres/init-database.sql

SELECT 'CREATE DATABASE commerce_notification'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'commerce_notification')\gexec
