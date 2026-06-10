-- Run with:
-- psql -h localhost -U postgres -d postgres -f psj-commerce-payment-service/scripts/postgres/init-database.sql

SELECT 'CREATE DATABASE psj_commerce_payment'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'psj_commerce_payment')\gexec
