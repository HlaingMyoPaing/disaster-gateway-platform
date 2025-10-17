
#!/usr/bin/env bash
set -euo pipefail
REALM="${1:-camel-realm}"
BASE="${2:-http://host.docker.internal:8080}"
USER="${3:-admin1@example.com}"
PASS="${4:-admin123}"
CID="${5:-camel-api}"

curl -s -X POST "$BASE/realms/$REALM/protocol/openid-connect/token"   -d "client_id=$CID" -d "grant_type=password"   -d "username=$USER" -d "password=$PASS" | jq -r .access_token

read -p "Copy token and Press [Enter] to exit..."