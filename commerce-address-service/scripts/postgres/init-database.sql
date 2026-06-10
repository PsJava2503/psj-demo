-- Run with:
-- psql -h localhost -U postgres -d postgres -f commerce-address-service/scripts/postgres/init-database.sql

SELECT 'CREATE DATABASE commerce_address'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'commerce_address')\gexec
