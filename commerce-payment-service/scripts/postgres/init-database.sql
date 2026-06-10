-- Run with:
-- psql -h localhost -U postgres -d postgres -f commerce-payment-service/scripts/postgres/init-database.sql

SELECT 'CREATE DATABASE commerce_payment'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'commerce_payment')\gexec
