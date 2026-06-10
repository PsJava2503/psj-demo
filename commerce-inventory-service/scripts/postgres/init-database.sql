-- Run with:
-- psql -h localhost -U postgres -d postgres -f commerce-inventory-service/scripts/postgres/init-database.sql

SELECT 'CREATE DATABASE commerce_inventory'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'commerce_inventory')\gexec
