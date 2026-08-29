# Eventra API developer guide

This guide explains how to run, browse, authenticate to, and call every Eventra HTTP API in the local development environment.

## 1. Start the platform

Requirements: Docker with Compose, Java 21 or newer, and Maven 3.9 or newer.

To run the complete containerized platform:

```bash
docker compose up --build -d
docker compose ps
```

Wait until each application is running, then check it through its health endpoint:

```bash
for port in 8080 8081 8082 8083 8084 8085; do
  curl --fail --silent "http://localhost:${port}/actuator/health"
  echo
done
```

Useful infrastructure UIs:

| Tool | URL | Local credentials |
|---|---|---|
| Keycloak administration | <http://localhost:8180/admin/master/console/> | `admin` / `admin_local_only` |
| Kafka UI | <http://localhost:8090/> | None |

The passwords in this guide are development-only defaults and must not be used outside a local environment.
The local Compose initialization explicitly permits HTTP in Keycloak; production realms must require HTTPS.

## 2. Browse the APIs

Each service publishes live documentation generated from its running controllers. Swagger UI can execute requests and `/v3/api-docs` can be imported into Postman, Insomnia, Bruno, or an OpenAPI client generator.

| Service | Port | Swagger UI | OpenAPI JSON |
|---|---:|---|---|
| Identity | 8081 | <http://localhost:8081/swagger-ui/index.html> | <http://localhost:8081/v3/api-docs> |
| Catalog | 8082 | <http://localhost:8082/swagger-ui/index.html> | <http://localhost:8082/v3/api-docs> |
| Booking | 8083 | <http://localhost:8083/swagger-ui/index.html> | <http://localhost:8083/v3/api-docs> |
| Payment | 8084 | <http://localhost:8084/swagger-ui/index.html> | <http://localhost:8084/v3/api-docs> |
| Engagement | 8085 | <http://localhost:8085/swagger-ui/index.html> | <http://localhost:8085/v3/api-docs> |

The checked-in API contracts are available at `SERVICE/src/main/resources/openapi.yaml`. The live `/v3/api-docs` output is the best source for the routes implemented by the currently running build.

Business requests should normally go through the API Gateway at `http://localhost:8080`. Swagger and OpenAPI documents are accessed directly on the service ports.

## 3. Authentication

Eventra uses the local Keycloak realm `eventra`. Most endpoints require an OAuth 2.0 bearer token. Public event discovery, health checks, API documentation, and payment webhooks are exceptions.

### Create a local user

1. Open <http://localhost:8180/admin/master/console/> and sign in as `admin` / `admin_local_only`. The hostname `keycloak` is internal to Docker and does not work in a host browser.
2. Select the `eventra` realm.
3. Create a user and set a non-temporary password under **Credentials**.
4. If testing privileged booking operations, assign one or more realm roles: `SCANNER`, `EVENT_MANAGER`, `ADMIN`, or `SERVICE`.

### Obtain a token

The local `eventra-cli` client permits the password grant for command-line development:

```bash
export EVENTRA_USERNAME='your-user'
export EVENTRA_PASSWORD='your-password'

export EVENTRA_TOKEN="$(curl --fail --silent \
  --request POST \
  --url http://localhost:8180/realms/eventra/protocol/openid-connect/token \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode client_id=eventra-cli \
  --data-urlencode grant_type=password \
  --data-urlencode username="$EVENTRA_USERNAME" \
  --data-urlencode password="$EVENTRA_PASSWORD" \
  | jq --raw-output '.access_token')"
```

Verify that a token was returned:

```bash
test -n "$EVENTRA_TOKEN" && test "$EVENTRA_TOKEN" != null
```

Pass it to protected APIs:

```bash
curl --fail --silent \
  --header "Authorization: Bearer $EVENTRA_TOKEN" \
  http://localhost:8080/v1/users/me | jq
```

Tokens expire. Run the token command again if a request begins returning `401 Unauthorized`.

## 4. Calling APIs

### Public event discovery

No token is required:

```bash
curl --fail --silent http://localhost:8080/v1/events | jq
curl --fail --silent --get \
  --data-urlencode 'city=New York' \
  http://localhost:8080/v1/events | jq
```

Only events whose status is `PUBLISHED` and visibility is `PUBLIC` are returned.

### Authenticated request

```bash
curl --fail --silent \
  --header "Authorization: Bearer $EVENTRA_TOKEN" \
  http://localhost:8080/v1/notifications | jq
```

### JSON request

Use Swagger UI to inspect the current request schema and then send it through the gateway:

```bash
curl --fail-with-body \
  --request POST \
  --url http://localhost:8080/v1/saved-items \
  --header "Authorization: Bearer $EVENTRA_TOKEN" \
  --header 'Content-Type: application/json' \
  --data '{"itemType":"EVENT","itemId":"00000000-0000-0000-0000-000000000001"}'
```

Use `--fail-with-body` while debugging so curl retains the RFC 9457-style error response.

## 5. Endpoint inventory

Unless marked public, endpoints require `Authorization: Bearer <token>`.

### Identity — port 8081

| Method | Path | Purpose |
|---|---|---|
| GET, PUT | `/v1/users/me` | Read or update the current profile |
| GET, PUT | `/v1/users/me/preferences` | Read or update preferences |
| POST | `/v1/organizations` | Create an organization |
| POST | `/v1/organizations/{organizationId}/members` | Add an organization member |
| POST | `/v1/users/{userId}/roles` | Assign a role |
| GET, POST | `/v1/users/me/verification-cases` | List or submit verification cases |
| GET, POST | `/v1/users/me/addresses` | List or add addresses |
| GET, POST | `/v1/verification-cases/{caseId}/documents` | List or register document references |
| POST | `/v1/verification-cases/{caseId}/document-upload-authorizations` | Authorize a document upload |

### Catalog — port 8082

| Method | Path | Purpose |
|---|---|---|
| GET | `/v1/events`, `/v1/events/{id}` | Discover/read public events (public) |
| POST, PUT | `/v1/events`, `/v1/events/{id}` | Create/update events |
| POST | `/v1/events/{id}/publication` | Publish an event |
| POST | `/v1/events/{id}/cancellations` | Cancel an event |
| GET, POST | `/v1/venues` | Discover or create venues |
| GET, POST | `/v1/providers` | Discover or create providers |
| POST | `/v1/services` | Create a provider service |
| GET | `/v1/providers/{providerId}/services` | List provider services |
| POST | `/v1/packages` | Create a service package |
| GET | `/v1/services/{serviceId}/packages` | List service packages |
| POST | `/v1/reviews` | Submit a review |
| POST | `/v1/reviews/{reviewId}/replies` | Reply to a review |
| POST, GET | `/v1/events/{eventId}/media`, `/v1/events/{eventId}/media-upload-authorizations` | Manage event media |
| POST, GET | `/v1/providers/{providerId}/portfolio`, `/v1/providers/{providerId}/portfolio-upload-authorizations` | Manage portfolios |
| POST, GET | `/v1/reviews/{reviewId}/moderation`, `/v1/reviews/{reviewId}/moderation-history` | Moderate reviews and inspect history |

### Booking — port 8083

| Method | Path | Purpose |
|---|---|---|
| POST, DELETE | `/v1/seat-holds`, `/v1/seat-holds/{id}` | Create or release a seat hold |
| POST, GET | `/v1/ticket-orders`, `/v1/ticket-orders/{id}` | Create or read an order |
| GET | `/v1/ticket-orders/{id}/tickets` | List issued tickets |
| PUT | `/v1/tickets/{id}/assignment` | Assign a ticket |
| POST | `/v1/tickets/{id}/credential-rotations` | Rotate an admission credential |
| POST | `/v1/events/{eventId}/check-ins` | Check in a ticket; requires scanner, event-manager, or admin role |
| POST | `/v1/invitations`, `/v1/invitations/{id}/responses` | Create or respond to invitations |
| POST | `/v1/service-requests` | Create a provider service request |
| POST | `/v1/service-requests/{id}/quotes`, `/v1/service-requests/{id}/quote-selection` | Submit/select quotes |
| POST | `/v1/service-bookings/{id}/acceptance`, `/decline`, `/cancellations` | Process service booking decisions |
| POST | `/v1/ticket-orders/{id}/cancellations` | Cancel a ticket order |
| GET, POST | `/v1/providers/me/availability`, `/v1/providers/me/availability-blocks` | Manage provider availability; call port 8083 directly |
| POST | `/v1/internal/ticket-orders/{id}/issuance` | Internal issuance; requires `SERVICE`; call port 8083 directly |

### Payment — port 8084

| Method | Path | Purpose |
|---|---|---|
| POST | `/v1/pricing/quotes` | Calculate a price quote |
| POST | `/v1/payment-intents` | Create a payment intent |
| POST | `/v1/payment-intents/{id}/capture` | Capture payment |
| POST | `/v1/refunds` | Request a refund |
| POST | `/v1/payments/webhooks/{provider}` | Receive a signed provider webhook (public, signature required) |
| POST | `/v1/ledger/journal-entries` | Post a balanced journal entry |
| POST | `/v1/settlements` | Create a settlement |
| POST | `/v1/settlements/{id}/payouts` | Request a payout |
| POST | `/v1/internal/reconciliation-runs` | Run reconciliation; call port 8084 directly |

### Engagement — port 8085

| Method | Path | Purpose |
|---|---|---|
| GET | `/v1/notifications` | List notifications |
| POST | `/v1/notifications/{id}/read` | Mark a notification read |
| GET, POST | `/v1/saved-items` | List or create saved items |
| DELETE | `/v1/saved-items/{itemType}/{itemId}` | Remove a saved item |
| GET, POST | `/v1/support-cases` | List or create support cases |
| GET, POST | `/v1/support-cases/{id}/messages` | List or add support messages |
| GET, POST | `/v1/disputes` | List or create disputes |
| GET, POST | `/v1/disputes/{id}/evidence` | List or add dispute evidence |

## 6. Gateway routing and direct service access

Use the gateway for normal client traffic:

```text
http://localhost:8080/v1/...
```

Use a service port for Swagger, OpenAPI, health checks, or routes explicitly identified above as internal/direct-only. The gateway applies Redis-backed rate limiting (20 requests/second sustained and a burst capacity of 40 by default). A `429 Too Many Requests` response means that limit was reached.

The gateway validates the token and forwards the original bearer token. Each service validates it again, so a route is accessible only when both gateway and service security policies allow it.

## 7. Common failures

| Response/problem | Likely cause | Resolution |
|---|---|---|
| `401 Unauthorized` | Missing, expired, or wrong-realm token | Obtain a fresh `eventra` token and include the bearer header |
| `403 Forbidden` | Token lacks a required realm role | Assign the appropriate Keycloak role and obtain a new token |
| `404 Not Found` through port 8080 | Route is not exposed by the gateway | Check the inventory and call the owning service directly if marked direct-only |
| `429 Too Many Requests` | Gateway rate limit exceeded | Slow/retry requests or adjust `GATEWAY_RATE_REPLENISH` locally |
| `500` with an application problem body | Service-side failure | Run `docker compose logs --tail=200 <service-name>` |
| Swagger cannot load | Service is not ready or image is stale | Check `/actuator/health`, then rebuild/restart that service |

Useful diagnostics:

```bash
docker compose ps
docker compose logs --tail=200 catalog-service
docker compose restart catalog-service
```

After changing application code or dependencies, rebuild the affected image:

```bash
docker compose up --build -d catalog-service api-gateway
```
