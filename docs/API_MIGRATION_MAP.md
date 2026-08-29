# API Migration Map

The supplied PDF is the legacy compatibility source. Its endpoint inventory is being mapped as each owning service API is implemented; the target OpenAPI 3.1 documents will be authoritative and will correct client-calculated pricing, single-role identity, generic status mutation, plaintext credentials, and cross-service persistence assumptions.

| Legacy surface                                                   | Target owner       | Decision | Compatibility note                                                                           |
| ---------------------------------------------------------------- | ------------------ | -------- | -------------------------------------------------------------------------------------------- |
| Event, venue, provider discovery and management                  | Catalog Service    | Change   | Public search excludes private/unpublished records and uses opaque IDs.                      |
| User, organization, role, verification and preference operations | Identity Service   | Change   | Multiple simultaneous roles and OIDC subject mapping replace single active-role assumptions. |
| Ticket, hold, order and check-in operations                      | Booking Service    | Change   | Server-side pricing, explicit actions, hashed credentials, and concurrency controls apply.   |
| Payment, refund, wallet, coupon and payout operations            | Payment Service    | Change   | Minor-unit money, idempotency keys, signed webhooks, and immutable ledger apply.             |
| Notifications, saved items, support and disputes                 | Engagement Service | Change   | Asynchronous, idempotent event consumption and object-storage evidence apply.                |

Detailed path-level entries will be added before Phase 7 completion after the PDF endpoint text is normalized against the generated OpenAPI documents.
