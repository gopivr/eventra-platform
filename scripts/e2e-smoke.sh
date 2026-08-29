#!/bin/sh
set -eu

gateway_url="${GATEWAY_URL:-http://localhost:8080}"
attempt=0
until curl --fail --silent "$gateway_url/actuator/health" >/dev/null; do
  attempt=$((attempt + 1))
  if [ "$attempt" -ge 60 ]; then
    echo "Gateway did not become healthy" >&2
    exit 1
  fi
  sleep 2
done

public_status=$(curl --silent --output /dev/null --write-out '%{http_code}' "$gateway_url/v1/events")
[ "$public_status" = "200" ] || { echo "Public event discovery returned $public_status" >&2; exit 1; }

protected_status=$(curl --silent --output /dev/null --write-out '%{http_code}' "$gateway_url/v1/users/me")
[ "$protected_status" = "401" ] || { echo "Protected profile returned $protected_status without a token" >&2; exit 1; }

if [ -n "${EVENTRA_ACCESS_TOKEN:-}" ]; then
  curl --fail --silent -H "Authorization: Bearer $EVENTRA_ACCESS_TOKEN" "$gateway_url/v1/users/me" >/dev/null
fi

echo "Eventra gateway smoke checks passed"
