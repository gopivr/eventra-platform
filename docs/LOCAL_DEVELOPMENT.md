# Local Development

Start infrastructure:

```sh
docker compose up -d postgres redis kafka kafka-ui keycloak
```

Build and test:

```sh
mvn verify
```

The root migration master is `database-migrations/src/main/resources/db/db.changelog-master.yaml`. It will provision five service schemas; service-owned changelogs will be added under each service in Phase 2. Reset only local data with `docker compose down -v`.

Run the complete containerized platform with `docker compose up --build -d`. The imported `eventra` Keycloak realm exposes the local public client `eventra-cli`; create local users through the Keycloak admin console and assign realm roles as needed. JWTs are validated at the gateway and again by each service, while the incoming bearer token is preserved on proxied requests.

After the stack is healthy, run `scripts/e2e-smoke.sh`. Set `EVENTRA_ACCESS_TOKEN` to additionally verify an authenticated profile request. OpenAPI validation and repeatable root migration validation run as part of `mvn verify`; Docker-backed checks automatically skip when Docker is unavailable.

Default local ports are gateway 8080, identity 8081, catalog 8082, booking 8083, payment 8084, engagement 8085, Kafka UI 8090, Keycloak 8180, PostgreSQL 5432, and Redis 6379.
