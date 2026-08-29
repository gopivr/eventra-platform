# Architecture

```mermaid
flowchart LR
  Client --> Gateway[API Gateway]
  Gateway --> Identity[Identity Service]
  Gateway --> Catalog[Catalog Service]
  Gateway --> Booking[Booking Service]
  Gateway --> Payment[Payment Service]
  Gateway --> Engagement[Engagement Service]
  Identity --- I[(identity schema)]
  Catalog --- C[(catalog schema)]
  Booking --- B[(booking schema)]
  Payment --- P[(payment schema)]
  Engagement --- E[(engagement schema)]
  Identity & Catalog & Booking & Payment & Engagement --> Kafka[(Kafka)]
  Booking --> Redis[(Redis holds)]
```

Services use local ACID transactions only. Transactional outbox rows publish versioned events, and inbox rows make consumers idempotent. Cross-service identifiers are opaque values; there are no cross-schema foreign keys or shared domain entities. PostgreSQL full-text projections are the initial search strategy, with an adapter boundary for OpenSearch later.

The primary asynchronous flow is payment capture to booking confirmation and ticket issuance, followed by an engagement notification. Sagas compensate failures such as payment capture without timely ticket issuance through retry, expiry, and refund actions.
