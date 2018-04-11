mvn liquibase:update
psql -h localhost -U hamsteri -d hamsteri -f init.sql
