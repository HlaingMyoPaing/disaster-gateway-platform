
#!/usr/bin/env bash
set -euo pipefail
curl -fsS http://localhost:${1:-8088}/actuator/health | jq .


read -p "Press [Enter] to exit..."
