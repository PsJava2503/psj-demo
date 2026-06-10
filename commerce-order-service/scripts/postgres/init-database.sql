-- Run with:
-- psql -h localhost -U postgres -d postgres -f commerce-order-service/scripts/postgres/init-database.sql

SELECT 'CREATE DATABASE commerce_order'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'commerce_order')\gexec
