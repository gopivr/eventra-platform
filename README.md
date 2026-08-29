# Eventra Platform

Eventra is an event booking platform organized around five bounded services: identity, catalog, booking, payment, and engagement. Each service owns its persistence model and communicates across service boundaries using APIs and versioned domain events.

## Current status

Phase 1 foundation is implemented. The reactor, runnable Spring Boot module boundaries, shared RFC 9457-style error response, and local PostgreSQL/Redis/Kafka/Keycloak infrastructure are in place. Domain persistence and APIs are intentionally tracked in [docs/IMPLEMENTATION_STATUS.md](docs/IMPLEMENTATION_STATUS.md).

## Quick start

Prerequisites: Java 21+, Maven 3.9+, and Docker Compose.

```sh
cp .env.example .env
docker compose up -d postgres redis kafka kafka-ui keycloak
mvn verify
```

The gateway is intended for `http://localhost:8080`; service ports are 8081 through 8085. Kafka UI is at `http://localhost:8090`, and Keycloak is at `http://localhost:8180`. OpenAPI endpoints will be enabled per service as their API slices are implemented.

See [docs/LOCAL_DEVELOPMENT.md](docs/LOCAL_DEVELOPMENT.md) for migration and reset commands.

See [docs/API_DEVELOPER_GUIDE.md](docs/API_DEVELOPER_GUIDE.md) to browse all Swagger UIs, obtain a local access token, and call every service API.
