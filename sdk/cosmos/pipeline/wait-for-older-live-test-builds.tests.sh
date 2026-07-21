#!/usr/bin/env bash
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License.
set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
SCRIPT="$HERE/wait-for-older-live-test-builds.sh"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

cat > "$TMP/curl" <<'EOF'
#!/usr/bin/env bash
if [ "${FAKE_CURL_FAIL:-false}" = "true" ]; then
  exit 22
fi
[[ "$*" == *"statusFilter=45"* ]] || exit 23
if [ "${FAKE_HAS_OLDER:-false}" = "true" ]; then
  older_status="${FAKE_OLDER_STATUS:-inProgress}"
  printf '{"value":[{"id":10,"buildNumber":"older","status":"%s"},{"id":20,"buildNumber":"current","status":"inProgress"}]}\n' "$older_status"
else
  echo '{"value":[{"id":20,"buildNumber":"current","status":"inProgress"}]}'
fi
EOF
chmod +x "$TMP/curl"

export PATH="$TMP:$PATH"
export SYSTEM_ACCESSTOKEN=test-token
export SYSTEM_COLLECTIONURI=https://dev.azure.com/example/
export SYSTEM_TEAMPROJECTID=project
export SYSTEM_DEFINITIONID=675
export COSMOS_BUILD_LEASE_DEFINITION_IDS=675,3686
export BUILD_BUILDID=20
export COSMOS_BUILD_LEASE_MAX_POLLS=1
export COSMOS_BUILD_LEASE_POLL_SECONDS=0

FAKE_HAS_OLDER=false "$SCRIPT" >/dev/null

if FAKE_HAS_OLDER=true "$SCRIPT" >/dev/null 2>&1; then
  echo "FAIL: expected an older active build to block acquisition" >&2
  exit 1
fi

if FAKE_HAS_OLDER=true FAKE_OLDER_STATUS=cancelling "$SCRIPT" >/dev/null 2>&1; then
  echo "FAIL: expected an older cancelling build to block acquisition" >&2
  exit 1
fi

if FAKE_HAS_OLDER=true FAKE_OLDER_STATUS=postponed "$SCRIPT" >/dev/null 2>&1; then
  echo "FAIL: expected an older postponed build to block acquisition" >&2
  exit 1
fi

if FAKE_CURL_FAIL=true "$SCRIPT" >/dev/null 2>&1; then
  echo "FAIL: expected an API failure to fail the lease closed" >&2
  exit 1
fi

if COSMOS_BUILD_LEASE_DEFINITION_IDS=3686 "$SCRIPT" >/dev/null 2>&1; then
  echo "FAIL: expected a definition set missing the current definition to fail" >&2
  exit 1
fi

echo "Build lease tests passed."
