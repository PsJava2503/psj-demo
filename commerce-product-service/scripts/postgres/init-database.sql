-- Run with:
-- psql -h localhost -U postgres -d postgres -f commerce-product-service/scripts/postgres/init-database.sql

SELECT 'CREATE DATABASE commerce_product'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'commerce_product')\gexec
