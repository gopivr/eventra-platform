# Domain Model

Identity owns users, organizations, memberships, roles, permissions, verification metadata, and audit history. Catalog owns events, venues, provider services, packages, media, reviews, and search projections. Booking owns inventory, holds, orders, tickets, check-ins, invitations, and vendor bookings. Payment owns pricing, provider interactions, refunds, the immutable double-entry ledger, settlements, and payouts. Engagement owns notifications, preferences, saved items, support, and disputes.

Money is represented as integer minor units plus an ISO-4217 currency. Authoritative prices are calculated by Payment and immutable order snapshots are stored by Booking. User roles are many-to-many, event ownership is an opaque organization ID, and a private event is never included in public search.

The state machines and transitions will be encoded as explicit domain actions, never generic status PATCH endpoints. Holds default to ten minutes and are guarded by Redis atomic operations plus durable PostgreSQL inventory locking. Admission credentials are random and only hashes are stored.
