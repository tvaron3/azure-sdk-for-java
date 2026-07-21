#!/usr/bin/env bash
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License.
#
# Serializes runs of the Cosmos live-test pipeline. Build IDs are monotonic, so an active
# build waits while any lower-ID build of the same definition is queued or running.
set -euo pipefail

fail() { echo "ERROR: $*" >&2; exit 1; }

command -v curl >/dev/null 2>&1 || fail "curl is required."
command -v jq >/dev/null 2>&1 || fail "jq is required."

token="${SYSTEM_ACCESSTOKEN:-}"
collection_uri="${SYSTEM_COLLECTIONURI:-}"
project_id="${SYSTEM_TEAMPROJECTID:-}"
definition_id="${SYSTEM_DEFINITIONID:-}"
definition_ids="${COSMOS_BUILD_LEASE_DEFINITION_IDS:-$definition_id}"
build_id="${BUILD_BUILDID:-}"
timeout_seconds="${COSMOS_BUILD_LEASE_TIMEOUT_SECONDS:-18000}"
poll_seconds="${COSMOS_BUILD_LEASE_POLL_SECONDS:-30}"
max_polls="${COSMOS_BUILD_LEASE_MAX_POLLS:-0}"

[ -n "$token" ] || fail "SYSTEM_ACCESSTOKEN is required."
[ -n "$collection_uri" ] || fail "SYSTEM_COLLECTIONURI is required."
[ -n "$project_id" ] || fail "SYSTEM_TEAMPROJECTID is required."
[ -n "$definition_id" ] || fail "SYSTEM_DEFINITIONID is required."
[[ "$definition_ids" =~ ^[0-9]+(,[0-9]+)*$ ]] \
  || fail "COSMOS_BUILD_LEASE_DEFINITION_IDS must be a comma-separated numeric list."
case ",$definition_ids," in
  *",$definition_id,"*) : ;;
  *) fail "COSMOS_BUILD_LEASE_DEFINITION_IDS must include current definition $definition_id." ;;
esac
[[ "$build_id" =~ ^[0-9]+$ ]] || fail "BUILD_BUILDID must be numeric."
[[ "$timeout_seconds" =~ ^[0-9]+$ ]] || fail "COSMOS_BUILD_LEASE_TIMEOUT_SECONDS must be numeric."
[[ "$poll_seconds" =~ ^[0-9]+$ ]] || fail "COSMOS_BUILD_LEASE_POLL_SECONDS must be numeric."
[[ "$max_polls" =~ ^[0-9]+$ ]] || fail "COSMOS_BUILD_LEASE_MAX_POLLS must be numeric."

base_uri="${collection_uri%/}/${project_id}/_apis/build/builds"
deadline=$(( $(date +%s) + timeout_seconds ))
poll_count=0

while true; do
  # BuildStatus is a flags enum:
  # inProgress (1) + cancelling (4) + postponed (8) + notStarted (32) = 45.
  # Query those states atomically so transitions cannot disappear between separate snapshots.
  # Capture curl normally (not through process substitution) so API failures fail the lease closed.
  builds="$(
    curl --fail --silent --show-error --retry 3 \
      -H "Authorization: Bearer $token" \
      "${base_uri}?definitions=${definition_ids}&statusFilter=45&queryOrder=queueTimeAscending&%24top=1000&api-version=7.1"
  )"

  active_count="$(jq '.value | length' <<< "$builds")"
  [ "$active_count" -lt 1000 ] \
    || fail "Active-build query reached its 1000-result limit; refusing to acquire a potentially unsafe lease."

  older_build="$(
    jq -c --argjson current "$build_id" \
      '[.value[] | select(.id < $current)]
       | sort_by(.id)
       | first // empty' \
      <<< "$builds"
  )"

  if [ -z "$older_build" ]; then
    echo "Cosmos live-test build lease acquired by build $build_id."
    exit 0
  fi

  older_id="$(jq -r '.id' <<< "$older_build")"
  older_number="$(jq -r '.buildNumber // .id' <<< "$older_build")"
  older_status="$(jq -r '.status' <<< "$older_build")"
  echo "Waiting for older Cosmos live-test build ${older_number} (${older_id}, ${older_status}) to finish."

  poll_count=$((poll_count + 1))
  if { [ "$max_polls" -gt 0 ] && [ "$poll_count" -ge "$max_polls" ]; } \
    || [ "$(date +%s)" -ge "$deadline" ]; then
    fail "Timed out waiting for older Cosmos live-test build $older_id."
  fi

  sleep "$poll_seconds"
done
