# Cosmos live-test account provisioning

`New-CosmosLiveTestAccounts.ps1` provisions the fixed Cosmos DB accounts used by the
**azure-sdk-for-java** Cosmos live tests and outputs the single JSON document
(`cosmos-live-test-accounts`) the pipelines read.

- **`New-CosmosLiveTestAccounts.ps1`** — creates the resource group (if missing) + the
  accounts, and **outputs** the accounts JSON (endpoints + keys). It does not touch Key
  Vault; publish the JSON to the secret manually (see below).

## Why

The Java Cosmos live tests are moving off the central EngSys on-the-fly provisioner to
**fixed, self-owned accounts** (no service-connection / tenant dependency for the
key-based tests). Because the ephemeral tenant is deleted and recreated roughly every
**90 days**, this script is re-run after each rotation to recreate the accounts and
regenerate the fresh endpoints/keys.

## Files

| File | Purpose |
| --- | --- |
| `New-CosmosLiveTestAccounts.ps1` | Creates `sdk-ci` RG (if missing) + accounts; outputs accounts JSON. |
| `cosmos-live-test-accounts.definition.json` | Desired accounts (logical selector + config). |

The emitted JSON conforms to the schema at
`../live-test-accounts.schema.json`, which the pipeline pre-step
`../resolve-cosmos-test-account.sh` parses.

## Prerequisites

- PowerShell 7+
- Az modules: `Az.Accounts`, `Az.Resources`, `Az.CosmosDB`
- Contributor on the subscription that hosts `sdk-ci`
- Signed in: `Connect-AzAccount -Tenant <id> -Subscription <sub>`

## Usage

```powershell
# Create/refresh accounts and write the JSON to a file
# (contains keys - treat as secret, delete after publishing)
./New-CosmosLiveTestAccounts.ps1 -SubscriptionId <sub> -OutputPath ./accounts.json

# Dry run: create nothing, print the assembled JSON with keys stubbed
./New-CosmosLiveTestAccounts.ps1 -SubscriptionId <sub> -WhatIf
```

Idempotent: existing accounts are left in place and missing capabilities are added; the
JSON is regenerated with current endpoints/keys.

Then **update the Key Vault secret / ADO variable manually** with the contents of
`accounts.json` (paste the JSON into the `cosmos-live-test-accounts` secret).

## Rotation runbook

1. Ephemeral tenant is recreated (~every 90 days).
2. `Connect-AzAccount -Tenant <new-tenant-id> -Subscription <sub>`.
3. Run the account script (above) to recreate any missing `sdk-ci` accounts and
   regenerate `accounts.json`.
4. Manually update the `cosmos-live-test-accounts` secret with the new JSON.
5. Java pipelines pick up the refreshed values on their next run — no YAML edits.
