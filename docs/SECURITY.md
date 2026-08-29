# Security

Services are OAuth 2.1/OIDC resource servers. JWT issuer and audience are configuration, never source-controlled secrets. Method-level authorization combines permissions, multi-role assignments, organization membership, and resource ownership.

Sensitive identity documents and payment credentials are represented by object-storage references and provider tokens only. Logs must omit tokens, document data, and full financial identifiers. Webhook signatures and timestamps are verified before deduplication. Upload APIs will enforce size and content-type allowlists and issue signed object-storage URLs.

Authentication, OTP, coupon, QR, webhook, refund, and payout operations require rate limits and idempotency where applicable. KYC, payment, payout, cancellation, and check-in transitions produce audit events. Dependency and container scanning are part of the final CI phase.
