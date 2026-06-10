-- Run with:
-- psql -h localhost -U postgres -d postgres -f psj-commerce-address-service/scripts/postgres/init-database.sql

SELECT 'CREATE DATABASE psj_commerce_address'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'psj_commerce_address')\gexec
