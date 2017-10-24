\set ON_ERROR_STOP on

CREATE USER hamsteri PASSWORD 'hamsteri';

CREATE DATABASE hamsteri WITH OWNER=hamsteri
    encoding   = 'UTF-8'
    LC_CTYPE   = 'en_US.UTF-8'
    LC_COLLATE = 'en_US.UTF-8'
    TEMPLATE template0;
\c hamsteri
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO hamsteri;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO hamsteri;
