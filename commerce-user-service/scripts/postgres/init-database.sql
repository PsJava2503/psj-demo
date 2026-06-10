-- Run with:
-- psql -h localhost -U postgres -d postgres -f commerce-user-service/scripts/postgres/init-database.sql

SELECT 'CREATE DATABASE commerce_user'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'commerce_user')\gexec
