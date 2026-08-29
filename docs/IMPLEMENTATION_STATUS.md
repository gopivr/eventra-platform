# Implementation Status

Updated: 2026-08-29

## Completed

- Phase 1: Maven parent reactor targeting Java 21 and Spring Boot 3.4.13.
- Phase 1: Independent modules for gateway, identity, catalog, booking, payment, engagement, common technical utilities, and migration orchestration.
- Phase 1: Shared JSON problem response and validation exception mapping in `common-platform`.
- Phase 1: Local Docker Compose definitions for PostgreSQL, Redis, Kafka, Kafka UI, and Keycloak without production credentials.
- Phase 1: Configurable root Liquibase schema-provisioning master layout.
- Phase 2: Service-owned Liquibase masters with explicit `include` ordering for release files.
- Phase 2: Identity schema covering accounts, identities, roles, permissions, organizations, memberships, addresses, preferences, verification references, bank references, and audit events.
- Phase 2: Catalog schema covering events, locations, venues, amenities, providers, services, packages, portfolio media, reviews, and flexible attributes.
- Phase 2: Booking schema covering ticket products/prices, seat maps, inventory allocation, holds, orders, tickets, credentials, check-ins, invitations, service requests, quotes, bookings, availability, cancellations, and status history.
- Phase 2: Payment schema covering intents, attempts, tokenized payment methods, coupons, adjustments, refunds, double-entry ledger tables, settlements, payouts, webhooks, and reconciliation runs.
- Phase 2: Engagement schema covering notifications, delivery attempts, preferences, saved items, support cases/messages, disputes, status history, and evidence references.
- Phase 2: Schema-qualified constraints and query indexes, including active-seat uniqueness, money/rating/time checks, and stable reference data.
- Phase 2: Transactional outbox/inbox tables in every service with JSONB payloads and idempotency primary keys.
- Phase 2 complete: Docker-enabled CI runs the repeatable root PostgreSQL migration test and verifies every table, index, and named constraint declared by the service changelogs.
- Phase 3 slice: Identity JPA mappings and repositories for user accounts, preferences, and organizations.
- Phase 3 slice: Identity APIs for current profile, profile update, preferences, and organization creation with JWT authentication and server-side subject mapping.
- Phase 3 slice: Catalog event aggregate, repository, lifecycle actions, and public discovery filtering published/public events.
- Phase 3 slice: Catalog APIs for event creation, retrieval, update, publication, cancellation, with timezone and time-range validation.
- Phase 3 slice: OpenAPI 3.1 documents for the implemented Identity and Catalog endpoints.
- Phase 3 slice: Unit tests for normalized profile handling and explicit event lifecycle invariants.
- Phase 3 completion slice: Identity membership, admin-gated role assignment, and verification-case submission/status APIs.
- Phase 3 completion slice: Catalog venue/provider/service/package boundary, review submission, moderation state, and provider reply APIs.
- Phase 3 completion slice: Identity address creation/listing and verification document-reference submission with ownership checks.
- Phase 3 completion slice: Catalog event media and provider portfolio object-storage references, review moderation transitions, and transactional event outbox persistence.
- Phase 3 completion: Catalog media/portfolio retrieval, review moderation history migration/API, and outbox emission for event and management transitions.
- Phase 3 complete: Time-limited HMAC-signed upload/download URLs for verification documents, event media, and provider portfolios, with namespace-scoped storage keys.
- Phase 3 complete: JWT organization ownership enforcement for catalog mutations, authenticated review authorship, verification-case document ownership checks, and security/ownership/signing tests.
- Phase 4 slice: Durable seat-hold items, Redis TTL release, order retrieval, payment-gated ticket issuance, idempotent check-in persistence, and Booking `PaymentCapturedV1` consumer boundary.
- Phase 5 slice: Idempotent refunds, webhook event deduplication with timestamp/signature input validation, and durable Payment capture outbox records.
- Phase 6 slice: Saved items, support cases, disputes, inbox-deduplicated notifications, and durable Booking `TicketIssuedV1` outbox records.
- Phase 7 slice: Spring Cloud Gateway routes for all business services with baseline correlation and security response headers.
- OpenAPI 3.1 contracts cover all five business services and their currently implemented endpoints.
- Phase 4 slice: Durable seat-hold items, idempotent hold release, order retrieval, payment-gated ticket issuance, and idempotent check-in persistence.
- Phase 5 slice: Refund commands with idempotency and webhook event deduplication with timestamp/signature input validation.
- Phase 6 slice: Saved items, support cases, disputes, inbox-deduplicated notifications, and Booking/Engagement `PaymentCapturedV1` consumer boundaries.
- Phase 7 slice: Spring Cloud Gateway routes for all business services with correlation and baseline security response headers.
- Migration foundation: Root orchestration now packages the authoritative service changelogs into the migration artifact without duplicating source ownership.
- Migration foundation: Added a PostgreSQL Testcontainers repeatability test that applies the root changelog twice and checks service schemas.
- Booking completion slice: durable hold-item mapping, idempotent release, order retrieval, payment-gated issuance, check-in persistence, and ticket/order outbox records.
- Payment completion slice: refund idempotency, webhook timestamp plus HMAC verification, webhook deduplication, and capture outbox records with the persisted order reference.
- Engagement completion slice: saved-item, support-case, and dispute APIs with authenticated ownership scoping.
- Phase 4 slice: Booking Redis TTL seat holds with duplicate-seat protection, authoritative order totals, idempotency-key order creation, and idempotent ticket issuance.
- Phase 5 slice: Payment minor-unit quotes, idempotent payment intents, explicit capture state, and balanced double-entry journal primitives.
- Phase 6 slice: Engagement notification persistence, inbox deduplication, authenticated notification APIs, and Kafka `PaymentCapturedV1` listener boundary.
- Phase 7 slice: Spring Cloud Gateway path routing for all five business services.
- OpenAPI 3.1 contracts are present for all five business services.
- Contract source reviewed: `docs/eventra_api_contracts.pdf`.
- Phase 4 complete: cross-instance atomic Redis seat acquisition with rollback, scheduled Redis/PostgreSQL hold reconciliation, role-gated scanners, order-owner ticket assignment and credential rotation, event-bound hashed credential validation, and concurrency/credential tests.
- Phase 5 complete: configurable provider adapter boundary, per-provider constant-time HMAC webhook verification, provider capture attempts, bounded refunds, balanced/idempotent ledger posting, settlements, idempotent payouts, scheduled/manual provider reconciliation, and payout/reconciliation persistence.
- Phase 6 complete: shared `PaymentCapturedV1` and `TicketIssuedV1` contracts, scheduled Booking/Payment transactional outbox relays, bounded Kafka retry with `.DLQ` recovery, inbox-deduplicated payment/ticket notifications, ownership-scoped support messages and dispute evidence references, and corrected engagement gateway routing.
- Phase 7 complete: reactive gateway JWT enforcement with bearer-header relay, public-route policy, Redis token-bucket rate limiting, correlation/security headers, container images for all services, imported Keycloak realm, internal/external Kafka networking and topic initialization, executable smoke checks, OpenAPI contract validation, and expanded migration metadata assertions.

## Verification

- `mvn -q -DskipTests compile`: passed.
- `mvn -q verify`: passed after Phase 2 schema additions.
- Ruby YAML parse across all 33 Liquibase resources: passed.
- `docker compose config`: passed.
- `mvn -q -pl identity-service,catalog-service -am test`: passed.
- OpenAPI YAML parsing: 2 documents passed.
- OpenAPI YAML parsing: 5 documents passed.
- `mvn -q -pl booking-service,payment-service,engagement-service -am test`: passed.
- `mvn -q verify` after Phases 4-7 extensions: passed.
- `mvn -q verify` after Booking, Payment, Engagement, and gateway extensions: passed.
- Liquibase YAML parsing across 33 resources: passed.
- YAML parsing across 40 OpenAPI/Liquibase resources: passed after the Phase 3 migration addition.
- Packaged migration resource verification: passed, including Catalog 1.1 moderation history.
- Docker Compose configuration validation: passed.
- `mvn -q verify` after migration packaging and Testcontainers test: passed; Docker-backed test skipped because no Docker daemon was available.
- Final `mvn -q verify`: passed after all requested-scope additions.
- Final Compose and OpenAPI validation: passed for 5 contracts.
- Final Liquibase YAML parsing: passed for 33 resources.
- Phase 4 completion verification: full `mvn -q verify` passed; Booking unit/concurrency/credential tests passed and the PostgreSQL+Redis Testcontainers test was skipped because Docker was unavailable.
- Phase 5 completion verification: full `mvn -q verify` passed; signature and payment domain tests passed, with PostgreSQL ledger integration tests skipped because Docker was unavailable.
- Phase 6 completion verification: full `mvn -q verify` passed and 32 affected YAML resources parsed; the PostgreSQL duplicate-delivery integration test was skipped because Docker was unavailable.
- Phase 7 verification: full `mvn -q verify`, gateway context/security tests, OpenAPI validation, 50-resource YAML parsing, smoke-script syntax, and `docker compose config -q` passed. Docker-backed migration and live end-to-end execution remain environment-dependent and skip when Docker is unavailable.
- All five OpenAPI contracts include the current implemented command/query surfaces.
- Phase 2/3 completion verification: affected Maven tests passed locally; the PostgreSQL Testcontainers migration test runs in Docker-enabled CI and skips locally when Docker is unavailable.

## Assumptions and limitations

- PostgreSQL schemas are used for local development; production deployment should use separate databases/users per service.
- Kafka is the event transport because no existing repository transport was present.
- OIDC authentication is delegated to Keycloak locally; services will validate issuer and audience and resolve token subjects to local identity records.
- The PDF remains the compatibility source; the new OpenAPI 3.1 contracts will be authoritative as each service is implemented.
